package com.noonlight.apps

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import com.noonlight.apps.ui.component.alarm.AlarmScreen
import com.noonlight.apps.ui.component.location.LocationPermissionsAlertDialog
import com.noonlight.apps.ui.model.alarm.AlarmScreenEvent
import com.noonlight.apps.ui.model.alarm.AlarmScreenViewModel
import com.noonlight.apps.ui.state.alarm.AlarmScreenState
import com.noonlight.apps.ui.theme.NoonlightDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val alarmScreenViewModel: AlarmScreenViewModel by viewModels()
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Timber.i("Got location permission status.")
        alarmScreenViewModel.onLocationPermissionsUpdated(permissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val alarmScreenState by alarmScreenViewModel.state.observeAsState(
                AlarmScreenState(status = AlarmScreenState.Status.NEW)
            )
            val alarmScreenEvent by alarmScreenViewModel.events.collectAsState(initial = AlarmScreenEvent.SolicitLocationPermissions)

            NoonlightDemoTheme {

                // TODO: Future consideration: Add in NavController.

                when (alarmScreenEvent) {
                    AlarmScreenEvent.SolicitLocationPermissions -> {
                        LocationPermissionsAlertDialog(
                            onPositiveClick = {
                                alarmScreenViewModel.onPermissionSolicitationAllowed()
                            },
                            onNegativeClick = {
                                alarmScreenViewModel.onPermissionsSolicitationDenied()
                            },
                            onDismiss = {

                            },
                            message = stringResource(id = R.string.allow_location_permissions_desc),
                        )
                    }
                    AlarmScreenEvent.StartLocationPermissionsRequest -> {
                        locationPermissionRequest.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        )
                    }
                    AlarmScreenEvent.SuggestFineLocationPermissions -> {
                        LocationPermissionsAlertDialog(
                            onPositiveClick = {
                                alarmScreenViewModel.onPermissionSolicitationAllowed()
                            },
                            onNegativeClick = {
                                alarmScreenViewModel.onPermissionsSolicitationDenied()
                            },
                            onDismiss = {

                            },
                            message = stringResource(id = R.string.allow_fine_location_permissions_explanation),
                        )
                    }
                    AlarmScreenEvent.LocationPermissionsDenied -> {
                        Toast.makeText(
                            this,
                            R.string.please_allow_permissions_to_proceed,
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                    null -> Timber.i("Null event not handled.")
                }

                AlarmScreen(
                    state = alarmScreenState,
                    onCreateAlarmClicked = {
                        alarmScreenViewModel.onCreateAlarmClicked()
                    },
                    onCancelAlarmClicked = {
                        alarmScreenViewModel.onCancelAlarmClicked()
                    })
            }
        }
    }
}