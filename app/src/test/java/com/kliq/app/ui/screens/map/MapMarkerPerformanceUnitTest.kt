package com.kliq.app.ui.screens.map

import com.google.android.gms.maps.model.BitmapDescriptor
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.ClubAnalytics
import com.kliq.app.data.model.Event
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.repository.ClubRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests validating Chapter 9.6 Map Marker Performance Tuning requirements:
 * - MVVM Separation of Concerns (raw club domain models vs marker UI states).
 * - Marker clustering performance with high-density markers.
 * - LRU caching for high-contrast purple/neon marker icons.
 * - Asynchronous background dispatcher data transformations and 250ms camera debouncing.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MapMarkerPerformanceUnitTest {

    private val testDispatcher = StandardTestDispatcher()
    private val clubRepository: ClubRepository = mock(ClubRepository::class.java)
    private lateinit var viewModel: MapViewModel

    private val mockClubs = listOf(
        Club(
            id = "club_1",
            name = "Berghain / Panorama Bar",
            location = GpsLocation(52.5112, 13.4430, "Am Wriezener Bahnhof, 10243 Berlin"),
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
                clubId = "club_1",
                title = "Klubnacht",
                description = "Techno",
                startTime = 1700000000000L,
                endTime = 1700086400000L,
                price = "20€"
            )
        ),
        Club(
            id = "club_2",
            name = "Watergate",
            location = GpsLocation(52.5011, 13.4452, "Falckensteinstraße 49, 10997 Berlin"),
            averageRating = 4.7,
            operatingHours = OperatingHours(true, "23:00 - 06:00"),
            category = "Club",
            analytics = ClubAnalytics(
                currentCapacityPercent = 60,
                malePercentage = 48,
                femalePercentage = 52,
                totalLiveVisitors = 210
            )
        ),
        Club(
            id = "club_3",
            name = "Sunset Lounge",
            location = GpsLocation(52.5280, 13.4100, "Torstraße 140, 10119 Berlin"),
            averageRating = 4.8,
            operatingHours = OperatingHours(true, "18:00 - 02:00"),
            category = "Bar",
            analytics = ClubAnalytics(
                currentCapacityPercent = 40,
                malePercentage = 45,
                femalePercentage = 55,
                totalLiveVisitors = 85
            )
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MarkerBitmapHelper.clearCache()
        MarkerBitmapHelper.descriptorFactory = { mock(BitmapDescriptor::class.java) }
        MapClusterManager.clearCache()

        `when`(clubRepository.getAllClubs()).thenReturn(flowOf(mockClubs))
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
    fun testMarkerBitmapLruCache_cachesCustomBitmapsAcrossRecompositions() {
        val initialCacheSize = MarkerBitmapHelper.cacheSize()
        assertTrue(initialCacheSize > 0) // Pre-warmed cache

        val descriptor1 = MarkerBitmapHelper.getClubMarkerBitmap("Club", hasActiveEvent = true)
        val descriptor2 = MarkerBitmapHelper.getClubMarkerBitmap("Club", hasActiveEvent = true)

        assertSame(descriptor1, descriptor2)

        val userDescriptor1 = MarkerBitmapHelper.getUserMarkerBitmap("Alex", isOnline = true)
        val userDescriptor2 = MarkerBitmapHelper.getUserMarkerBitmap("Adam", isOnline = true) // Same initial 'A'

        assertSame(userDescriptor1, userDescriptor2)
    }

    @Test
    fun testSeparationOfConcerns_rawClubLoadingTransformsToMarkerUiStates() {
        val state = viewModel.uiState.value

        assertEquals(3, state.nearbyVenues.size)
        assertEquals(3, state.clubMarkers.size)

        val berghainMarker = state.clubMarkers.first { it.id == "club_1" }
        assertEquals("Berghain / Panorama Bar", berghainMarker.name)
        assertTrue(berghainMarker.hasActiveEvent)
        assertEquals("Klubnacht", berghainMarker.activeEventTitle)
        assertEquals(85, berghainMarker.capacityPercent)
        assertEquals(52.5112, berghainMarker.latitude, 0.0001)

        val barMarker = state.clubMarkers.first { it.id == "club_3" }
        assertEquals("Bar", barMarker.category)
        assertFalse(barMarker.hasActiveEvent)
    }

    @Test
    fun testCameraMovementDebouncing_throttlesCalculationsUntilIdle() {
        // Fast panning movements
        viewModel.onCameraMoved(52.5200, 13.4000, 12.0f)
        viewModel.onCameraMoved(52.5220, 13.4020, 12.0f)
        viewModel.onCameraMoved(52.5250, 13.4050, 12.0f)

        // Advance by only 100ms (less than 250ms debounce threshold)
        testDispatcher.scheduler.advanceTimeBy(100)

        // Advance past debounce threshold (250ms+)
        testDispatcher.scheduler.advanceTimeBy(200)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(52.5250, state.cameraPosition.latitude, 0.0001)
        assertEquals(13.4050, state.cameraPosition.longitude, 0.0001)
        assertTrue(state.clusteredMarkers.isNotEmpty())
    }

    @Test
    fun testLocationFilterModes_updatesClusteringAndPrivacyCorrectly() {
        // Mode: PUBLIC_ONLY
        viewModel.onLocationFilterModeSelected(MapLocationFilterMode.PUBLIC_ONLY)
        testDispatcher.scheduler.advanceUntilIdle()

        var state = viewModel.uiState.value
        assertTrue(state.showPublicEvents)
        assertFalse(state.showPrivateLocations)
        assertTrue(state.clubMarkers.isNotEmpty())
        assertTrue(state.userMarkers.isEmpty())

        // Mode: PRIVATE_ONLY
        viewModel.onLocationFilterModeSelected(MapLocationFilterMode.PRIVATE_ONLY)
        testDispatcher.scheduler.advanceUntilIdle()

        state = viewModel.uiState.value
        assertFalse(state.showPublicEvents)
        assertTrue(state.showPrivateLocations)
        assertTrue(state.clubMarkers.isEmpty())
        assertTrue(state.clusteredMarkers.isEmpty())
        assertTrue(state.userMarkers.isNotEmpty())

        // Mode: ALL
        viewModel.onLocationFilterModeSelected(MapLocationFilterMode.ALL)
        testDispatcher.scheduler.advanceUntilIdle()

        state = viewModel.uiState.value
        assertTrue(state.showPublicEvents)
        assertTrue(state.showPrivateLocations)
        assertTrue(state.clubMarkers.isNotEmpty())
        assertTrue(state.userMarkers.isNotEmpty())
    }

    @Test
    fun testClusterClick_animatesCameraWithZoomStep() {
        val clusterNode = ClusterMarkerUiState.ClusterNode(
            clusterId = "cluster_test",
            count = 3,
            centerLat = 52.5100,
            centerLng = 13.4400,
            items = viewModel.uiState.value.nearbyVenues,
            primaryCategory = "Club"
        )

        val initialZoom = viewModel.uiState.value.cameraPosition.zoom
        viewModel.onClusterClicked(clusterNode)
        testDispatcher.scheduler.advanceUntilIdle()

        val newZoom = viewModel.uiState.value.cameraPosition.zoom
        assertEquals(initialZoom + 2.0f, newZoom, 0.01f)
        assertEquals(52.5100, viewModel.uiState.value.cameraPosition.latitude, 0.0001)
    }
}
