package com.noonlight.apps.ui.component.alarm

import androidx.annotation.RestrictTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.noonlight.apps.R
import com.noonlight.apps.ui.state.alarm.AlarmScreenState

@Composable
fun AlarmScreen(
    state: AlarmScreenState,
    onCreateAlarmClicked: () -> Unit,
    onCancelAlarmClicked: () -> Unit
) {
    Surface {
        Box(modifier = Modifier.fillMaxSize()) {
            when (state.status) {
                AlarmScreenState.Status.NEW -> {
                    // Create Alarm button
                    AlarmButton(
                        text = stringResource(id = R.string.create_alarm),
                        color = MaterialTheme.colors.error,
                        onClick = onCreateAlarmClicked
                    )
                }
                AlarmScreenState.Status.LOADING -> {
                    // Loading indicator
                    AlarmLoadingIndicator()
                }
                AlarmScreenState.Status.ARMED -> {
                    // Cancel Alarm button
                    AlarmButton(
                        text = stringResource(id = R.string.cancel_alarm),
                        color = MaterialTheme.colors.error,
                        onClick = onCancelAlarmClicked
                    )
                }
            }
        }
    }
}