package com.kliq.app.service

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.GeofenceTransitionType
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.GeofenceRepositoryImpl
import com.kliq.app.util.GeofenceManager
import com.kliq.app.viewmodel.GeofenceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

/**
 * Executable Automated Integration Test for Kapitel 4.6: Geofencing-Logik für Club-Radien.
 * Simulates GPS entrance into a 50m radius club, GPS jitter stay, and exit transition.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class GeofenceClubRadiiIntegrationTest {

    private lateinit var context: Context
    private val clubRepository: ClubRepository = mock(ClubRepository::class.java)
    private val geofenceManager: GeofenceManager = mock(GeofenceManager::class.java)

    private lateinit var geofenceRepository: GeofenceRepositoryImpl
    private lateinit var geofenceViewModel: GeofenceViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val mockClub50m = Club(
        id = "club_havana_50m",
        name = "Club Havana",
        location = GpsLocation(46.6240, 14.3060, "St. Veiter Ring 20, Klagenfurt"),
        geofenceRadiusMeters = 50.0,
        averageRating = 4.7,
        operatingHours = OperatingHours(isOpenNow = true, todayHours = "22:00 - 05:00")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = ApplicationProvider.getApplicationContext()

        `when`(clubRepository.getClubById("club_havana_50m")).thenReturn(flowOf(mockClub50m))
        `when`(clubRepository.getAllClubs()).thenReturn(flowOf(listOf(mockClub50m)))

        geofenceRepository = GeofenceRepositoryImpl(clubRepository)
        geofenceViewModel = GeofenceViewModel(
            geofenceRepository = geofenceRepository,
            geofenceManager = geofenceManager,
            clubRepository = clubRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testCompleteGeofenceWorkflow_ApproachJitterExit() = runTest {
        // Initial state verification: outside geofence, review button disabled
        testDispatcher.scheduler.advanceUntilIdle()
        var uiState = geofenceViewModel.uiState.value
        assertFalse(uiState.isInsideGeofence)
        assertFalse(uiState.isReviewEnabled)
        assertNull(uiState.activeClubId)

        // -------------------------------------------------------------------------
        // Step 1: Annäherung & Eintritt in den 50m Club-Radius
        // Simulated GPS: Lat 46.62401, Lon 14.30601 (Distance ~1.5m inside 50m radius)
        // -------------------------------------------------------------------------
        geofenceRepository.handleGeofenceTransition("club_havana_50m", GeofenceTransitionType.ENTER)
        testDispatcher.scheduler.advanceUntilIdle()

        uiState = geofenceViewModel.uiState.value
        assertTrue("Geofence ENTER transition must set isInsideGeofence to true", uiState.isInsideGeofence)
        assertTrue("Geofence ENTER transition must enable review button (isReviewEnabled = true)", uiState.isReviewEnabled)
        assertEquals("club_havana_50m", uiState.activeClubId)
        assertEquals("Club Havana", uiState.activeClubName)
        assertEquals("club_havana_50m", uiState.verifiedClubId)
        assertNotNull("Entry timestamp must be set", uiState.entryTimestamp)

        // Verify visit history record
        val historyStage1 = geofenceRepository.getVisitedHistoryForUser()
        assertEquals(1, historyStage1.size)
        assertEquals("club_havana_50m", historyStage1[0].clubId)
        assertNull("Active visit exitTimestamp must remain null during stay", historyStage1[0].exitTimestamp)
        assertTrue("Visit must be marked as verified", historyStage1[0].isVerifiedVisit)

        // -------------------------------------------------------------------------
        // Step 2: Aufenthalt im Radius mit GPS-Jitter (Koordinatenschwankungen)
        // Jitter 1: 46.62403, 14.30604 (~4m)
        // Jitter 2: 46.62398, 14.30596 (~5m)
        // Jitter 3: 46.62405, 14.30608 (~8m)
        // -------------------------------------------------------------------------
        val jitterCoordinates = listOf(
            Pair(46.62403, 14.30604),
            Pair(46.62398, 14.30596),
            Pair(46.62405, 14.30608)
        )

        for ((lat, lon) in jitterCoordinates) {
            // Verify that location shifts inside geofence do not interrupt active state
            val dist = calculateDistanceMeters(mockClub50m.location.latitude, mockClub50m.location.longitude, lat, lon)
            assertTrue("Jitter position should remain inside 50m radius", dist < 50.0f)

            // Re-evaluating state during stay
            uiState = geofenceViewModel.uiState.value
            assertTrue("State must remain continuously active during GPS jitter stay", uiState.isInsideGeofence)
            assertTrue("Review button must remain continuously enabled", uiState.isReviewEnabled)
            assertEquals("club_havana_50m", uiState.activeClubId)
        }

        // History count remains stable at 1 entry
        val historyStage2 = geofenceRepository.getVisitedHistoryForUser()
        assertEquals("GPS jitter should not create duplicate visit history records", 1, historyStage2.size)

        // -------------------------------------------------------------------------
        // Step 3: Verlassen des Club-Radius (GPS far outside)
        // Simulated GPS: Lat 46.6350, Lon 14.3200 (> 1.5 km outside radius)
        // -------------------------------------------------------------------------
        val exitLat = 46.6350
        val exitLon = 14.3200
        val distExit = calculateDistanceMeters(mockClub50m.location.latitude, mockClub50m.location.longitude, exitLat, exitLon)
        assertTrue("Exit position must be far outside radius", distExit > 1000.0f)

        geofenceRepository.handleGeofenceTransition("club_havana_50m", GeofenceTransitionType.EXIT)
        testDispatcher.scheduler.advanceUntilIdle()

        uiState = geofenceViewModel.uiState.value
        assertFalse("Geofence EXIT transition must set isInsideGeofence to false", uiState.isInsideGeofence)
        assertFalse("Geofence EXIT transition must disable review button (isReviewEnabled = false)", uiState.isReviewEnabled)
        assertNull("Active club ID must be cleared upon exit", uiState.activeClubId)
        assertNull("Active club name must be cleared upon exit", uiState.activeClubName)

        // Verify visit history entry completion
        val historyStage3 = geofenceRepository.getVisitedHistoryForUser()
        assertEquals(1, historyStage3.size)
        assertNotNull("Exit timestamp must be written to completed visit history record", historyStage3[0].exitTimestamp)
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
}
