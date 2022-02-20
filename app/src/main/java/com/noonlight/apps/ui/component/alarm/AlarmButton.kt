package com.noonlight.apps.ui.component.alarm

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.noonlight.apps.R
import com.noonlight.apps.ui.theme.NoonlightDemoTheme

@Composable
fun AlarmButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    NoonlightDemoTheme {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = color
            )
        ) {
            Text(
                text = text
            )
        }
    }
}

@Preview
@Composable
private fun CreateAlarmButton() {
    val context = LocalContext.current
    AlarmButton(
        text = context.getString(R.string.create_alarm),
        color = MaterialTheme.colors.error,
        onClick = { /* non - op */ }
    )
}

@Preview
@Composable
private fun CancelAlarmButton() {
    val context = LocalContext.current
    AlarmButton(
        text = context.getString(R.string.create_alarm),
        color = MaterialTheme.colors.primary,
        onClick = { /* non - op */ }
    )
}