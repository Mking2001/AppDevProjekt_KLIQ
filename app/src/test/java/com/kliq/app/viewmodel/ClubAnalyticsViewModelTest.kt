package com.kliq.app.viewmodel

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GenderRatio
import com.kliq.app.data.repository.ClubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
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

@OptIn(ExperimentalCoroutinesApi::class)
class ClubAnalyticsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeRepository = FakeClubRepository()

    private lateinit var viewModel: ClubAnalyticsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ClubAnalyticsViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testObserveClubAnalyticsWithValidClubIdEmitsSegments() = runTest {
        val clubId = "club_watergate"
        fakeRepository.genderRatioToReturn = GenderRatio(
            malePercentage = 50f,
            femalePercentage = 40f,
            diversePercentage = 10f,
            totalVisitorsCount = 20,
            hasSufficientData = true
        )

        viewModel.observeClubAnalytics(clubId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(clubId, state.clubId)
        assertEquals(20, state.totalLiveVisitors)
        assertEquals(3, state.segments.size)

        val femaleSegment = state.segments.find { it.label == "Weiblich" }
        assertNotNull(femaleSegment)
        assertEquals(40f, femaleSegment?.percentage)

        val maleSegment = state.segments.find { it.label == "Männlich" }
        assertNotNull(maleSegment)
        assertEquals(50f, maleSegment?.percentage)
    }

    @Test
    fun testObserveClubAnalyticsBlankClubIdSetsError() = runTest {
        viewModel.observeClubAnalytics("")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Ungültige Club ID", state.errorMessage)
    }

    @Test
    fun testRefreshAnalyticsUpdatesState() = runTest {
        val clubId = "club_matrix"
        fakeRepository.genderRatioToReturn = GenderRatio(
            malePercentage = 60f,
            femalePercentage = 40f,
            diversePercentage = 0f,
            totalVisitorsCount = 15,
            hasSufficientData = true
        )

        viewModel.refreshAnalytics(clubId)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.segments.size)
        assertEquals(15, state.totalLiveVisitors)
    }

    private class FakeClubRepository : ClubRepository {
        var genderRatioToReturn: GenderRatio = GenderRatio()

        override fun getAllClubs(): Flow<List<Club>> = flowOf(emptyList())
        override fun getFavoriteClubs(): Flow<List<Club>> = flowOf(emptyList())
        override fun getClubById(clubId: String): Flow<Club?> = flowOf(null)
        override fun searchClubsLocal(query: String): Flow<List<Club>> = flowOf(emptyList())
        override fun searchClubsFiltered(query: String, regionFilter: String?, genreFilter: String?): Flow<List<Club>> = flowOf(emptyList())
        override fun searchRegionsAndCities(query: String): Flow<List<com.kliq.app.data.model.RegionSearchResult>> = flowOf(emptyList())
        override suspend fun toggleFavorite(clubId: String, currentFavoriteState: Boolean) {}
        override suspend fun searchExternalClubs(query: String, userLat: Double?, userLon: Double?, radiusKm: Int): Result<List<Club>> = Result.success(emptyList())
        override suspend fun isUserWithinGeofence(clubId: String, userLat: Double, userLon: Double): Boolean = false

        override fun getClubGenderRatio(clubId: String, timeWindowMs: Long): Flow<GenderRatio> {
            return flowOf(genderRatioToReturn)
        }

        override suspend fun calculateClubGenderRatio(clubId: String, timeWindowMs: Long): GenderRatio {
            return genderRatioToReturn
        }

        override suspend fun toggleClubHype(clubId: String, userId: String): Result<Boolean> = Result.success(true)
        override fun isClubHypedToday(clubId: String, userId: String): Flow<Boolean> = flowOf(false)
        override fun getHypedClubIdsToday(userId: String): Flow<List<String>> = flowOf(emptyList())
    }
}
