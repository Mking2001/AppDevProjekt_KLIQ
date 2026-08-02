package com.kliq.app.viewmodel

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.model.RegionSearchResult
import com.kliq.app.data.model.SearchFilterType
import com.kliq.app.data.repository.ClubRepository
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@OptIn(ExperimentalCoroutinesApi::class)
class ClubSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ClubRepository = mock(ClubRepository::class.java)
    private lateinit var viewModel: ClubSearchViewModel

    private val sampleClub = Club(
        id = "club_matrix",
        name = "Matrix Club Berlin",
        location = GpsLocation(latitude = 52.503, longitude = 13.447, address = "Warschauer Str. 18, Berlin"),
        geofenceRadiusMeters = 250.0,
        averageRating = 4.5,
        operatingHours = OperatingHours(isOpenNow = true, todayHours = "22:00 - 06:00"),
        isFavorite = false,
        category = "Techno",
        region = "Berlin"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(repository.searchClubsFiltered("", null, null)).thenReturn(flowOf(listOf(sampleClub)))
        `when`(repository.searchRegionsAndCities("")).thenReturn(flowOf(listOf(RegionSearchResult("Berlin", 1, true))))
        viewModel = ClubSearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialSearchState_loadsDefaultResults() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("", state.searchQuery)
        assertEquals(SearchFilterType.ALL, state.activeFilter)
        assertEquals(1, state.clubResults.size)
        assertEquals("Matrix Club Berlin", state.clubResults[0].name)
        assertEquals(1, state.regionResults.size)
        assertEquals("Berlin", state.regionResults[0].regionName)
    }

    @Test
    fun onQueryChanged_updatesStateAndDebounces() = runTest {
        `when`(repository.searchClubsFiltered("Matrix", null, null)).thenReturn(flowOf(listOf(sampleClub)))
        `when`(repository.searchRegionsAndCities("Matrix")).thenReturn(flowOf(emptyList()))

        viewModel.onQueryChanged("Matrix")
        assertEquals("Matrix", viewModel.uiState.value.searchQuery)

        testDispatcher.scheduler.advanceTimeBy(350L)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.clubResults.size)
        assertEquals("Matrix Club Berlin", state.clubResults[0].name)
    }

    @Test
    fun onFilterChanged_updatesActiveFilter() = runTest {
        viewModel.onFilterChanged(SearchFilterType.REGION)
        assertEquals(SearchFilterType.REGION, viewModel.uiState.value.activeFilter)
    }

    @Test
    fun selectRegion_togglesRegionFilter() = runTest {
        viewModel.selectRegion("Berlin")
        assertEquals("Berlin", viewModel.uiState.value.selectedRegion)

        viewModel.selectRegion("Berlin")
        assertNull(viewModel.uiState.value.selectedRegion)
    }

    @Test
    fun clearSearch_resetsQueryAndFilters() = runTest {
        viewModel.onQueryChanged("Berlin Party")
        viewModel.selectRegion("Berlin")
        viewModel.clearSearch()

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertNull(state.selectedRegion)
        assertNull(state.selectedGenre)
    }

    @Test
    fun setUserLocation_enablesGpsAndCalculatesDistances() = runTest {
        viewModel.setUserLocation(52.520, 13.405)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isGpsActive)
        assertEquals(52.520, state.userLatitude!!, 0.001)
        assertEquals(13.405, state.userLongitude!!, 0.001)
    }

    @Test
    fun toggleFavorite_invokesRepository() = runTest {
        viewModel.toggleFavorite("club_matrix", false)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(repository).toggleFavorite("club_matrix", false)
    }
}
