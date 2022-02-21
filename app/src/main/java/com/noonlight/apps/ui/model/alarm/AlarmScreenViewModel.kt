package com.noonlight.apps.ui.model.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noonlight.apps.data.alarm.*
import com.noonlight.apps.domain.location.LocationRepository
import com.noonlight.apps.domain.location.LocationWrapper
import com.noonlight.apps.domain.user.UserProvider
import com.noonlight.apps.network.api.NoonlightApi
import com.noonlight.apps.ui.state.alarm.AlarmScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class AlarmScreenViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val noonlightApi: NoonlightApi,
    private val userProvider: UserProvider
) : ViewModel() {

    private val _state = MutableLiveData<AlarmScreenState>()
    val state: LiveData<AlarmScreenState> = _state

    private val _events = MutableSharedFlow<AlarmScreenEvent>(replay = 1)
    val events: SharedFlow<AlarmScreenEvent> = _events

    private var createAlarmRequestPending: AtomicBoolean = AtomicBoolean(false)

    init {
        val permissions = locationRepository.getCurrentLocationPermissionsStatus()
        onLocationPermissionsUpdated(permissions = permissions)
    }

    /**
     * Only request permissions when the user wants to use that app; That gives context for why
     * we're asking for their location.
     */
    fun onCreateAlarmClicked() {
        val permissions = locationRepository.getCurrentLocationPermissionsStatus()
        onLocationPermissionsUpdated(permissions = permissions)

        locationRepository.getLastLocation(onSuccess = { wrapper ->
            if (!wrapper.isValid()) {
                Timber.e("Wrapper is invalid!")
                updateState { old -> old.copy(screenStatus = AlarmScreenState.Status.CREATING) }
            } else {
                createNewAlarm(wrapper = wrapper)
            }
        }, onError = {
            Timber.e("Permissions have not been granted yet.")
            updateState { old -> old.copy(screenStatus = AlarmScreenState.Status.CREATING) }
        })
    }

    private fun createNewAlarm(wrapper: LocationWrapper) {
        updateState { old -> old.copy(screenStatus = AlarmScreenState.Status.ARMING) }
        viewModelScope.launch {
            val request = CreateAlarmRequest(
                name = userProvider.name,
                phone = userProvider.phone,
                pin = userProvider.pin,
                location = AlarmLocation(
                    coordinates = AlarmCoordinates(
                        // !! is not best practice; However, these were verified earlier in the
                        // call.
                        lat = wrapper.latitude!!,
                        lng = wrapper.longitude!!,
                        accuracy = wrapper.accuracy!!
                    )
                )
            )
            val response = noonlightApi.createAlarm(request)
            if (response.isSuccessful) {
                val alarmId = response.body()?.id
                if (alarmId.isNullOrBlank()) {
                    Timber.e("Response was successful, but there was no alarm ID.")
                    updateState { old ->
                        old.copy(
                            screenStatus = AlarmScreenState.Status.CREATING,
                            currentAlarmId = alarmId
                        )
                    }
                } else {
                    updateState { old ->
                        old.copy(
                            screenStatus = AlarmScreenState.Status.ARMED,
                            currentAlarmId = alarmId
                        )
                    }
                }
            } else {
                Timber.e("There was an error creating an alarm: ${response.errorBody()}")
                updateState { old ->
                    old.copy(
                        screenStatus = AlarmScreenState.Status.CREATING,
                        currentAlarmId = null
                    )
                }
            }
        }
    }

    /**
     * Android-syntactic method to check location permissions again and whether
     * they've been approved.
     *
     * For the purposes of this demo, we want both fine and course permissions so
     * we can ensure the participant's safety.
     */
    fun onLocationPermissionsUpdated(permissions: Map<String, Boolean>) {
        locationRepository.checkLocationPermissions(onGranted = {
            if (createAlarmRequestPending.getAndSet(false)) {
                onCreateAlarmClicked()
            }

            // Start listening to location updates.
            viewModelScope.launch {
                locationRepository.getLocationUpdates()
                    .catch { e ->
                        Timber.e(e, "Error while getting the location wrapper!")
                    }
                    .collect { locationWrapper ->
                        val alarmId = _state.value?.currentAlarmId
                        if (!alarmId.isNullOrBlank()) {
                            updateAlarmLocation(
                                alarmId = alarmId,
                                locationWrapper = locationWrapper
                            )
                        }
                    }
            }
        }, onShowRationale = { coarseGranted ->
            viewModelScope.launch {
                _events.emit(
                    if (coarseGranted) {
                        AlarmScreenEvent.SuggestFineLocationPermissions
                    } else {
                        AlarmScreenEvent.SolicitLocationPermissions
                    }
                )
            }
        }, permissions = permissions)
    }

    private suspend fun updateAlarmLocation(alarmId: String, locationWrapper: LocationWrapper) {
        if (!locationWrapper.isValid()) {
            Timber.e("The location wrapper is invalid and we cannot update the location!")
        } else {
            // These were previously verified. !! is still not best practice.
            val lat = locationWrapper.latitude!!
            val lng = locationWrapper.longitude!!
            val accuracy = locationWrapper.accuracy!!

            val response = noonlightApi.updateAlarmLocation(
                alarmId = alarmId,
                alarmLocation = AlarmLocation(
                    coordinates = AlarmCoordinates(
                        lat = lat,
                        lng = lng,
                        accuracy = accuracy
                    )
                )
            )

            _events.emit(if (response.isSuccessful) {
                Timber.i("Location was successfully updated.")
                AlarmScreenEvent.LocationUpdated(locationWrapper = locationWrapper)
            } else {
                Timber.w("There was an error updating the location.")
                AlarmScreenEvent.ErrorUpdatingLocation(Exception(response.errorBody().toString()))
            })
        }
    }

    fun onCancelAlarmClicked() {
        Timber.i("User cancelled their alarm.")
        val alarmId = _state.value?.currentAlarmId ?: kotlin.run {
            Timber.e("There was no current alarm ID")
            return
        }

        updateState { old -> old.copy(screenStatus = AlarmScreenState.Status.DISARMING) }
        viewModelScope.launch {
            val response = noonlightApi.updateAlarmStatus(
                alarmId = alarmId,
                alarmStatus = AlarmStatus(status = AlarmStatus.Type.CANCELED)
            )

            if (response.isSuccessful) {
                Timber.i("Alarm successfully cancelled.")
                updateState { old ->
                    old.copy(
                        screenStatus = AlarmScreenState.Status.CREATING,
                        currentAlarmId = null
                    )
                }
            } else {
                val errorBody = response.errorBody()
                Timber.e("There was an error cancelling the alarm: $errorBody")
                _events.emit(AlarmScreenEvent.ErrorCancellingAlarm(Exception(errorBody.toString())))
            }
        }
    }

    fun onPermissionSolicitationAllowed() {
        Timber.i("Permission solicitation allowed.")
        viewModelScope.launch {
            _events.emit(AlarmScreenEvent.StartLocationPermissionsRequest)
        }

        createAlarmRequestPending.set(true)
    }

    fun onPermissionsSolicitationDenied() {
        Timber.w("Permission solicitation denied.")
        viewModelScope.launch {
            _events.emit(AlarmScreenEvent.LocationPermissionsDenied)
        }

        createAlarmRequestPending.set(false)
    }

    private fun updateState(process: (AlarmScreenState) -> AlarmScreenState) {
        _state.value = process(_state.value ?: AlarmScreenState(AlarmScreenState.Status.CREATING))
    }
}

sealed class AlarmScreenEvent {
    object SolicitLocationPermissions : AlarmScreenEvent()
    object SuggestFineLocationPermissions : AlarmScreenEvent()
    object StartLocationPermissionsRequest : AlarmScreenEvent()
    object LocationPermissionsDenied : AlarmScreenEvent()

    data class LocationUpdated(val locationWrapper: LocationWrapper) : AlarmScreenEvent()
    data class ErrorUpdatingLocation(val throwable: Throwable): AlarmScreenEvent()
    data class ErrorCancellingAlarm(val throwable: Throwable) : AlarmScreenEvent()
}