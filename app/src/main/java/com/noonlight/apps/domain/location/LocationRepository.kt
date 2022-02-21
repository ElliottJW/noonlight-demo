package com.noonlight.apps.domain.location

import kotlinx.coroutines.flow.Flow


interface LocationRepository {
    fun getCurrentLocationPermissionsStatus(): Map<String, Boolean>
    fun getLocationUpdates(): Flow<LocationWrapper>
    fun checkLocationPermissions(
        onShowRationale: (coarseGranted: Boolean) -> Unit,
        onGranted: () -> Unit,
        permissions: Map<String, Boolean>,
    )
}