package com.noonlight.apps.domain.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class LocationRepositoryImplTest {

    private var grantLocationPermissions: Boolean = false

    @get:Rule
    val rule: GrantPermissionRule
        get() = GrantPermissionRule.grant(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )


    private lateinit var context: Context
    private val mockLocationProviderClient = mockk<FusedLocationProviderClient>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var underTest: LocationRepositoryImpl

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            .apply { setMockMode(true) }

        underTest = LocationRepositoryImpl(
            context = context,
            fusedLocationProviderClient = fusedLocationProviderClient
        )
    }

    @Test
    @Ignore(
        "Test ignored because permissions can only be granted once per " +
                "test class. This could be moved to another class and tested separately."
    )
    fun locationWrapper_returnsNull_whenPermissionsNotGranted() {
        grantLocationPermissions = false

        runBlocking {
            val actual = underTest.getLocationUpdates().first()

            val permissions = mapOf(
                Manifest.permission.ACCESS_COARSE_LOCATION to false,
                Manifest.permission.ACCESS_FINE_LOCATION to false
            )
            val expected = LocationWrapper(
                permissions = permissions,
                latitude = null,
                longitude = null,
                accuracy = null
            )

            assertEquals(expected, actual)
        }
    }

    /**
     * Google has patchy instructions on how to mock the fusedLocationProviderClient,
     * some of which being API-specific. After trying on a couple devices, I decided
     * to move on to integrating the API, which is the main objective.
     */
    @Test
    @Ignore("Having trouble figuring out how to mock the fusedLocationProviderClient.")
    fun receiveLocationUpdates_whenPermissionsAreGranted() {
        runBlocking {
            val actual = underTest.getLocationUpdates().first()
            val location = Location(FusedLocationProviderClient::class.simpleName).apply {
                latitude = 100.0
                longitude = 101.0
            }
            fusedLocationProviderClient.setMockLocation(location)
                // Neither of these callbacks are ever called, even with setMockLocation in the
                // LocationRepositoryImpl
                .addOnSuccessListener { Log.d(this::class.simpleName, "Set the mock") }
                .addOnFailureListener { Log.e(this::class.simpleName, "Failed to set mock") }

            val permissions = mapOf(
                Manifest.permission.ACCESS_COARSE_LOCATION to true,
                Manifest.permission.ACCESS_FINE_LOCATION to true
            )
            val expected = LocationWrapper(
                permissions = permissions,
                latitude = 100.0,
                longitude = 101.0,
                accuracy = 5
            )
            assertEquals(expected, actual)
        }
    }

    @Test
    fun currentLocationPermissionsStatus_returnsTrueForBoth_whenLocationsGranted() {
        val actual = underTest.getCurrentLocationPermissionsStatus()
        val expected = mapOf(
            Manifest.permission.ACCESS_COARSE_LOCATION to true,
            Manifest.permission.ACCESS_FINE_LOCATION to true
        )
        assertEquals(expected, actual)
    }
}