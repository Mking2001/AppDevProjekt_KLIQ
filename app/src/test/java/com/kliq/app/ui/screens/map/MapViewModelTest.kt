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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Unit tests for [MapViewModel] validating ClubRepository flow integration,
 * dynamic marker clustering, camera position management, venue filtering,
 * edge case handling, and quick view popup states.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

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
    fun testInitialStateLoadsClubsFromRepositoryAndClusters() {
        val state = viewModel.uiState.value
        assertTrue(state.filters.isNotEmpty())
        assertEquals(2, state.nearbyVenues.size)
        assertTrue(state.clusteredMarkers.isNotEmpty())
        assertNull(state.selectedFilter)
        assertNull(state.selectedVenue)
        assertFalse(state.isLocationEnabled)
    }

    @Test
    fun testFilterSelectionTogglesCorrectIndexAndFiltersVenues() {
        viewModel.onFilterSelected(1) // "Clubs"
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.selectedFilter)
        assertEquals(2, viewModel.uiState.value.nearbyVenues.size)

        // Select "Events" filter (index 3) - only Berghain (c1) has activeEvent
        viewModel.onFilterSelected(3)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.selectedFilter)
        assertEquals(1, viewModel.uiState.value.nearbyVenues.size)
        assertEquals("c1", viewModel.uiState.value.nearbyVenues.first().id)

        // Toggle off when selecting same filter
        viewModel.onFilterSelected(3)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.selectedFilter)
        assertEquals(2, viewModel.uiState.value.nearbyVenues.size)
    }

    @Test
    fun testLocationRequestedUpdatesCameraPositionAndLocationState() {
        viewModel.onLocationRequested()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isLocationEnabled)
        assertFalse(state.isLoadingLocation)
        assertEquals(52.5112, state.cameraPosition.latitude, 0.0001)
        assertEquals(13.4430, state.cameraPosition.longitude, 0.0001)
        assertEquals(15.0f, state.cameraPosition.zoom)
    }

    @Test
    fun testMarkerClickedUpdatesSelectedVenueAndCameraPosition() {
        val venue = viewModel.uiState.value.nearbyVenues.first()
        viewModel.onMarkerClicked(venue)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.selectedVenue)
        assertEquals(venue.id, state.selectedVenue?.id)
        assertEquals(venue.latitude, state.cameraPosition.latitude, 0.0001)
        assertEquals(venue.longitude, state.cameraPosition.longitude, 0.0001)
        assertEquals(16.0f, state.cameraPosition.zoom)
    }

    @Test
    fun testClusterClickedZoomsInCamera() {
        val initialZoom = viewModel.uiState.value.cameraPosition.zoom
        val clusterNode = ClusterMarkerUiState.ClusterNode(
            clusterId = "cluster_1",
            count = 2,
            centerLat = 52.5100,
            centerLng = 13.4400,
            items = viewModel.uiState.value.nearbyVenues,
            primaryCategory = "Club"
        )

        viewModel.onClusterClicked(clusterNode)
        testDispatcher.scheduler.advanceUntilIdle()

        val newZoom = viewModel.uiState.value.cameraPosition.zoom
        assertEquals(initialZoom + 2.0f, newZoom, 0.01f)
        assertEquals(52.5100, viewModel.uiState.value.cameraPosition.latitude, 0.0001)
    }

    @Test
    fun testQuickViewDismissedClearsSelectedVenue() {
        val venue = viewModel.uiState.value.nearbyVenues.first()
        viewModel.onMarkerClicked(venue)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.selectedVenue)

        viewModel.onQuickViewDismissed()
        testDispatcher.scheduler.advanceUntilIdle()
        assertNull(viewModel.uiState.value.selectedVenue)
    }

    @Test
    fun testOnMapLoadedUpdatesLoadedState() {
        assertFalse(viewModel.uiState.value.isMapLoaded)
        viewModel.onMapLoaded()
        assertTrue(viewModel.uiState.value.isMapLoaded)
    }

    @Test
    fun testEdgeCase_emptyRepositoryFlow_usesFallbackVenuesSafely() {
        `when`(clubRepository.getAllClubs()).thenReturn(flowOf(emptyList()))
        val fallbackVm = MapViewModel(clubRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = fallbackVm.uiState.value
        assertTrue(state.nearbyVenues.isNotEmpty())
        assertEquals(4, state.nearbyVenues.size) // Default 4 fallback venues
    }

    @Test
    fun testEdgeCase_clubWithActiveEvent_populatesActiveEventDetailsInUiState() {
        val state = viewModel.uiState.value
        val berghain = state.nearbyVenues.first { it.id == "c1" }
        assertEquals("Klubnacht", berghain.activeEventTitle)
    }

    @Test
    fun testEdgeCase_clubWithEmptyCategory_defaultsToClubCategory() {
        val clubWithEmptyCategory = Club(
            id = "c3",
            name = "Mystery Venue",
            location = GpsLocation(52.5200, 13.4000, "Unknown Street"),
            averageRating = 4.0,
            operatingHours = OperatingHours(false, ""),
            category = ""
        )
        `when`(clubRepository.getAllClubs()).thenReturn(flowOf(listOf(clubWithEmptyCategory)))
        val customVm = MapViewModel(clubRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val venue = customVm.uiState.value.nearbyVenues.first()
        assertEquals("Club", venue.category)
    }
}
