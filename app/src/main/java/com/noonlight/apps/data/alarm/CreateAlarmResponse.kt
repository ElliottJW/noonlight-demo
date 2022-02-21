package com.noonlight.apps.data.alarm

import com.squareup.moshi.Json
import java.util.*

data class CreateAlarmResponse(
    val id: String,
    val status: AlarmStatus.Type,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "owner_id") val ownerId: String
)
