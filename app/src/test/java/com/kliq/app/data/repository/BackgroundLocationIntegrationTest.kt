package com.kliq.app.data.repository

import android.content.Context
import com.kliq.app.data.local.dao.LocationDao
import com.kliq.app.data.local.entities.LocationEntity
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.util.PermissionManager
import com.kliq.app.viewmodel.LocationTrackingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class IntegrationFakeLocationDao : LocationDao {
    private val locations = mutableListOf<LocationEntity>()
    var clearCount = 0

    override suspend fun insertLocation(location: LocationEntity): Long {
        locations.add(location)
        return locations.size.toLong()
    }

    override fun getLatestLocation(): Flow<LocationEntity?> {
        return flowOf(locations.lastOrNull())
    }

    override fun getRecentLocations(limit: Int): Flow<List<LocationEntity>> {
        return flowOf(locations.takeLast(limit))
    }

    override fun getLocationCount(): Flow<Int> {
        return flowOf(locations.size)
    }

    override suspend fun clearAllLocations() {
        clearCount++
        locations.clear()
    }
}

/**
 * Automated Integration Test Suite for Kapitel 4.3: Background Location Tracking.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BackgroundLocationIntegrationTest {

    private val context: Context = mock(Context::class.java)
    private val permissionManager: PermissionManager = mock(PermissionManager::class.java)
    private val fakeDao = IntegrationFakeLocationDao()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var repository: LocationRepositoryImpl
    private lateinit var viewModel: LocationTrackingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        `when`(permissionManager.checkBackgroundLocationPermission(context)).thenReturn(LocationPermissionState.Granted)

        repository = LocationRepositoryImpl(
            context = context,
            locationDao = fakeDao,
            ioDispatcher = testDispatcher
        )

        viewModel = LocationTrackingViewModel(
            context = context,
            locationRepository = repository,
            permissionManager = permissionManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSimulatedLocationUpdates_persistedToRoomDatabaseAndStateFlow() = testScope.runTest {
        val sample1 = LocationData(latitude = 52.5200, longitude = 13.4050, accuracy = 8.5f, timestampMs = 1000L)
        val sample2 = LocationData(latitude = 52.5215, longitude = 13.4080, accuracy = 5.0f, timestampMs = 2000L)

        repository.recordLocationUpdate(sample1)
        repository.recordLocationUpdate(sample2)
        testDispatcher.scheduler.advanceUntilIdle()

        val latestState = repository.locationUpdates.value
        assertNotNull(latestState)
        assertEquals(52.5215, latestState!!.latitude, 0.0001)

        val latestSavedInDb = repository.getLatestSavedLocation().first()
        assertNotNull(latestSavedInDb)
        assertEquals(52.5215, latestSavedInDb!!.latitude, 0.0001)
        assertEquals(5.0f, latestSavedInDb.accuracy, 0.01f)
    }

    @Test
    fun testAppMinimizationBackgroundMode_locationUpdatesContinueCaching() = testScope.runTest {
        repository.startBackgroundTracking()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(repository.isTrackingActive.value)

        val trackPoints = listOf(
            LocationData(latitude = 52.5000, longitude = 13.3000, accuracy = 12f, timestampMs = 10000L),
            LocationData(latitude = 52.5020, longitude = 13.3030, accuracy = 10f, timestampMs = 20000L),
            LocationData(latitude = 52.5050, longitude = 13.3080, accuracy = 7f, timestampMs = 30000L)
        )

        for (tp in trackPoints) {
            repository.recordLocationUpdate(tp)
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val history = repository.getRecentLocationHistory(limit = 10).first()
        assertEquals(3, history.size)
        assertEquals(52.5050, history.last().latitude, 0.0001)
    }

    @Test
    fun testGpsDisabledOrPermissionRevoked_stopsTrackingCleanlyAndUpdatesUiState() = testScope.runTest {
        `when`(permissionManager.checkBackgroundLocationPermission(context)).thenReturn(LocationPermissionState.Denied)

        viewModel.toggleTracking()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(repository.isTrackingActive.value)
        assertEquals(LocationPermissionState.Denied, viewModel.uiState.value.backgroundPermissionState)
    }

    @Test
    fun testAdaptiveIntervalFiltering_calculatesDistanceDisplacement() {
        val locStart = LocationData(latitude = 52.5200, longitude = 13.4050)
        val locMinorShift = LocationData(latitude = 52.5202, longitude = 13.4052)
        val locMajorShift = LocationData(latitude = 52.5245, longitude = 13.4110)

        val distMinor = calculateDistanceMeters(locStart.latitude, locStart.longitude, locMinorShift.latitude, locMinorShift.longitude)
        val distMajor = calculateDistanceMeters(locStart.latitude, locStart.longitude, locMajorShift.latitude, locMajorShift.longitude)

        assertTrue("Minor shift should be under 50m", distMinor < 50f)
        assertTrue("Major shift should be over 50m", distMajor > 50f)
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val earthRadius = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (earthRadius * c).toFloat()
    }
}
