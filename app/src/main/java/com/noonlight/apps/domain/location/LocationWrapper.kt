package com.noonlight.apps.domain.location

data class LocationWrapper(
    val permissions: Map<String, Boolean>,
    val accuracy: Int?,
    val latitude: Double?,
    val longitude: Double?) {

    fun isValid(): Boolean {
        return accuracy != null && latitude != null && longitude != null
    }
}