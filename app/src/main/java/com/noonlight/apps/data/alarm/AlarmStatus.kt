package com.noonlight.apps.data.alarm

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

data class AlarmStatus(
    val status: Type,
    val pin: String? = null,
    @Json(name = "created_at") val createdAt: Date? = null
) {
    // Annotated to prevent being obfuscated by R8.
    @JsonClass(generateAdapter = false)
    enum class Type {
        ACTIVE, CANCELED
    }
}
