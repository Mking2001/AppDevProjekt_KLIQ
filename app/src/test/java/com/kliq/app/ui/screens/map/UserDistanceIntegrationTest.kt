package com.kliq.app.ui.screens.map

import com.kliq.app.data.local.entities.LocationEntity
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.LocationRepository
import com.kliq.app.domain.usecase.CalculateUserDistanceUseCase
import com.kliq.app.util.UserDistanceFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.Locale

class FakeLocationRepository : LocationRepository {
    private val _locationUpdates = MutableStateFlow<LocationData?>(null)
    override val locationUpdates: StateFlow<LocationData?> = _locationUpdates

    private val _isTrackingActive = MutableStateFlow(false)
    override val isTrackingActive: StateFlow<Boolean> = _isTrackingActive

    fun emitLocation(location: LocationData) {
        _locationUpdates.value = location
    }

    override fun startBackgroundTracking() { _isTrackingActive.value = true }
    override fun stopBackgroundTracking() { _isTrackingActive.value = false }
    override suspend fun recordLocationUpdate(location: LocationData) { _locationUpdates.value = location }
    override fun getLatestSavedLocation(): Flow<LocationEntity?> = flowOf(null)
    override fun getRecentLocationHistory(limit: Int): Flow<List<LocationEntity>> = flowOf(emptyList())
    override fun getLocationCount(): Flow<Int> = flowOf(0)
    override suspend fun clearLocationHistory() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class UserDistanceIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private val clubRepository: ClubRepository = mock(ClubRepository::class.java)
    private val fakeLocationRepository = FakeLocationRepository()

    private val calculateUseCase = CalculateUserDistanceUseCase()
    private val formatter = UserDistanceFormatter(Locale.US)

    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(clubRepository.getAllClubs()).thenReturn(flowOf(emptyList()))

        viewModel = MapViewModel(
            clubRepository = clubRepository,
            calculateUserDistanceUseCase = calculateUseCase,
            userDistanceFormatter = formatter,
            locationRepository = fakeLocationRepository,
            defaultDispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialUserMarkersHaveCalculatedDistances() {
        val userMarkers = viewModel.uiState.value.userMarkers
        assertTrue(userMarkers.isNotEmpty())

        userMarkers.forEach { user ->
            assertNotNull(user.distanceMeters)
            assertTrue(user.formattedDistance.isNotBlank())
        }
    }

    @Test
    fun testUpdateUserDistances_exactMatchCoordinates_returnsZeroMeters() {
        // Lena (usr_lena) ist im Klagenfurt-Fallback-Datensatz an der Strandbar Loretto
        // positioniert: 46.6162, 14.2696 (siehe MapViewModel.getFallbackUsers()).
        viewModel.updateUserDistances(46.6162, 14.2696)
        testDispatcher.scheduler.advanceUntilIdle()

        val lena = viewModel.uiState.value.userMarkers.first { it.userId == "usr_lena" }
        assertNotNull(lena.distanceMeters)
        assertEquals(0.0, lena.distanceMeters!!, 0.1)
        assertEquals("0 m", lena.formattedDistance)
    }

    @Test
    fun testUpdateUserDistances_farAwayCoordinates_formatsInKilometers() {
        // Positioniert weit entfernt von Klagenfurt (Wien: 48.2082, 16.3738).
        viewModel.updateUserDistances(48.2082, 16.3738)
        testDispatcher.scheduler.advanceUntilIdle()

        val lena = viewModel.uiState.value.userMarkers.first { it.userId == "usr_lena" }
        assertNotNull(lena.distanceMeters)
        assertTrue(lena.distanceMeters!! > 200000.0) // > 200 km
        assertTrue(lena.formattedDistance.endsWith("km"))
    }

    @Test
    fun testLocationRepositoryUpdates_automaticallyRecalculateDistances() {
        // Emit location via reactive flow
        fakeLocationRepository.emitLocation(LocationData(latitude = 46.6162, longitude = 14.2696))
        testDispatcher.scheduler.advanceUntilIdle()

        val lena = viewModel.uiState.value.userMarkers.first { it.userId == "usr_lena" }
        assertEquals("0 m", lena.formattedDistance)

        // Emit new location snapshot (Villach, ca. 40 km entfernt)
        fakeLocationRepository.emitLocation(LocationData(latitude = 46.6103, longitude = 13.8558))
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedLena = viewModel.uiState.value.userMarkers.first { it.userId == "usr_lena" }
        assertTrue(updatedLena.formattedDistance.endsWith("km"))
    }
}
