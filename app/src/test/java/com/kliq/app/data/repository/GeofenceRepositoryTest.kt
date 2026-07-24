package com.kliq.app.data.repository

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.GeofenceTransitionType
import com.kliq.app.data.model.OperatingHours
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class GeofenceRepositoryTest {

    private val clubRepository: ClubRepository = mock(ClubRepository::class.java)
    private lateinit var geofenceRepository: GeofenceRepositoryImpl

    private val sampleClub = Club(
        id = "club_101",
        name = "Kliq Nightclub",
        location = GpsLocation(46.6249, 14.3050, "Universitätsstraße 65, Klagenfurt"),
        geofenceRadiusMeters = 150.0,
        averageRating = 4.8,
        operatingHours = OperatingHours(isOpenNow = true, todayHours = "22:00 - 04:00")
    )

    @Before
    fun setUp() {
        `when`(clubRepository.getClubById("club_101")).thenReturn(flowOf(sampleClub))
        `when`(clubRepository.getClubById("unknown_club")).thenReturn(flowOf(null))
        geofenceRepository = GeofenceRepositoryImpl(clubRepository)
    }

    @Test
    fun handleGeofenceTransition_ENTER_updatesActiveClubAndVisitedHistory() = runTest {
        geofenceRepository.handleGeofenceTransition("club_101", GeofenceTransitionType.ENTER)

        val activeState = geofenceRepository.activeClubState.value
        assertEquals("club_101", activeState.activeClubId)
        assertEquals("Kliq Nightclub", activeState.activeClubName)
        assertTrue(activeState.isInsideGeofence)
        assertNotNull(activeState.entryTimestamp)
        assertEquals("club_101", activeState.verifiedClubId)

        val history = geofenceRepository.getVisitedHistoryForUser()
        assertEquals(1, history.size)
        assertEquals("club_101", history[0].clubId)
        assertEquals("Kliq Nightclub", history[0].clubName)
        assertNull(history[0].exitTimestamp)
        assertTrue(history[0].isVerifiedVisit)
    }

    @Test
    fun handleGeofenceTransition_EXIT_resetsActiveStateAndUpdateHistoryExitTimestamp() = runTest {
        geofenceRepository.handleGeofenceTransition("club_101", GeofenceTransitionType.ENTER)
        geofenceRepository.handleGeofenceTransition("club_101", GeofenceTransitionType.EXIT)

        val activeState = geofenceRepository.activeClubState.value
        assertNull(activeState.activeClubId)
        assertNull(activeState.activeClubName)
        assertFalse(activeState.isInsideGeofence)
        assertNull(activeState.entryTimestamp)

        val history = geofenceRepository.getVisitedHistoryForUser()
        assertEquals(1, history.size)
        assertEquals("club_101", history[0].clubId)
        assertNotNull(history[0].exitTimestamp)
    }

    @Test
    fun isClubGeofenceVerified_returnsTrueOnlyWhenInsideGeofenceForMatchingClub() = runTest {
        assertFalse(geofenceRepository.isClubGeofenceVerified("club_101"))

        geofenceRepository.handleGeofenceTransition("club_101", GeofenceTransitionType.ENTER)

        assertTrue(geofenceRepository.isClubGeofenceVerified("club_101"))
        assertFalse(geofenceRepository.isClubGeofenceVerified("club_999"))
    }

    @Test
    fun resetGeofenceState_clearsAllStatesAndHistory() = runTest {
        geofenceRepository.handleGeofenceTransition("club_101", GeofenceTransitionType.ENTER)
        geofenceRepository.resetGeofenceState()

        val activeState = geofenceRepository.activeClubState.value
        assertFalse(activeState.isInsideGeofence)
        assertNull(activeState.activeClubId)
        assertTrue(geofenceRepository.getVisitedHistoryForUser().isEmpty())
    }
}
