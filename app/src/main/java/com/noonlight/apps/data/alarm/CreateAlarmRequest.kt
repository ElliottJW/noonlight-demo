package com.noonlight.apps.data.alarm

data class CreateAlarmRequest(
    val name: String,
    val phone: String,
    val pin: String? = null,
    val location: AlarmLocation
)
