package com.kliq.app.ui.screens.map

import com.kliq.app.data.repository.ClubRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

/**
 * Unit tests for [MapViewModel] validating camera position management,
 * filter toggling, venue selection, and map styling configuration.
 */
class MapViewModelTest {

    private val clubRepository: ClubRepository = mock(ClubRepository::class.java)
    private lateinit var viewModel: MapViewModel

    @Before
    fun setUp() {
        viewModel = MapViewModel(clubRepository)
    }

    @Test
    fun testInitialStateLoadsMockVenuesAndFilters() {
        val state = viewModel.uiState.value
        assertTrue(state.filters.isNotEmpty())
        assertTrue(state.nearbyVenues.isNotEmpty())
        assertNull(state.selectedFilter)
        assertNull(state.selectedVenue)
        assertFalse(state.isLocationEnabled)
    }

    @Test
    fun testFilterSelectionTogglesCorrectIndex() {
        viewModel.onFilterSelected(1)
        assertEquals(1, viewModel.uiState.value.selectedFilter)

        // Toggle off when selecting same filter
        viewModel.onFilterSelected(1)
        assertNull(viewModel.uiState.value.selectedFilter)
    }

    @Test
    fun testLocationRequestedUpdatesCameraPositionAndLocationState() {
        viewModel.onLocationRequested()
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

        val state = viewModel.uiState.value
        assertNotNull(state.selectedVenue)
        assertEquals(venue.id, state.selectedVenue?.id)
        assertEquals(venue.latitude, state.cameraPosition.latitude, 0.0001)
        assertEquals(venue.longitude, state.cameraPosition.longitude, 0.0001)
        assertEquals(16.0f, state.cameraPosition.zoom)
    }

    @Test
    fun testQuickViewDismissedClearsSelectedVenue() {
        val venue = viewModel.uiState.value.nearbyVenues.first()
        viewModel.onMarkerClicked(venue)
        assertNotNull(viewModel.uiState.value.selectedVenue)

        viewModel.onQuickViewDismissed()
        assertNull(viewModel.uiState.value.selectedVenue)
    }

    @Test
    fun testOnMapLoadedUpdatesLoadedState() {
        assertFalse(viewModel.uiState.value.isMapLoaded)
        viewModel.onMapLoaded()
        assertTrue(viewModel.uiState.value.isMapLoaded)
    }
}
