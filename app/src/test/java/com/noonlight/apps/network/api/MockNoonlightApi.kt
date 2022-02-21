package com.noonlight.apps.network.api

import com.noonlight.apps.data.alarm.*
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response

class MockNoonlightApi : NoonlightApi {

    var mockCreateAlarmResponse: CreateAlarmResponse? = null
    var mockAlarmStatusResponse: AlarmStatus? = null
    var mockAlarmCoordinatesResponse: AlarmLocation? = null

    private val errorResponseBody: ResponseBody
        get() = ResponseBody.create(MediaType.parse("text/plain"), "")

    override suspend fun createAlarm(createAlarmRequest: CreateAlarmRequest): Response<CreateAlarmResponse> {
        return mockCreateAlarmResponse?.let { Response.success(it) } ?: Response.error(500, errorResponseBody)
    }

    override suspend fun updateAlarmStatus(alarmId: String, alarmStatus: AlarmStatus): Response<AlarmStatus> {
        return mockAlarmStatusResponse?.let { Response.success(it) } ?: Response.error(500, errorResponseBody)
    }

    override suspend fun getAlarmStatus(alarmId: String): Response<AlarmStatus> {
        return mockAlarmStatusResponse?.let { Response.success(it) } ?: Response.error(500, errorResponseBody)
    }

    override suspend fun updateAlarmLocation(
        alarmId: String,
        alarmLocation: AlarmLocation
    ): Response<AlarmLocation> {
        return mockAlarmCoordinatesResponse?.let { Response.success(it) } ?: Response.error(500, errorResponseBody)
    }
}