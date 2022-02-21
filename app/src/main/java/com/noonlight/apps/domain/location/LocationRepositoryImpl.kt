package com.noonlight.apps.domain.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import timber.log.Timber
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private val _locationUpdates = MutableSharedFlow<LocationWrapper>()
    val locationUpdates: SharedFlow<LocationWrapper> = _locationUpdates

    override fun getCurrentLocationPermissionsStatus(): Map<String, Boolean> {
        val courseGrainPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION)
            .let { PackageManager.PERMISSION_GRANTED == it }
            .also { granted ->
                Timber.i("Course-grained location ${if (granted) "GRANTED" else "DENIED"}.")
            }

        val fineGrainPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION)
            .let { PackageManager.PERMISSION_GRANTED == it }
            .also { granted ->
                Timber.i("Fine-grained location ${if (granted) "GRANTED" else "DENIED"}.")
            }

        return mapOf(
            Manifest.permission.ACCESS_COARSE_LOCATION to courseGrainPermission,
            Manifest.permission.ACCESS_FINE_LOCATION to fineGrainPermission
        )
    }
}