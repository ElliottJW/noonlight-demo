package com.noonlight.apps.domain.location

data class LocationWrapper(
    val permissions: Map<String, Boolean>,
    val latitude: Double?,
    val longitude: Double?)