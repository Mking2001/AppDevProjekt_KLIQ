package com.kliq.app.ui.screens.map

import com.kliq.app.data.model.CameraEasing
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.LatLngBoundsData
import com.kliq.app.data.model.MapCameraAnimationEvent
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.seed.KlagenfurtSeedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class MapCameraAnimationTest {

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
            category = "Club"
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
    fun testOnLocationRequested_emitsAnimateToLocationEventWithDurationAndEasing() {
        val events = mutableListOf<MapCameraAnimationEvent>()
        val job = kotlinx.coroutines.CoroutineScope(testDispatcher).launch {
            viewModel.cameraEventFlow.collect { events.add(it) }
        }

        viewModel.onLocationRequested()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(events.isNotEmpty())
        val lastEvent = events.last() as MapCameraAnimationEvent.AnimateToLocation
        assertEquals(KlagenfurtSeedData.CITY_LATITUDE, lastEvent.latitude, 0.0001)
        assertEquals(KlagenfurtSeedData.CITY_LONGITUDE, lastEvent.longitude, 0.0001)
        assertEquals(15.0f, lastEvent.zoom)
        assertEquals(0.0f, lastEvent.tilt)
        assertEquals(1000, lastEvent.durationMs)
        assertEquals(CameraEasing.EASE_IN_OUT, lastEvent.easing)

        job.cancel()
    }

    @Test
    fun testOnMarkerClicked_emitsAnimateToLocationWithNightTiltAndBearing() {
        val events = mutableListOf<MapCameraAnimationEvent>()
        val job = kotlinx.coroutines.CoroutineScope(testDispatcher).launch {
            viewModel.cameraEventFlow.collect { events.add(it) }
        }

        val venue = viewModel.uiState.value.nearbyVenues.first()
        viewModel.onMarkerClicked(venue)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(events.isNotEmpty())
        val event = events.last() as MapCameraAnimationEvent.AnimateToLocation
        assertEquals(venue.latitude, event.latitude, 0.0001)
        assertEquals(venue.longitude, event.longitude, 0.0001)
        assertEquals(16.0f, event.zoom)
        assertEquals(35.0f, event.tilt)
        assertEquals(15.0f, event.bearing)
        assertEquals(1000, event.durationMs)

        job.cancel()
    }

    @Test
    fun testOnUserMarkerClicked_emitsAnimateToLocationWithNightTiltAndBearing() {
        val events = mutableListOf<MapCameraAnimationEvent>()
        val job = kotlinx.coroutines.CoroutineScope(testDispatcher).launch {
            viewModel.cameraEventFlow.collect { events.add(it) }
        }

        val userMarker = viewModel.uiState.value.userMarkers.first()
        viewModel.onUserMarkerClicked(userMarker)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(events.isNotEmpty())
        val event = events.last() as MapCameraAnimationEvent.AnimateToLocation
        assertEquals(userMarker.latitude, event.latitude, 0.0001)
        assertEquals(userMarker.longitude, event.longitude, 0.0001)
        assertEquals(16.0f, event.zoom)
        assertEquals(35.0f, event.tilt)
        assertEquals(15.0f, event.bearing)
        assertEquals(1000, event.durationMs)

        job.cancel()
    }

    @Test
    fun testOnClusterClicked_emitsAnimateToLocationWithIncreasedZoom() {
        val events = mutableListOf<MapCameraAnimationEvent>()
        val job = kotlinx.coroutines.CoroutineScope(testDispatcher).launch {
            viewModel.cameraEventFlow.collect { events.add(it) }
        }

        val clusterNode = ClusterMarkerUiState.ClusterNode(
            clusterId = "cluster_1",
            count = 5,
            centerLat = 52.5100,
            centerLng = 13.4400,
            items = emptyList(),
            primaryCategory = "Club"
        )

        viewModel.onClusterClicked(clusterNode)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(events.isNotEmpty())
        val event = events.last() as MapCameraAnimationEvent.AnimateToLocation
        assertEquals(52.5100, event.latitude, 0.0001)
        assertEquals(13.4400, event.longitude, 0.0001)
        assertEquals(20.0f, event.tilt)
        assertEquals(800, event.durationMs)

        job.cancel()
    }

    @Test
    fun testLatLngBoundsData_fromCoordinates_calculatesCorrectBoundingBox() {
        val coords = listOf(
            Pair(52.5000, 13.4000),
            Pair(52.5200, 13.4500),
            Pair(52.5100, 13.4200)
        )

        val bounds = LatLngBoundsData.fromCoordinates(coords)
        assertNotNull(bounds)
        assertEquals(52.5000, bounds!!.southWestLat, 0.0001)
        assertEquals(13.4000, bounds.southWestLng, 0.0001)
        assertEquals(52.5200, bounds.northEastLat, 0.0001)
        assertEquals(13.4500, bounds.northEastLng, 0.0001)
        assertEquals(52.5100, bounds.centerLat, 0.0001)
        assertEquals(13.4250, bounds.centerLng, 0.0001)
    }

    @Test
    fun testOnFilterSelected_emitsAnimateToBoundsEventForVisibleMarkers() {
        val events = mutableListOf<MapCameraAnimationEvent>()
        val job = kotlinx.coroutines.CoroutineScope(testDispatcher).launch {
            viewModel.cameraEventFlow.collect { events.add(it) }
        }

        viewModel.onFilterSelected(1)
        testDispatcher.scheduler.advanceUntilIdle()

        val boundsEvent = events.filterIsInstance<MapCameraAnimationEvent.AnimateToBounds>().lastOrNull()
        assertNotNull(boundsEvent)
        assertEquals(120, boundsEvent!!.paddingPx)
        assertEquals(1000, boundsEvent.durationMs)

        job.cancel()
    }

    @Test
    fun testAnimateNightPerspective_emitsAnimateTiltRotationEvent() {
        val events = mutableListOf<MapCameraAnimationEvent>()
        val job = kotlinx.coroutines.CoroutineScope(testDispatcher).launch {
            viewModel.cameraEventFlow.collect { events.add(it) }
        }

        viewModel.animateNightPerspective(tilt = 45.0f, bearing = 30.0f)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(events.isNotEmpty())
        val event = events.last() as MapCameraAnimationEvent.AnimateTiltRotation
        assertEquals(45.0f, event.tilt)
        assertEquals(30.0f, event.bearing)
        assertEquals(800, event.durationMs)

        job.cancel()
    }
}
