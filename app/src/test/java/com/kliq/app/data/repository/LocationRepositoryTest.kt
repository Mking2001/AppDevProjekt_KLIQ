package com.kliq.app.data.repository

import android.content.Context
import com.kliq.app.data.local.dao.LocationDao
import com.kliq.app.data.local.entities.LocationEntity
import com.kliq.app.data.model.LocationData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class LocationRepositoryTest {

    private val context: Context = mock(Context::class.java)
    private val locationDao: LocationDao = mock(LocationDao::class.java)
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var repository: LocationRepositoryImpl

    @Before
    fun setUp() {
        repository = LocationRepositoryImpl(
            context = context,
            locationDao = locationDao,
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

        val captor = ArgumentCaptor.forClass(LocationEntity::class.java)
        verify(locationDao).insertLocation(captor.capture())
        val capturedEntity = captor.value
        assertEquals(52.5200, capturedEntity.latitude, 0.0001)
        assertEquals(13.4050, capturedEntity.longitude, 0.0001)
    }

    @Test
    fun getLatestSavedLocation_delegatesToDao() = testScope.runTest {
        val expectedEntity = LocationEntity(
            id = 1L,
            latitude = 52.5200,
            longitude = 13.4050,
            accuracy = 5f,
            timestampMs = 1700000000000L
        )

        `when`(locationDao.getLatestLocation()).thenReturn(flowOf(expectedEntity))

        val result = repository.getLatestSavedLocation().first()
        assertNotNull(result)
        assertEquals(1L, result!!.id)
        assertEquals(52.5200, result.latitude, 0.0001)
    }

    @Test
    fun clearLocationHistory_invokesDaoClear() = testScope.runTest {
        repository.clearLocationHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(locationDao).clearAllLocations()
    }
}
