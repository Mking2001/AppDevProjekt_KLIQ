package com.kliq.app.viewmodel

import com.kliq.app.data.model.GenderRatio
import com.kliq.app.data.model.OccupancyCategory
import com.kliq.app.data.model.OccupancyTrend
import com.kliq.app.data.repository.ClubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class ClubLiveVisitorStatsTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var clubRepository: ClubRepository
    private lateinit var viewModel: ClubAnalyticsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        clubRepository = mock(ClubRepository::class.java)
        viewModel = ClubAnalyticsViewModel(clubRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testOccupancyCategory_boundaryValues_mapsCorrectly() {
        assertEquals(OccupancyCategory.SCHWACH, OccupancyCategory.fromPercentage(0))
        assertEquals(OccupancyCategory.SCHWACH, OccupancyCategory.fromPercentage(39))
        assertEquals(OccupancyCategory.MITTEL, OccupancyCategory.fromPercentage(40))
        assertEquals(OccupancyCategory.MITTEL, OccupancyCategory.fromPercentage(75))
        assertEquals(OccupancyCategory.VOLL, OccupancyCategory.fromPercentage(76))
        assertEquals(OccupancyCategory.VOLL, OccupancyCategory.fromPercentage(100))
    }

    @Test
    fun testClubAnalyticsUiState_computedProperties_formatsCorrectly() {
        val state = ClubAnalyticsUiState(
            currentCapacityPercent = 85,
            totalLiveVisitors = 1275,
            maxCapacity = 1500,
            occupancyCategory = OccupancyCategory.VOLL
        )

        assertEquals(0.85f, state.occupancyRate, 0.001f)
        assertEquals("85%", state.formattedCapacityPercent)
        assertEquals("1275 / 1500 Gäste", state.formattedVisitorCount)
    }

    @Test
    fun testUpdateVisitorStats_emitsUpdatedOccupancyAndCategoryState() {
        viewModel.updateVisitorStats(
            capacityPercent = 82,
            totalVisitors = 1230,
            maxCapacity = 1500,
            trend = OccupancyTrend.RISING
        )

        val state = viewModel.uiState.value

        assertEquals(82, state.currentCapacityPercent)
        assertEquals(1230, state.totalLiveVisitors)
        assertEquals(1500, state.maxCapacity)
        assertEquals(OccupancyCategory.VOLL, state.occupancyCategory)
        assertEquals(OccupancyTrend.RISING, state.occupancyTrend)
        assertTrue(state.isLive)
    }

    @Test
    fun testObserveClubAnalytics_populatesLiveOccupancyMetrics() = runTest {
        val clubId = "club_berghain"
        val mockGenderRatio = GenderRatio.calculate(
            maleCount = 550,
            femaleCount = 450,
            diverseCount = 100
        )

        `when`(clubRepository.getClubGenderRatio(clubId)).thenReturn(flowOf(mockGenderRatio))

        viewModel.observeClubAnalytics(clubId)

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(clubId, state.clubId)
        assertEquals(1100, state.totalLiveVisitors)
        assertEquals(73, state.currentCapacityPercent)
        assertEquals(OccupancyCategory.MITTEL, state.occupancyCategory)
        assertTrue(state.isLive)
    }
}
