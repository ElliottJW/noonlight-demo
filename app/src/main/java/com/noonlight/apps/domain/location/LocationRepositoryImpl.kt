package com.noonlight.apps.domain.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationRepository {

    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<LocationWrapper> {
        return callbackFlow {
            val locationRequest = LocationRequest.create().apply {
                interval = TimeUnit.SECONDS.toMillis(UPDATE_INTERVAL_SECS)
                fastestInterval = TimeUnit.SECONDS.toMillis(FASTEST_UPDATE_INTERVAL_SECS)
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    Timber.i("New location result")
                    result.lastLocation.let {
                        trySend(
                            LocationWrapper(
                                latitude = it.latitude,
                                longitude = it.longitude
                            )
                        )
                    }
                }

                override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                    if (locationAvailability.isLocationAvailable) {
                        Timber.i("Location is available.")
                    } else {
                        Timber.w("Location is not available.")
                    }
                }
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            awaitClose { fusedLocationProviderClient.removeLocationUpdates(locationCallback) }
        }
    }

    override fun getCurrentLocationPermissionsStatus(): Map<String, Boolean> {
        val courseGrainPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
            .let { PackageManager.PERMISSION_GRANTED == it }
            .also { granted ->
                Timber.i("Course-grained location ${if (granted) "GRANTED" else "DENIED"}.")
            }

        val fineGrainPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
            .let { PackageManager.PERMISSION_GRANTED == it }
            .also { granted ->
                Timber.i("Fine-grained location ${if (granted) "GRANTED" else "DENIED"}.")
            }

        return mapOf(
            Manifest.permission.ACCESS_COARSE_LOCATION to courseGrainPermission,
            Manifest.permission.ACCESS_FINE_LOCATION to fineGrainPermission
        )
    }

    companion object {
        private const val UPDATE_INTERVAL_SECS = 10L
        private const val FASTEST_UPDATE_INTERVAL_SECS = 2L
    }
}