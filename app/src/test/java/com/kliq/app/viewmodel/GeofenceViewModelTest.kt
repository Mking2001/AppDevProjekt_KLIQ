package com.kliq.app.viewmodel

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.ClubGeofenceState
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.GeofenceTransitionType
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.model.VisitedClubHistory
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.GeofenceRepository
import com.kliq.app.util.GeofenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class GeofenceViewModelTest {

    private val geofenceRepository: GeofenceRepository = mock(GeofenceRepository::class.java)
    private val geofenceManager: GeofenceManager = mock(GeofenceManager::class.java)
    private val clubRepository: ClubRepository = mock(ClubRepository::class.java)

    private val activeClubStateFlow = MutableStateFlow(ClubGeofenceState())
    private val visitedHistoryFlow = MutableStateFlow<List<VisitedClubHistory>>(emptyList())

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: GeofenceViewModel

    private val sampleClub = Club(
        id = "club_klagenfurt_1",
        name = "Matrix Club",
        location = GpsLocation(46.6230, 14.3075, "Heiligengeistplatz 1, Klagenfurt"),
        geofenceRadiusMeters = 200.0,
        averageRating = 4.5,
        operatingHours = OperatingHours(isOpenNow = true, todayHours = "23:00 - 05:00")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        `when`(geofenceRepository.activeClubState).thenReturn(activeClubStateFlow)
        `when`(geofenceRepository.visitedHistory).thenReturn(visitedHistoryFlow)
        `when`(clubRepository.getAllClubs()).thenReturn(flowOf(listOf(sampleClub)))

        viewModel = GeofenceViewModel(
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
    fun initialState_reflectsDefaultRepositoryValues() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isInsideGeofence)
        assertFalse(state.isReviewEnabled)
        assertNull(state.activeClubId)
        assertTrue(state.visitedHistory.isEmpty())
    }

    @Test
    fun observeGeofenceState_updatesUiStateWhenRepositoryEmitsActiveGeofence() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        activeClubStateFlow.value = ClubGeofenceState(
            activeClubId = "club_klagenfurt_1",
            activeClubName = "Matrix Club",
            isInsideGeofence = true,
            entryTimestamp = 1700000000000L,
            activeGeofenceCount = 5,
            verifiedClubId = "club_klagenfurt_1"
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isInsideGeofence)
        assertTrue(state.isReviewEnabled)
        assertEquals("club_klagenfurt_1", state.activeClubId)
        assertEquals("Matrix Club", state.activeClubName)
        assertEquals("club_klagenfurt_1", state.verifiedClubId)
    }

    @Test
    fun syncGeofencesForLocation_invokesGeofenceManagerAndRepositoryUpdate() = runTest {
        `when`(geofenceManager.updateGeofencesForLocation(46.6230, 14.3075, listOf(sampleClub), 50))
            .thenReturn(Result.success(1))

        viewModel.syncGeofencesForLocation(46.6230, 14.3075, 50)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(geofenceManager).updateGeofencesForLocation(46.6230, 14.3075, listOf(sampleClub), 50)
        verify(geofenceRepository).updateRegisteredGeofenceCount(1)
        assertEquals(1, viewModel.uiState.value.registeredGeofencesCount)
    }

    @Test
    fun simulateGeofenceEnter_triggersRepositoryHandleTransition() = runTest {
        viewModel.simulateGeofenceEnter("club_klagenfurt_1")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(geofenceRepository).handleGeofenceTransition("club_klagenfurt_1", GeofenceTransitionType.ENTER)
    }

    @Test
    fun simulateGeofenceExit_triggersRepositoryHandleTransition() = runTest {
        viewModel.simulateGeofenceExit("club_klagenfurt_1")
        testDispatcher.scheduler.advanceUntilIdle()

        verify(geofenceRepository).handleGeofenceTransition("club_klagenfurt_1", GeofenceTransitionType.EXIT)
    }

    @Test
    fun clearAllGeofences_clearsManagerAndResetsRepository() = runTest {
        viewModel.clearAllGeofences()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(geofenceManager).clearAllGeofences()
        verify(geofenceRepository).resetGeofenceState()
        assertEquals(0, viewModel.uiState.value.registeredGeofencesCount)
    }
}
