package com.noonlight.apps.data.alarm

import com.squareup.moshi.Json
import java.util.*

data class AlarmCoordinates(
    val lat: Double,
    val lng: Double,
    val accuracy: Int,
    @Json(name = "created_at") val createdAt: Date? = null
)
