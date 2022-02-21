package com.noonlight.apps.domain.location

interface LocationRepository {

    fun getCurrentLocationPermissionsStatus(): Map<String, Boolean>
}