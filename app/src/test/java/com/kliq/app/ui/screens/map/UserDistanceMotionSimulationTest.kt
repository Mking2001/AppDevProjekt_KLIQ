package com.kliq.app.ui.screens.map

import com.kliq.app.data.model.LocationData
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.domain.usecase.CalculateUserDistanceUseCase
import com.kliq.app.util.UserDistanceFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class UserDistanceMotionSimulationTest {

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
    fun testLiveMotionSimulation_userApproaching_distanceDecreasesContinuously() {

        val steps = listOf(
            Pair(46.6382, 14.2696),
            Pair(46.6282, 14.2696),
            Pair(46.6207, 14.2696),
            Pair(46.61755, 14.2696),
            Pair(46.6162, 14.2696)
        )

        val recordedDistances = mutableListOf<Pair<Double?, String>>()

        steps.forEach { (currentLat, currentLng) ->
            fakeLocationRepository.emitLocation(LocationData(latitude = currentLat, longitude = currentLng))
            testDispatcher.scheduler.advanceUntilIdle()

            val lena = viewModel.uiState.value.userMarkers.first { it.userId == "usr_lena" }
            recordedDistances.add(Pair(lena.distanceMeters, lena.formattedDistance))
        }

        for (i in 0 until recordedDistances.size - 1) {
            val currentDist = recordedDistances[i].first
            val nextDist = recordedDistances[i + 1].first
            assertNotNull(currentDist)
            assertNotNull(nextDist)
            assertTrue("Distance should decrease: $currentDist -> $nextDist", nextDist!! < currentDist!!)
        }

        assertEquals("2.4 km", recordedDistances[0].second)
        assertEquals("1.3 km", recordedDistances[1].second)
        assertEquals("500 m", recordedDistances[2].second)
        assertEquals("150 m", recordedDistances[3].second)
        assertEquals("0 m", recordedDistances[4].second)
    }
}
