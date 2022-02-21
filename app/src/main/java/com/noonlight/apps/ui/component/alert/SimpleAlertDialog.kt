package com.noonlight.apps.ui.component.alert

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noonlight.apps.R

@Composable
fun SimpleAlertDialog(
    title: String,
    message: String,
    positiveText: String,
    onPositiveClick: () -> Unit,
    negativeText: String,
    onNegativeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onNegativeClick) {
                    Text(
                        text = negativeText,
                        color = MaterialTheme.colors.error
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(onClick = onPositiveClick) {
                    Text(text = positiveText)
                }
            }
        },
        title = {
            Text(text = title)
            Text(text = stringResource(id = R.string.allow_location_permissions))
        },
        text = {
            Text(text = message)
        }
    )
}