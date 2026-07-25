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

/**
 * Automated motion simulation test suite verifying continuous distance recalculations
 * as users move relative to each other.
 */
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
        // Target User "Alex" (u1) is stationary at (52.5130, 13.4410)
        val targetLat = 52.5130
        val targetLng = 13.4410

        // Current user moves towards Alex over 5 simulation steps from 2.5km away to 0m
        val steps = listOf(
            Pair(52.5350, 13.4410), // ~2.45 km away
            Pair(52.5250, 13.4410), // ~1.33 km away
            Pair(52.5175, 13.4410), // ~500 m away
            Pair(52.51435, 13.4410), // ~150 m away
            Pair(52.5130, 13.4410)   // 0 m (exact match)
        )

        val recordedDistances = mutableListOf<Pair<Double?, String>>()

        steps.forEach { (currentLat, currentLng) ->
            fakeLocationRepository.emitLocation(LocationData(latitude = currentLat, longitude = currentLng))
            testDispatcher.scheduler.advanceUntilIdle()

            val alex = viewModel.uiState.value.userMarkers.first { it.userId == "u1" }
            recordedDistances.add(Pair(alex.distanceMeters, alex.formattedDistance))
        }

        // Verify that distances strictly decrease across steps
        for (i in 0 until recordedDistances.size - 1) {
            val currentDist = recordedDistances[i].first
            val nextDist = recordedDistances[i + 1].first
            assertNotNull(currentDist)
            assertNotNull(nextDist)
            assertTrue("Distance should decrease: $currentDist -> $nextDist", nextDist!! < currentDist!!)
        }

        // Verify formatted strings at key milestones
        assertEquals("2.4 km", recordedDistances[0].second)
        assertEquals("1.3 km", recordedDistances[1].second)
        assertEquals("500 m", recordedDistances[2].second)
        assertEquals("150 m", recordedDistances[3].second)
        assertEquals("0 m", recordedDistances[4].second)
    }
}
