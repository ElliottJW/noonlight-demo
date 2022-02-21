package com.noonlight.apps.ui.model.alarm

import android.Manifest
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.noonlight.apps.data.alarm.*
import com.noonlight.apps.domain.location.LocationRepository
import com.noonlight.apps.domain.location.LocationWrapper
import com.noonlight.apps.domain.user.MockUserProvider
import com.noonlight.apps.network.api.NoonlightApi
import com.noonlight.apps.rule.CoroutineDispatcherRule
import com.noonlight.apps.util.TestUtil
import io.mockk.*
import junit.framework.Assert.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class AlarmScreenViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineDispatcherRule = CoroutineDispatcherRule()

    private val locationRepository = mockk<LocationRepository>()
    private val noonlightApi = mockk<NoonlightApi>()
    private val userProvider = MockUserProvider()
    private lateinit var underTest: AlarmScreenViewModel

    private fun getLocationWrapper(): LocationWrapper {
        return LocationWrapper(
            accuracy = 5,
            latitude = 100.0,
            longitude = 101.0,
            permissions = mapOf(
                Manifest.permission.ACCESS_FINE_LOCATION to true,
                Manifest.permission.ACCESS_COARSE_LOCATION to true
            )
        )
    }

    private fun getAlarmRequest(wrapper: LocationWrapper): CreateAlarmRequest {
        return CreateAlarmRequest(
            name = userProvider.name,
            phone = userProvider.phone,
            pin = userProvider.pin,
            location = AlarmLocation(
                coordinates = AlarmCoordinates(
                    lat = wrapper.latitude!!,
                    lng = wrapper.longitude!!,
                    accuracy = wrapper.accuracy!!
                )
            )
        )
    }

    @Before
    @ExperimentalCoroutinesApi
    fun setUp() {
        every { locationRepository.areCurrentLocationPermissionsGranted() } returns true
        every { locationRepository.checkLocationPermissions(any(), any()) } just runs

        underTest = AlarmScreenViewModel(
            locationRepository = locationRepository,
            noonlightApi = noonlightApi,
            userProvider = userProvider
        )
    }

    @Test
    fun `Fire location solicitation event if permissions are not granted`() {
        every { locationRepository.areCurrentLocationPermissionsGranted() } returns false

        runBlocking {
            underTest.onCreateAlarmClicked()
            val actual = underTest.events.first()
            val expected = AlarmScreenEvent.SolicitLocationPermissions
            assertEquals(expected, actual)
        }

    }

    @Test
    fun `Get the last location if permissions have been granted`() {
        every { locationRepository.getLastLocation(any(), any()) } answers {
            firstArg<((LocationWrapper) -> Unit)>().invoke(getLocationWrapper())
        }

        underTest.onCreateAlarmClicked()
        verify { locationRepository.getLastLocation(any(), any()) }
    }

    @Test
    fun `Successful create alarm request is created with valid wrapper`() {
        val wrapper = getLocationWrapper()
        every { locationRepository.getLastLocation(any(), any()) } answers {
            firstArg<((LocationWrapper) -> Unit)>().invoke(wrapper)
        }
        val response = TestUtil.getTestObjectFromFile(
            "alarm/create_alarm_response_success.json",
            CreateAlarmResponse::class.java
        )
        coEvery { noonlightApi.createAlarm(any()) } returns Response.success(response)
        val expectedRequest = getAlarmRequest(wrapper = wrapper)

        runBlocking {
            underTest.onCreateAlarmClicked()
            coVerify { noonlightApi.createAlarm(expectedRequest) }
        }
    }

    @Test
    fun `Successful cancel alarm emits creating state`() {
        // setup
        val wrapper = getLocationWrapper()
        every { locationRepository.getLastLocation(any(), any()) } answers {
            firstArg<((LocationWrapper) -> Unit)>().invoke(wrapper)
        }
        val response = TestUtil.getTestObjectFromFile(
            "alarm/create_alarm_response_success.json",
            CreateAlarmResponse::class.java
        )
        coEvery { noonlightApi.createAlarm(any()) } returns Response.success(response)

        runBlocking {
            underTest.onCreateAlarmClicked()

            // ... then test:
            underTest.onCancelAlarmClicked()

            coVerify { noonlightApi.updateAlarmStatus(
                alarmId = response.id,
                alarmStatus = AlarmStatus(
                    status = AlarmStatus.Type.CANCELED
                )
            ) }
        }
    }

    @Test
    fun `Permitting location permissions emits start location permissions request`() {
        runBlocking {
            underTest.onPermissionSolicitationAllowed()
            val actual = underTest.events.first()
            val expected = AlarmScreenEvent.StartLocationPermissionsRequest
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Permitting location permissions sets pending request to true`() {
        runBlocking {
            underTest.onPermissionSolicitationAllowed()
            assertTrue(underTest.createAlarmRequestPending.get())
        }
    }

    @Test
    fun `Denying location permissions emits denied permissions request`() {
        runBlocking {
            underTest.onPermissionsSolicitationDenied()
            val actual = underTest.events.first()
            val expected = AlarmScreenEvent.LocationPermissionsDenied
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `Denying location permissions sets pending request to false`() {
        runBlocking {
            underTest.onPermissionsSolicitationDenied()
            assertFalse(underTest.createAlarmRequestPending.get())
        }
    }
}