package com.noonlight.apps.ui.component.alarm

import androidx.annotation.RestrictTo
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.noonlight.apps.R
import com.noonlight.apps.ui.theme.NoonlightDemoTheme

object AlarmLoadingIndicatorConstants {
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    const val PROGRESS_TAG = "progress_indicator"
}

@Composable
fun AlarmLoadingIndicator(
    text: String
) {
    Surface {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(modifier = Modifier.testTag(AlarmLoadingIndicatorConstants.PROGRESS_TAG))
            Spacer(modifier = Modifier.width(width = 12.dp))
            Text(text = text)
        }
    }
}

@Preview
@Composable
fun PreviewAlarmLoadingIndicator() {
    NoonlightDemoTheme {
        AlarmLoadingIndicator(stringResource(id = R.string.preparing_alarm))
    }
}

@Preview
@Composable
fun PreviewAlarmDisarmingIndicator() {
    NoonlightDemoTheme {
        AlarmLoadingIndicator(stringResource(id = R.string.disarming_alarm))
    }
}