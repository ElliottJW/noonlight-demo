package com.noonlight.apps.domain.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.noonlight.apps.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationRepository {

    private fun Location.toLocationWrapper(): LocationWrapper {
        return LocationWrapper(
            permissions = getLocationPermissionsStatus(),
            accuracy = this.accuracy.toInt(), // Documentation said number; API said integer.
            longitude =  this.longitude,
            latitude = this.latitude
        )
    }

    override fun areCurrentLocationPermissionsGranted(): Boolean {
        val permissions = getLocationPermissionsStatus()
        return permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
    }

    // Still need the lint check because we're using the checkSelfPermission API by proxy.
    @SuppressLint("MissingPermission")
    override fun getLastLocation(onSuccess: (LocationWrapper) -> Unit, onError: () -> Unit) {
        if (!areCurrentLocationPermissionsGranted()) {
            onError()
        } else {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    onSuccess(task.result.toLocationWrapper())
                } else {
                    Timber.e(task.exception, "There was an error getting the last location")
                    onError()
                }
            }
        }
    }

    // Still need the lint check because we're using the checkSelfPermission API by proxy.
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<LocationWrapper> {
        return callbackFlow {
            if (!areCurrentLocationPermissionsGranted()) {
                Timber.w("Permissions have not been granted before accessing location updates.")
                trySend(
                    LocationWrapper(
                        permissions = getLocationPermissionsStatus(),
                        latitude = null,
                        longitude = null,
                        accuracy = null
                    )
                )

                awaitClose { Timber.w("Closing flow without incident.") }
            } else {
                val locationRequest = getLocationRequest()
                val locationCallback = object : LocationCallback() {

                    override fun onLocationResult(result: LocationResult) {
                        Timber.i("New location result")
                        trySend(result.lastLocation.toLocationWrapper())
                    }

                    override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                        if (locationAvailability.isLocationAvailable) {
                            Timber.i("Location is available.")
                        } else {
                            Timber.w("Location is not available.")
                        }
                    }
                }
                fusedLocationProviderClient.setMockMode(BuildConfig.DEBUG)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                ).addOnCompleteListener {
                    Timber.i("Location updates requested.")
                }

                // Remove location updates when stream is closed.
                awaitClose { fusedLocationProviderClient.removeLocationUpdates(locationCallback) }
            }
        }
    }

    override fun getLocationPermissionsStatus(): Map<String, Boolean> {
        val courseGrainPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
            .let { PackageManager.PERMISSION_GRANTED == it }

        val fineGrainPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
            .let { PackageManager.PERMISSION_GRANTED == it }

        return mapOf(
            Manifest.permission.ACCESS_COARSE_LOCATION to courseGrainPermission,
            Manifest.permission.ACCESS_FINE_LOCATION to fineGrainPermission
        )
    }

    override fun checkLocationPermissions(
        onShowRationale: (coarseGranted: Boolean) -> Unit,
        onGranted: () -> Unit
    ) {
        val permissions = getLocationPermissionsStatus()
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            // If we're here, we know that both permissions have been granted, since
            // course must be granted before fine can be.
            onGranted()
        } else {
            // Determine if course permissions were granted, but not fine. We'd prefer
            // to have fine. Otherwise, neither course nor fine-grained permissions were granted.
            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            onShowRationale(coarseGranted)
        }
    }

    private fun getLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(UPDATE_INTERVAL_SECS)
            fastestInterval = TimeUnit.SECONDS.toMillis(FASTEST_UPDATE_INTERVAL_SECS)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    companion object {
        private const val UPDATE_INTERVAL_SECS = 10L
        private const val FASTEST_UPDATE_INTERVAL_SECS = 2L
    }
}