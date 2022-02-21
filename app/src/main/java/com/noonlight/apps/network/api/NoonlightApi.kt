package com.noonlight.apps.network.api

import com.noonlight.apps.data.alarm.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NoonlightApi {

    @POST("/dispatch/v1/alarms")
    suspend fun createAlarm(@Body createAlarmRequest: CreateAlarmRequest): Response<CreateAlarmResponse>

    @POST("/dispatch/v1/alarms/{alarmId}/status")
    suspend fun updateAlarmStatus(
        @Path("alarmId") alarmId: String,
        @Body alarmStatus: AlarmStatus
    ): Response<AlarmStatus>

    @GET("/dispatch/v1/alarms/{alarmId}/status")
    suspend fun getAlarmStatus(
        @Path("alarmId") alarmId: String
    ): Response<AlarmStatus>

    @POST("/dispatch/v1/alarms/{alarmId}/locations")
    suspend fun updateAlarmLocation(
        @Path("alarmId") alarmId: String,
        @Body alarmLocation: AlarmLocation
    ): Response<AlarmLocation>
}