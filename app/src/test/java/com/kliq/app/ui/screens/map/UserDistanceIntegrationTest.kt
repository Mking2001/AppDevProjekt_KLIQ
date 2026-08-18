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

import com.kliq.app.data.model.LocationPowerPolicy
import com.kliq.app.data.model.LocationTrackingMode

class FakeLocationRepository : LocationRepository {
    private val _locationUpdates = MutableStateFlow<LocationData?>(null)
    override val locationUpdates: StateFlow<LocationData?> = _locationUpdates

    private val _isTrackingActive = MutableStateFlow(false)
    override val isTrackingActive: StateFlow<Boolean> = _isTrackingActive

    private val _trackingMode = MutableStateFlow(LocationTrackingMode.BALANCED_AMBIENT)
    override val trackingMode: StateFlow<LocationTrackingMode> = _trackingMode

    private val _powerPolicy = MutableStateFlow(LocationPowerPolicy.BalancedAmbient)
    override val powerPolicy: StateFlow<LocationPowerPolicy> = _powerPolicy

    override val isStationary: StateFlow<Boolean> = MutableStateFlow(false)
    override val isBurstActive: StateFlow<Boolean> = MutableStateFlow(false)
    override val burstRemainingSeconds: StateFlow<Int> = MutableStateFlow(0)

    fun emitLocation(location: LocationData) {
        _locationUpdates.value = location
    }

    override fun setTrackingMode(mode: LocationTrackingMode) {
        _trackingMode.value = mode
        _powerPolicy.value = LocationPowerPolicy.forMode(mode)
    }

    override fun requestHighAccuracyBurst(durationMs: Long) {
        _trackingMode.value = LocationTrackingMode.HIGH_ACCURACY
        _powerPolicy.value = LocationPowerPolicy.HighAccuracy
    }

    override fun cancelBurstSession() {
        _trackingMode.value = LocationTrackingMode.BALANCED_AMBIENT
        _powerPolicy.value = LocationPowerPolicy.BalancedAmbient
    }

    override fun setAppForegroundState(isForeground: Boolean) {}

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
        // Alex (u1) is at 52.5130, 13.4410 in fallback list
        viewModel.updateUserDistances(52.5130, 13.4410)
        testDispatcher.scheduler.advanceUntilIdle()

        val alex = viewModel.uiState.value.userMarkers.first { it.userId == "u1" }
        assertNotNull(alex.distanceMeters)
        assertEquals(0.0, alex.distanceMeters!!, 0.1)
        assertEquals("0 m", alex.formattedDistance)
    }

    @Test
    fun testUpdateUserDistances_farAwayCoordinates_formatsInKilometers() {
        // Positioned far from Berlin (e.g. Munich: 48.1351, 11.5820)
        viewModel.updateUserDistances(48.1351, 11.5820)
        testDispatcher.scheduler.advanceUntilIdle()

        val alex = viewModel.uiState.value.userMarkers.first { it.userId == "u1" }
        assertNotNull(alex.distanceMeters)
        assertTrue(alex.distanceMeters!! > 400000.0) // > 400 km
        assertTrue(alex.formattedDistance.endsWith("km"))
    }

    @Test
    fun testLocationRepositoryUpdates_automaticallyRecalculateDistances() {
        // Emit location via reactive flow
        fakeLocationRepository.emitLocation(LocationData(latitude = 52.5130, longitude = 13.4410))
        testDispatcher.scheduler.advanceUntilIdle()

        val alex = viewModel.uiState.value.userMarkers.first { it.userId == "u1" }
        assertEquals("0 m", alex.formattedDistance)

        // Emit new location snapshot (Potsdam ~ 26 km away)
        fakeLocationRepository.emitLocation(LocationData(latitude = 52.3988, longitude = 13.0657))
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedAlex = viewModel.uiState.value.userMarkers.first { it.userId == "u1" }
        assertTrue(updatedAlex.formattedDistance.endsWith("km"))
    }
}
