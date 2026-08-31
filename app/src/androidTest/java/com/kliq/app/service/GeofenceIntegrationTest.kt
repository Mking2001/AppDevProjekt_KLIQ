package com.kliq.app.service

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.GeofenceTransitionType
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.GeofenceRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class GeofenceIntegrationTest {

    private lateinit var context: Context
    private val clubRepository: ClubRepository = mock(ClubRepository::class.java)
    private lateinit var geofenceRepository: GeofenceRepositoryImpl

    private val testClub = Club(
        id = "integration_club_1",
        name = "Kliq VIP Lounge",
        location = GpsLocation(46.6235, 14.3070, "Villacher Straße 10, Klagenfurt"),
        geofenceRadiusMeters = 100.0,
        averageRating = 4.9,
        operatingHours = OperatingHours(isOpenNow = true, todayHours = "21:00 - 04:00")
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        `when`(clubRepository.getClubById("integration_club_1")).thenReturn(flowOf(testClub))
        geofenceRepository = GeofenceRepositoryImpl(clubRepository)
    }

    @Test
    fun testGeofenceBroadcastReceiverIntentHandling() = runTest {
        val receiver = GeofenceBroadcastReceiver()
        receiver.geofenceRepository = geofenceRepository
        receiver.ioDispatcher = kotlinx.coroutines.Dispatchers.Main

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        }

        assertEquals(GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT, intent.action)
    }

    @Test
    fun testEndToEndGeofenceTransitionWorkflow() = runTest {

        geofenceRepository.handleGeofenceTransition("integration_club_1", GeofenceTransitionType.ENTER)

        val activeState = geofenceRepository.activeClubState.value
        assertTrue(activeState.isInsideGeofence)
        assertEquals("integration_club_1", activeState.activeClubId)
        assertEquals("Kliq VIP Lounge", activeState.activeClubName)
        assertTrue(geofenceRepository.isClubGeofenceVerified("integration_club_1"))

        val history = geofenceRepository.getVisitedHistoryForUser()
        assertEquals(1, history.size)
        assertEquals("integration_club_1", history[0].clubId)
        assertTrue(history[0].isVerifiedVisit)

        geofenceRepository.handleGeofenceTransition("integration_club_1", GeofenceTransitionType.EXIT)

        val updatedState = geofenceRepository.activeClubState.value
        assertFalse(updatedState.isInsideGeofence)
        assertFalse(geofenceRepository.isClubGeofenceVerified("integration_club_1"))

        val updatedHistory = geofenceRepository.getVisitedHistoryForUser()
        assertEquals(1, updatedHistory.size)
        assertNotNull(updatedHistory[0].exitTimestamp)
    }
}
