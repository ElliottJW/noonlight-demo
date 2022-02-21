package com.noonlight.apps.ui.component.location

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.noonlight.apps.R
import com.noonlight.apps.ui.component.alert.SimpleAlertDialog
import timber.log.Timber

@Composable
fun LocationPermissionsAlertDialog(
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit,
    message: String,
) {
    SimpleAlertDialog(
        title = stringResource(id = R.string.allow_location_permissions),
        message = message,
        positiveText = stringResource(id = android.R.string.ok),
        onPositiveClick = onPositiveClick,
        negativeText = stringResource(id = R.string.deny),
        onNegativeClick = onNegativeClick,
        onDismiss = {
            Timber.i("Request permission dialog dismissed.")
        })
}