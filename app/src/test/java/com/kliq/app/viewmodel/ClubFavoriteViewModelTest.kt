package com.kliq.app.viewmodel

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.ui.screens.club.ClubViewModel
import com.kliq.app.ui.screens.map.MapViewModel
import com.kliq.app.ui.screens.map.VenueItemUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class ClubFavoriteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val clubRepository: ClubRepository = mock(ClubRepository::class.java)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun clubViewModel_loadClubDetails_observesRepositoryAndSetsState() = runTest {
        val testClub = Club(
            id = "c_kitkat",
            name = "KitKatClub",
            location = GpsLocation(52.5114, 13.4172, "Köpenicker Str. 76"),
            averageRating = 4.6,
            operatingHours = OperatingHours(isOpenNow = true),
            isFavorite = true
        )
        `when`(clubRepository.getClubById("c_kitkat")).thenReturn(flowOf(testClub))

        val viewModel = ClubViewModel(clubRepository)
        viewModel.loadClubDetails("c_kitkat")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("KitKatClub", state.club?.name)
        assertTrue(state.club?.isFavorite == true)
    }

    @Test
    fun clubViewModel_toggleFavorite_updatesStateAndCallsRepository() = runTest {
        val testClub = Club(
            id = "c_kitkat",
            name = "KitKatClub",
            location = GpsLocation(52.5114, 13.4172, "Köpenicker Str. 76"),
            averageRating = 4.6,
            operatingHours = OperatingHours(isOpenNow = true),
            isFavorite = false
        )
        `when`(clubRepository.getClubById("c_kitkat")).thenReturn(flowOf(testClub))

        val viewModel = ClubViewModel(clubRepository)
        viewModel.loadClubDetails("c_kitkat")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleFavorite()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.club?.isFavorite == true)
        verify(clubRepository).toggleFavorite("c_kitkat", false)
    }

    @Test
    fun mapViewModel_toggleFavorite_updatesSelectedVenueAndRepository() = runTest {
        val testClub = Club(
            id = "c_watergate",
            name = "Watergate",
            location = GpsLocation(52.5011, 13.4452, "Falckensteinstraße 49"),
            averageRating = 4.7,
            operatingHours = OperatingHours(isOpenNow = true),
            isFavorite = false
        )
        `when`(clubRepository.getAllClubs()).thenReturn(flowOf(listOf(testClub)))

        val viewModel = MapViewModel(
            clubRepository = clubRepository,
            defaultDispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val venue = VenueItemUi(
            id = "c_watergate",
            name = "Watergate",
            category = "Club",
            distance = "0.7 km",
            isFavorite = false
        )
        viewModel.onMarkerClicked(venue)

        viewModel.toggleFavorite("c_watergate", currentFavoriteState = false)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.selectedVenue?.isFavorite == true)
        verify(clubRepository).toggleFavorite("c_watergate", false)
    }
}
