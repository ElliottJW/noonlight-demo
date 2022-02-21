package com.noonlight.apps.ui.component.alarm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.noonlight.apps.R
import com.noonlight.apps.ui.state.alarm.AlarmScreenState
import com.noonlight.apps.ui.theme.NoonlightDemoTheme

@Composable
fun AlarmScreen(
    state: AlarmScreenState,
    onCreateAlarmClicked: () -> Unit,
    onCancelAlarmClicked: () -> Unit
) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state.screenStatus) {
                AlarmScreenState.Status.CREATING -> {
                    // Create Alarm button
                    AlarmButton(
                        text = stringResource(id = R.string.create_alarm),
                        color = MaterialTheme.colors.primary,
                        onClick = onCreateAlarmClicked
                    )
                }
                AlarmScreenState.Status.ARMING -> {
                    // Loading indicator
                    AlarmLoadingIndicator(
                        text = stringResource(id = R.string.preparing_alarm)
                    )
                }
                AlarmScreenState.Status.ARMED -> {
                    // Cancel Alarm button
                    AlarmButton(
                        text = stringResource(id = R.string.cancel_alarm),
                        color = MaterialTheme.colors.error,
                        onClick = onCancelAlarmClicked
                    )
                }
                AlarmScreenState.Status.DISARMING -> {
                    // Disarming indicator
                    AlarmLoadingIndicator(
                        text = stringResource(id = R.string.disarming_alarm)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewAlarmScreenNew() {
    NoonlightDemoTheme {
        AlarmScreen(
            state = AlarmScreenState(screenStatus = AlarmScreenState.Status.CREATING),
            onCreateAlarmClicked = { /*TODO*/ },
            onCancelAlarmClicked = {})
    }
}

@Preview
@Composable
fun PreviewAlarmScreenLoading() {
    NoonlightDemoTheme {
        AlarmScreen(
            state = AlarmScreenState(screenStatus = AlarmScreenState.Status.ARMING),
            onCreateAlarmClicked = { /*TODO*/ },
            onCancelAlarmClicked = {})
    }
}

@Preview
@Composable
fun PreviewAlarmScreenArmed() {
    NoonlightDemoTheme {
        AlarmScreen(
            state = AlarmScreenState(screenStatus = AlarmScreenState.Status.ARMED),
            onCreateAlarmClicked = { /*TODO*/ },
            onCancelAlarmClicked = {})
    }
}

@Preview
@Composable
fun PreviewAlarmScreenDisarming() {
    NoonlightDemoTheme {
        AlarmScreen(
            state = AlarmScreenState(screenStatus = AlarmScreenState.Status.DISARMING),
            onCreateAlarmClicked = { /*TODO*/ },
            onCancelAlarmClicked = {})
    }
}