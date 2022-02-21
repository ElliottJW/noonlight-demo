package com.noonlight.apps.ui.model.alarm

import android.Manifest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noonlight.apps.domain.location.LocationRepository
import com.noonlight.apps.ui.state.alarm.AlarmScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AlarmScreenViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _state = MutableLiveData<AlarmScreenState>()
    val state: LiveData<AlarmScreenState> = _state

    private val _events = MutableSharedFlow<AlarmScreenEvent>()
    val events: SharedFlow<AlarmScreenEvent> = _events

    /**
     * Only request permissions when the user wants to use that app; That gives context for why
     * we're asking for their location.
     */
    fun onCreateAlarmClicked() {
        val permissions = locationRepository.getCurrentLocationPermissionsStatus()
        onLocationPermissionsUpdated(permissions = permissions)
    }

    /**
     * Android-syntactic method to check location permissions again and whether
     * they've been approved.
     *
     * For the purposes of this demo, we want both fine and course permissions so
     * we can ensure the participant's safety.
     */
    fun onLocationPermissionsUpdated(permissions: Map<String, Boolean>) {
        viewModelScope.launch {
            locationRepository.checkLocationPermissions(onGranted = {
                // Start listening to location updates.
                viewModelScope.launch {
                    locationRepository.getLocationUpdates().collect { locationWrapper ->

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

    }

    fun onCancelAlarmClicked() {
        Timber.i("User cancelled their alarm.")
        // TODO: Cancel the alarm via API.
    }

    fun onPermissionSolicitationAllowed() {
        Timber.i("Permission solicitation allowed.")
        viewModelScope.launch {
            _events.emit(AlarmScreenEvent.StartLocationPermissionsRequest)
        }
    }

    fun onPermissionsSolicitationDenied() {
        Timber.w("Permission solicitation denied.")
        viewModelScope.launch {
            _events.emit(AlarmScreenEvent.LocationPermissionsDenied)
        }
    }
}

sealed class AlarmScreenEvent {
    object SolicitLocationPermissions : AlarmScreenEvent()
    object SuggestFineLocationPermissions : AlarmScreenEvent()
    object StartLocationPermissionsRequest : AlarmScreenEvent()
    object LocationPermissionsDenied : AlarmScreenEvent()
}