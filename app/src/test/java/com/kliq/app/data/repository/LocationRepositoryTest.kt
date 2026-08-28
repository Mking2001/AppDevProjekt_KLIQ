package com.kliq.app.data.repository

import android.content.Context
import com.kliq.app.data.local.dao.LocationDao
import com.kliq.app.data.local.entities.LocationEntity
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPowerPolicy
import com.kliq.app.data.model.LocationTrackingMode
import com.kliq.app.util.AdaptiveLocationController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class FakeLocationDao : LocationDao {
    val insertedLocations = mutableListOf<LocationEntity>()
    var clearCalled = false

    override suspend fun insertLocation(location: LocationEntity): Long {
        insertedLocations.add(location)
        return insertedLocations.size.toLong()
    }

    override fun getLatestLocation(): Flow<LocationEntity?> {
        return flowOf(insertedLocations.lastOrNull())
    }

    override fun getRecentLocations(limit: Int): Flow<List<LocationEntity>> {
        return flowOf(insertedLocations.takeLast(limit))
    }

    override fun getLocationCount(): Flow<Int> {
        return flowOf(insertedLocations.size)
    }

    override suspend fun clearAllLocations() {
        clearCalled = true
        insertedLocations.clear()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LocationRepositoryTest {

    private val context: Context = mock(Context::class.java)
    private val fakeLocationDao = FakeLocationDao()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var adaptiveController: AdaptiveLocationController
    private lateinit var repository: LocationRepositoryImpl

    @Before
    fun setUp() {
        adaptiveController = AdaptiveLocationController(testDispatcher)
        repository = LocationRepositoryImpl(
            context = context,
            locationDao = fakeLocationDao,
            adaptiveController = adaptiveController,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun recordLocationUpdate_updatesStateFlow_andInsertsIntoDao() = testScope.runTest {
        val testData = LocationData(
            latitude = 52.5200,
            longitude = 13.4050,
            accuracy = 10f,
            timestampMs = 1700000000000L,
            speed = 1.2f
        )

        repository.recordLocationUpdate(testData)
        testDispatcher.scheduler.advanceUntilIdle()

        val latestLocation = repository.locationUpdates.value
        assertNotNull(latestLocation)
        assertEquals(52.5200, latestLocation!!.latitude, 0.0001)
        assertEquals(13.4050, latestLocation.longitude, 0.0001)

        assertEquals(1, fakeLocationDao.insertedLocations.size)
        val capturedEntity = fakeLocationDao.insertedLocations.first()
        assertEquals(52.5200, capturedEntity.latitude, 0.0001)
        assertEquals(13.4050, capturedEntity.longitude, 0.0001)
    }

    @Test
    fun getLatestSavedLocation_delegatesToDao() = testScope.runTest {
        val testData = LocationData(
            latitude = 52.5200,
            longitude = 13.4050,
            accuracy = 5f,
            timestampMs = 1700000000000L
        )

        repository.recordLocationUpdate(testData)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = repository.getLatestSavedLocation().first()
        assertNotNull(result)
        assertEquals(52.5200, result!!.latitude, 0.0001)
    }

    @Test
    fun clearLocationHistory_invokesDaoClear() = testScope.runTest {
        repository.clearLocationHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(fakeLocationDao.clearCalled)
    }

    @Test
    fun setTrackingMode_updatesModeAndPowerPolicy() {
        repository.setTrackingMode(LocationTrackingMode.HIGH_ACCURACY)
        assertEquals(LocationTrackingMode.HIGH_ACCURACY, repository.trackingMode.value)
        assertEquals(LocationPowerPolicy.HighAccuracy, repository.powerPolicy.value)

        repository.setTrackingMode(LocationTrackingMode.IDLE_PASSIVE)
        assertEquals(LocationTrackingMode.IDLE_PASSIVE, repository.trackingMode.value)
        assertEquals(LocationPowerPolicy.IdlePassive, repository.powerPolicy.value)
    }

    @Test
    fun requestHighAccuracyBurst_activatesBurstAndTimer() = testScope.runTest {
        repository.requestHighAccuracyBurst(durationMs = 10_000L)
        assertTrue(repository.isBurstActive.value)
        assertEquals(LocationTrackingMode.HIGH_ACCURACY, repository.trackingMode.value)
        assertEquals(10, repository.burstRemainingSeconds.value)

        repository.cancelBurstSession()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(repository.isBurstActive.value)
        assertEquals(0, repository.burstRemainingSeconds.value)
        assertEquals(LocationTrackingMode.BALANCED_AMBIENT, repository.trackingMode.value)
    }

    @Test
    fun setAppForegroundState_throttlesModeWhenBackgrounded() {
        repository.setAppForegroundState(false)
        assertEquals(LocationTrackingMode.IDLE_PASSIVE, repository.trackingMode.value)

        repository.setAppForegroundState(true)
        assertEquals(LocationTrackingMode.BALANCED_AMBIENT, repository.trackingMode.value)
    }
}
