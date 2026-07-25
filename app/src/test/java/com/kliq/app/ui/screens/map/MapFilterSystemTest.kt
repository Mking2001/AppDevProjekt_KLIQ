package com.kliq.app.ui.screens.map

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.Event
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.repository.ClubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Unit tests validating [MapViewModel] location filter modes (ALL, PUBLIC_ONLY, PRIVATE_ONLY),
 * marker visibility StateFlow reactivity, privacy location sharing enforcement, and category combinations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MapFilterSystemTest {

    private val testDispatcher = StandardTestDispatcher()
    private val clubRepository: ClubRepository = mock(ClubRepository::class.java)
    private lateinit var viewModel: MapViewModel

    private val testClubs = listOf(
        Club(
            id = "c1",
            name = "Berghain",
            location = GpsLocation(52.5112, 13.4430, "Am Wriezener Bahnhof"),
            averageRating = 4.9,
            operatingHours = OperatingHours(true, "00:00 - 24:00"),
            category = "Club",
            activeEvent = Event(
                id = "e1",
                clubId = "c1",
                title = "Klubnacht",
                description = "Techno Special",
                startTime = 1700000000000L,
                endTime = 1700086400000L,
                price = "20€"
            )
        ),
        Club(
            id = "c2",
            name = "Watergate",
            location = GpsLocation(52.5011, 13.4452, "Falckensteinstraße 49"),
            averageRating = 4.7,
            operatingHours = OperatingHours(true, "23:00 - 06:00"),
            category = "Club"
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(clubRepository.getAllClubs()).thenReturn(flowOf(testClubs))
        viewModel = MapViewModel(clubRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState_defaultsToAllFilterModeAndShowsBothPublicAndPrivateLocations() {
        val state = viewModel.uiState.value
        assertEquals(MapLocationFilterMode.ALL, state.locationFilterMode)
        assertTrue(state.showPublicEvents)
        assertTrue(state.showPrivateLocations)
        assertEquals(2, state.clubMarkers.size)
        assertTrue(state.userMarkers.isNotEmpty())
    }

    @Test
    fun testPublicOnlyFilterMode_hidesUserMarkersAndShowsVenues() {
        viewModel.onLocationFilterModeSelected(MapLocationFilterMode.PUBLIC_ONLY)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(MapLocationFilterMode.PUBLIC_ONLY, state.locationFilterMode)
        assertTrue(state.showPublicEvents)
        assertFalse(state.showPrivateLocations)
        assertEquals(2, state.clubMarkers.size)
        assertTrue(state.userMarkers.isEmpty())
    }

    @Test
    fun testPrivateOnlyFilterMode_hidesPublicVenuesAndShowsUserMarkers() {
        viewModel.onLocationFilterModeSelected(MapLocationFilterMode.PRIVATE_ONLY)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(MapLocationFilterMode.PRIVATE_ONLY, state.locationFilterMode)
        assertFalse(state.showPublicEvents)
        assertTrue(state.showPrivateLocations)
        assertTrue(state.clubMarkers.isEmpty())
        assertTrue(state.nearbyVenues.isEmpty())
        assertTrue(state.clusteredMarkers.isEmpty())
        assertTrue(state.userMarkers.isNotEmpty())
    }

    @Test
    fun testPrivacyEnforcement_userWithDisabledLocationSharing_isExcludedFromUserMarkers() {
        val state = viewModel.uiState.value
        val hiddenUser = state.userMarkers.find { it.userId == "u4" }
        // User u4 has isLocationSharingEnabled = false and must not be present in userMarkers
        assertTrue(hiddenUser == null)
        assertTrue(state.userMarkers.all { it.isLocationSharingEnabled })
    }

    @Test
    fun testCombinedFilter_publicOnlyWithCategoryFilter_filtersCategoryCorrectly() {
        viewModel.onLocationFilterModeSelected(MapLocationFilterMode.PUBLIC_ONLY)
        viewModel.onFilterSelected(3) // "Events" filter
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.clubMarkers.size)
        assertEquals("c1", state.clubMarkers.first().id)
        assertTrue(state.userMarkers.isEmpty())
    }
}
