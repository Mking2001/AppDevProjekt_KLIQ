package com.kliq.app.ui.screens.map

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.ClubAnalytics
import com.kliq.app.data.model.Event
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.seed.KlagenfurtSeedData
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
 * dynamic marker clustering, separate ClubMarkerUiState and UserMarkerUiState,
 * camera position management, venue filtering, long-press gesture quick-view trigger,
 * live visitor stats and gender ratio mapping.
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
            analytics = ClubAnalytics(
                currentCapacityPercent = 85,
                malePercentage = 52,
                femalePercentage = 48,
                totalLiveVisitors = 380
            ),
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
            category = "Club",
            analytics = ClubAnalytics(
                currentCapacityPercent = 60,
                malePercentage = 48,
                femalePercentage = 52,
                totalLiveVisitors = 210
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(clubRepository.getAllClubs()).thenReturn(flowOf(testClubs))
        viewModel = MapViewModel(
            clubRepository = clubRepository,
            defaultDispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateLoadsClubsAndUsers() {
        val state = viewModel.uiState.value
        assertTrue(state.filters.isNotEmpty())
        assertEquals(2, state.nearbyVenues.size)
        assertEquals(2, state.clubMarkers.size)
        assertTrue(state.userMarkers.isNotEmpty())
        assertTrue(state.clusteredMarkers.isNotEmpty())
        assertNull(state.selectedFilter)
        assertNull(state.selectedVenue)
        assertNull(state.selectedUser)
        assertFalse(state.isLocationEnabled)
    }

    @Test
    fun testClubMarkerUiStateMapping_populatesEventDetailsCorrectly() {
        val clubMarker = viewModel.uiState.value.clubMarkers.first { it.id == "c1" }
        assertEquals("Berghain", clubMarker.name)
        assertTrue(clubMarker.hasActiveEvent)
        assertEquals("Klubnacht", clubMarker.activeEventTitle)
        assertEquals(52.5112, clubMarker.latitude, 0.0001)
    }

    @Test
    fun testUserMarkerClicked_updatesSelectedUserAndCameraPosition() {
        val userMarker = viewModel.uiState.value.userMarkers.first()
        viewModel.onUserMarkerClicked(userMarker)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.selectedUser)
        assertEquals(userMarker.userId, state.selectedUser?.userId)
        assertNull(state.selectedVenue)
        assertEquals(userMarker.latitude, state.cameraPosition.latitude, 0.0001)
        assertEquals(16.0f, state.cameraPosition.zoom)
    }

    @Test
    fun testClubMarkerClicked_updatesSelectedVenueAndClearsSelectedUser() {
        val userMarker = viewModel.uiState.value.userMarkers.first()
        viewModel.onUserMarkerClicked(userMarker)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.selectedUser)

        val clubMarker = viewModel.uiState.value.clubMarkers.first()
        viewModel.onClubMarkerClicked(clubMarker)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.selectedVenue)
        assertEquals(clubMarker.id, state.selectedVenue?.id)
        assertNull(state.selectedUser)
    }

    @Test
    fun testUserQuickViewDismissed_clearsOnlySelectedUser() {
        val userMarker = viewModel.uiState.value.userMarkers.first()
        viewModel.onUserMarkerClicked(userMarker)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.selectedUser)

        viewModel.onUserQuickViewDismissed()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.selectedUser)
    }

    @Test
    fun testQuickViewDismissed_clearsBothSelectedVenueAndUser() {
        val userMarker = viewModel.uiState.value.userMarkers.first()
        viewModel.onUserMarkerClicked(userMarker)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.selectedUser)

        viewModel.onQuickViewDismissed()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.selectedVenue)
        assertNull(viewModel.uiState.value.selectedUser)
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
        // Ohne LocationRepository faellt onLocationRequested() auf das
        // Klagenfurt-Stadtzentrum aus KlagenfurtSeedData zurueck.
        viewModel.onLocationRequested()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isLocationEnabled)
        assertFalse(state.isLoadingLocation)
        assertEquals(KlagenfurtSeedData.CITY_LATITUDE, state.cameraPosition.latitude, 0.0001)
        assertEquals(KlagenfurtSeedData.CITY_LONGITUDE, state.cameraPosition.longitude, 0.0001)
        assertEquals(15.0f, state.cameraPosition.zoom)
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
    fun testOnMapLoadedUpdatesLoadedState() {
        assertFalse(viewModel.uiState.value.isMapLoaded)
        viewModel.onMapLoaded()
        assertTrue(viewModel.uiState.value.isMapLoaded)
    }

    @Test
    fun testMarkerLongPressed_triggersQuickViewAndUpdatesVenueState() {
        val venue = viewModel.uiState.value.nearbyVenues.first { it.id == "c1" }
        assertNull(viewModel.uiState.value.selectedVenue)

        viewModel.onMarkerLongPressed(venue)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.selectedVenue)
        assertEquals("c1", state.selectedVenue?.id)
        assertEquals("Berghain", state.selectedVenue?.name)
        assertEquals(380, state.selectedVenue?.totalLiveVisitors)
        assertEquals(52, state.selectedVenue?.malePercentage)
        assertEquals(48, state.selectedVenue?.femalePercentage)
    }

    @Test
    fun testVenueItemUi_mapsAnalyticsGenderRatioAndVisitorCount() {
        val berghain = viewModel.uiState.value.nearbyVenues.first { it.id == "c1" }
        val watergate = viewModel.uiState.value.nearbyVenues.first { it.id == "c2" }

        assertEquals(380, berghain.totalLiveVisitors)
        assertEquals(52, berghain.malePercentage)
        assertEquals(48, berghain.femalePercentage)

        assertEquals(210, watergate.totalLiveVisitors)
        assertEquals(48, watergate.malePercentage)
        assertEquals(52, watergate.femalePercentage)
    }

    @Test
    fun testEdgeCase_emptyRepositoryFlow_usesFallbackVenuesSafely() {
        // Bleibt die Room-Datenbank leer, faellt das ViewModel auf den
        // Klagenfurt-Demonstrationsdatensatz zurueck (KlagenfurtSeedData.clubs()).
        `when`(clubRepository.getAllClubs()).thenReturn(flowOf(emptyList()))
        val fallbackVm = MapViewModel(clubRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedClubs = KlagenfurtSeedData.clubs()
        val state = fallbackVm.uiState.value
        assertTrue(state.nearbyVenues.isNotEmpty())
        assertEquals(expectedClubs.size, state.nearbyVenues.size)
        assertEquals(expectedClubs.size, state.clubMarkers.size)

        val firstExpected = expectedClubs.first()
        val firstActual = state.nearbyVenues.first { it.id == firstExpected.id }
        assertEquals(firstExpected.totalLiveVisitors, firstActual.totalLiveVisitors)
        assertEquals(firstExpected.malePercentage, firstActual.malePercentage)
    }
}
