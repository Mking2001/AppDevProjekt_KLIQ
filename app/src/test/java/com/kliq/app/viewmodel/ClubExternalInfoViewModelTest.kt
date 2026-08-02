package com.kliq.app.viewmodel

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GenderRatio
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.LiveOpeningStatus
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.model.RegionSearchResult
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
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class ClubExternalInfoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeRepository = FakeClubRepository()

    private lateinit var viewModel: ClubExternalInfoViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ClubExternalInfoViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadExternalClubInfo with valid id loads data`() = runTest {
        viewModel.loadExternalClubInfo("club_123")
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals("club_123", state.clubId)
        assertTrue(state.address.isNotBlank())
        assertNotNull(state.websiteUrl)
    }

    @Test
    fun `loadExternalClubInfo with blank id sets error state`() = runTest {
        viewModel.loadExternalClubInfo("")
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals("Ungültige Club ID", state.errorMessage)
    }

    @Test
    fun `updateStateFromClub correctly sets ui state and computes live status`() = runTest {
        val mockClub = Club(
            id = "test_club",
            name = "Test Club",
            location = GpsLocation(52.52, 13.40, "Alexanderplatz 1, Berlin"),
            averageRating = 4.5,
            operatingHours = OperatingHours(
                isOpenNow = true,
                todayHours = "23:00 - 06:00",
                weeklySchedule = mapOf("Freitag" to "23:00 - 06:00")
            ),
            websiteUrl = "https://testclub.de",
            phoneNumber = "+49 30 123456",
            contactEmail = "contact@testclub.de"
        )

        viewModel.updateStateFromClub(mockClub, currentTime = LocalTime.of(1, 0))
        val state = viewModel.uiState.value

        assertEquals("test_club", state.clubId)
        assertEquals("Test Club", state.clubName)
        assertEquals("https://testclub.de", state.websiteUrl)
        assertEquals("+49 30 123456", state.phoneNumber)
        assertEquals(LiveOpeningStatus.OPEN_NOW, state.liveStatus)
    }

    @Test
    fun `intent uri helpers return correctly formatted URIs`() = runTest {
        viewModel.loadExternalClubInfo("club_123")
        testDispatcher.scheduler.advanceUntilIdle()

        val webUri = viewModel.getWebsiteIntentUri()
        val phoneUri = viewModel.getPhoneDialUri()
        val navUri = viewModel.getNavigationUri()

        assertTrue(webUri?.startsWith("https://") == true)
        assertTrue(phoneUri?.startsWith("tel:") == true)
        assertTrue(navUri.startsWith("geo:0,0?q="))
    }

    private class FakeClubRepository : ClubRepository {
        override fun getAllClubs(): Flow<List<Club>> = flowOf(emptyList())
        override fun getFavoriteClubs(): Flow<List<Club>> = flowOf(emptyList())
        override fun getClubById(clubId: String): Flow<Club?> = flowOf(null)
        override fun searchClubsLocal(query: String): Flow<List<Club>> = flowOf(emptyList())
        override fun searchClubsFiltered(query: String, regionFilter: String?, genreFilter: String?): Flow<List<Club>> = flowOf(emptyList())
        override fun searchRegionsAndCities(query: String): Flow<List<RegionSearchResult>> = flowOf(emptyList())
        override suspend fun toggleFavorite(clubId: String, currentFavoriteState: Boolean) {}
        override suspend fun searchExternalClubs(query: String, userLat: Double?, userLon: Double?, radiusKm: Int): Result<List<Club>> = Result.success(emptyList())
        override suspend fun isUserWithinGeofence(clubId: String, userLat: Double, userLon: Double): Boolean = false
        override fun getClubGenderRatio(clubId: String, timeWindowMs: Long): Flow<GenderRatio> = flowOf(GenderRatio())
        override suspend fun calculateClubGenderRatio(clubId: String, timeWindowMs: Long): GenderRatio = GenderRatio()
    }
}
