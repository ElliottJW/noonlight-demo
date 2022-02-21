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
        checkLocationPermissions(permissions = permissions)
    }

    /**
     * Android-syntactic method to check location permissions again and whether
     * they've been approved.
     *
     * For the purposes of this demo, we want both fine and course permissions so
     * we can ensure the participant's safety.
     */
    fun onLocationPermissionsUpdated(permissions: Map<String, Boolean>) {
        checkLocationPermissions(permissions = permissions)
    }

    private fun checkLocationPermissions(permissions: Map<String, Boolean>) {
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                // If we're here, we know that both permissions have been granted, since
                // course must be granted before fine can be.
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                // Course permissions were granted, but not fine. We'd prefer to have fine.
                onShowLocationPermissionRationale(false)
            }
            else -> {
                // Neither course nor fine-grained permissions were granted.
                onShowLocationPermissionRationale()
            }
        }
    }

    private fun onShowLocationPermissionRationale(isFineGranted: Boolean = false) {
        viewModelScope.launch {
            _events.emit(if (!isFineGranted) {
                AlarmScreenEvent.SuggestFineLocationPermissions
            } else {
                AlarmScreenEvent.SolicitLocationPermissions
            })
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
    object SuggestFineLocationPermissions: AlarmScreenEvent()
    object StartLocationPermissionsRequest : AlarmScreenEvent()
    object LocationPermissionsDenied : AlarmScreenEvent()
}