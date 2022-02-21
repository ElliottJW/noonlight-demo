package com.noonlight.apps.network.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.noonlight.apps.data.alarm.*
import com.noonlight.apps.network.api.NoonlightApi
import com.noonlight.apps.util.TestUtil
import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NoonlightApiTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var underTest: NoonlightApi

    @Before
    fun setUp() {
        underTest = MockNoonlightApi()
    }

    @Test
    fun `Creating an alarm with a returned error is unsuccessful`() {
        val request = CreateAlarmRequest(
            name = "Elliott JW",
            phone = "11001112222",
            location = AlarmLocation(
                coordinates = AlarmCoordinates(
                    lat = 100.0,
                    lng = 101.0,
                    accuracy = 1
                )
            )
        )
        runBlocking {
            assertFalse(underTest.createAlarm(request).isSuccessful)
        }
    }

    @Test
    fun `Creating an alarm with a returned result is successful`() {
        val request = CreateAlarmRequest(
            name = "Elliott JW",
            phone = "11001112222",
            location = AlarmLocation(
                coordinates = AlarmCoordinates(
                    lat = 100.0,
                    lng = 101.0,
                    accuracy = 1
                )
            )
        )
        val response = TestUtil.getTestObjectFromFile(
            "alarm/create_alarm_response_success.json",
            CreateAlarmResponse::class.java)
        (underTest as MockNoonlightApi).mockCreateAlarmResponse = response
        runBlocking {
            assertTrue(underTest.createAlarm(request).isSuccessful)
        }
    }

    @Test
    fun `Updating an alarm with a returned error is unsuccessful`() {
        val alarmId = "test_id"
        val alarmStatus = AlarmStatus(
            status = AlarmStatus.Type.CANCELED
        )
        runBlocking {
            assertFalse(
                underTest.updateAlarmStatus(
                    alarmId = alarmId,
                    alarmStatus = alarmStatus
                ).isSuccessful
            )
        }
    }

    @Test
    fun `Updating an alarm with a returned result is successful`() {
        val alarmId = "test_id"
        val alarmStatus = AlarmStatus(
            status = AlarmStatus.Type.CANCELED
        )
        val response = TestUtil.getTestObjectFromFile(
            "alarm/update_alarm_status_canceled_success.json",
            AlarmStatus::class.java)
        (underTest as MockNoonlightApi).mockAlarmStatusResponse = response
        runBlocking {
            assertTrue(
                underTest.updateAlarmStatus(
                    alarmId = alarmId,
                    alarmStatus = alarmStatus
                ).isSuccessful
            )
        }
    }

    @Test
    fun `Getting an alarm's status with a returned error is unsuccessful`() {
        val alarmId = "test_id"
        runBlocking {
            assertFalse(
                underTest.getAlarmStatus(
                    alarmId = alarmId
                ).isSuccessful
            )
        }
    }

    @Test
    fun `Getting an alarms status is successful`() {
        val alarmId = "test_id"
        val response = TestUtil.getTestObjectFromFile(
            "alarm/get_alarm_status_success.json",
            AlarmStatus::class.java)
        (underTest as MockNoonlightApi).mockAlarmStatusResponse = response
        runBlocking {
            assertTrue(
                underTest.getAlarmStatus(
                    alarmId = alarmId
                ).isSuccessful
            )
        }
    }

    @Test
    fun `Updating alarm location with returned error is unsuccessful`() {
        val alarmId = "test_id"
        val alarmLocation = AlarmLocation(
            coordinates = AlarmCoordinates(
                lat = 100.0,
                lng = 101.0,
                accuracy = 1
            )
        )
        runBlocking {
            assertFalse(
                underTest.updateAlarmLocation(
                    alarmId = alarmId,
                    alarmLocation = alarmLocation
                ).isSuccessful
            )
        }
    }

    @Test
    fun `Updating alarm location with returned result is successful`() {
        val alarmId = "test_id"
        val alarmLocation = AlarmLocation(
            coordinates = AlarmCoordinates(
                lat = 100.0,
                lng = 101.0,
                accuracy = 1
            )
        )
        val response = TestUtil.getTestObjectFromFile(
            "alarm/update_alarm_location_success.json",
            AlarmLocation::class.java)
        (underTest as MockNoonlightApi).mockAlarmCoordinatesResponse = response
        runBlocking {
            assertTrue(
                underTest.updateAlarmLocation(
                    alarmId = alarmId,
                    alarmLocation = alarmLocation
                ).isSuccessful
            )
        }
    }
}