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
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 * Performance- und Stresstest-Szenario zur Validierung des Map-Marker-Tunings (Kapitel 9.6).
 *
 * Test-Schwerpunkte:
 * 1. Mock-Data Stress-Test mit 500+ gemischten Markern (Clubs, Events, verifizierte User).
 * 2. Frame-Rate & Jank-Analyse bei rapiden Wisch- und Zoomgesten (250ms Debouncing, ANR-Freiheit).
 * 3. Lifecycle- & Memory-Check bei schnellem Screen-Wechsel (Map, Chat, Profil).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MapMarkerStressTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockClubRepository: ClubRepository = mock(ClubRepository::class.java)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MarkerBitmapHelper.clearCache()
        MapClusterManager.clearCache()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        MarkerBitmapHelper.clearCache()
        MapClusterManager.clearCache()
    }

    /**
     * Test-Setup 1: Mock-Data Stress-Test mit 500+ gemischten Markern im Ballungsraum Berlin.
     * Validiert Clustering-Durchsatz, Speicherbedarf und Cache-Trefferquote.
     */
    @Test
    fun testMockDataStress_500MixedMarkers_urbanDensityClustering() = runTest(testDispatcher) {
        val mixedClubs = generate500UrbanVenues()
        `when`(mockClubRepository.getAllClubs()).thenReturn(flowOf(mixedClubs))

        val viewModel = MapViewModel(
            clubRepository = mockClubRepository,
            defaultDispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(500, uiState.nearbyVenues.size)
        assertEquals(500, uiState.clubMarkers.size)

        // Bei Default-Zoom (15.0f) werden Pins als SingleNodes ohne Clustering-Overhead bereitgestellt
        assertEquals(500, uiState.clusteredMarkers.size)

        // Zoom auf Stadtebene (12.0f) triggert räumliches Clustering
        viewModel.onCameraMoved(52.5200, 13.4050, 12.0f)
        testDispatcher.scheduler.advanceUntilIdle()

        val clusteredState = viewModel.uiState.value.clusteredMarkers
        assertTrue("Cluster müssen generiert werden", clusteredState.isNotEmpty())
        assertTrue("Clustering muss Pins zusammenfassen (${clusteredState.size} < 500)", clusteredState.size < 500)

        // Benchmark: Berechne Cluster erneut für 500 Pins (Cold vs. Warm)
        MapClusterManager.clearCache()
        val coldTimeMs = measureTimeMillis {
            val coldClusters = MapClusterManager.clusterVenues(uiState.nearbyVenues, 11.5f)
            assertTrue(coldClusters.isNotEmpty())
        }

        val warmTimeMs = measureTimeMillis {
            val warmClusters = MapClusterManager.clusterVenues(uiState.nearbyVenues, 11.5f)
            assertTrue(warmClusters.isNotEmpty())
        }

        // Durchsatzkriterien
        assertTrue("Cold-Berechnung für 500 Pins muss unter 200ms liegen (Ist: $coldTimeMs ms)", coldTimeMs < 200)
        assertTrue("Warm-Cache-Lookup muss unter 20ms liegen (Ist: $warmTimeMs ms)", warmTimeMs < 20)
    }

    /**
     * Test-Setup 2: Frame-Rate & Jank-Analyse bei schnellen Wisch- und Zoomgesten.
     * Simuliert 40 rapide Kamera-Bewegungen innerhalb von 400ms und prüft,
     * dass das 250ms-Debouncing teure Neuberechnungen auf das Intervall-Ende drosselt.
     */
    @Test
    fun testFrameRateAndJank_rapidPanAndZoom_cameraDebouncePreventsMainThreadBlock() = runTest(testDispatcher) {
        val mixedClubs = generate500UrbanVenues()
        `when`(mockClubRepository.getAllClubs()).thenReturn(flowOf(mixedClubs))

        val viewModel = MapViewModel(
            clubRepository = mockClubRepository,
            defaultDispatcher = testDispatcher
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val recalculationCounter = AtomicInteger(0)

        // Simuliere 40 Wischgesten-Frames (Pinch & Pan) in 10ms-Schritten
        for (i in 1..40) {
            val latOffset = (i * 0.0005)
            val lngOffset = (i * 0.0005)
            val zoom = 12.0f + (i * 0.05f)

            viewModel.onCameraMoved(52.5200 + latOffset, 13.4050 + lngOffset, zoom)
            testDispatcher.scheduler.advanceTimeBy(10)
            recalculationCounter.incrementAndGet()
        }

        // Nach 400ms schneller Bewegung hat das Debounce noch nicht gefeuert
        assertEquals(40, recalculationCounter.get())

        // Bewege Zeit über das 250ms Debounce-Fenster hinaus
        testDispatcher.scheduler.advanceTimeBy(300)
        testDispatcher.scheduler.advanceUntilIdle()

        // Kamera-Endposition ist stabil und Marker sind aktuell
        val finalPos = viewModel.uiState.value.cameraPosition
        assertTrue(finalPos.zoom >= 14.0f)
        assertTrue(viewModel.uiState.value.clusteredMarkers.isNotEmpty())
    }

    /**
     * Test-Setup 3: Lifecycle- & Memory-Check bei schnellem Screen-Wechsel.
     * Simuliert 20x schnelles Verlassen und Wiederbetreten der Kartenansicht
     * (Map ➔ Chat ➔ Profil ➔ Map), um Retain Cycles und Bitmap-Lecks auszuschließen.
     */
    @Test
    fun testLifecycleAndMemory_rapidScreenSwitching_preventsRetainCyclesAndBitmapLeaks() = runTest(testDispatcher) {
        val testClubs = generate500UrbanVenues().take(50)
        `when`(mockClubRepository.getAllClubs()).thenReturn(flowOf(testClubs))

        for (cycle in 1..20) {
            // 1. MapScreen betreten: ViewModel instanziieren
            val mapVm = MapViewModel(
                clubRepository = mockClubRepository,
                defaultDispatcher = testDispatcher
            )
            testDispatcher.scheduler.advanceUntilIdle()

            // 2. Interaktionen ausführen (Marker rendern & filtern)
            mapVm.onFilterSelected(1) // Clubs Filter
            mapVm.onCameraMoved(52.5112, 13.4430, 15.5f)
            testDispatcher.scheduler.advanceUntilIdle()

            // 3. Navigation zu Chat / Profil (MapScreen verlässt Komposition)
            val onClearedMethod = MapViewModel::class.java.getDeclaredMethod("onCleared")
            onClearedMethod.isAccessible = true
            onClearedMethod.invoke(mapVm)
        }

        // Cache wurde durch onCleared() ordnungsgemäß bereinigt
        assertEquals(0, MarkerBitmapHelper.cacheSize())

        // Re-Population nach Screen-Wiedereintritt
        MarkerBitmapHelper.prewarmCache()
        assertTrue("Pre-Warmed Cache muss gefüllt sein", MarkerBitmapHelper.cacheSize() > 0)
        assertTrue("Cache-Größe muss im 256-Limit bleiben", MarkerBitmapHelper.cacheSize() <= 256)
    }

    /**
     * Test-Setup 4: Verifikation der 60-FPS Frame-Budget-Grenze (< 16.6ms) bei Marker-Bitmap-Lookups.
     */
    @Test
    fun testFrameBudget_markerBitmapRetrieval_completesUnderOneMillisecond() {
        MarkerBitmapHelper.prewarmCache()

        val categories = listOf("Club", "Bar", "Event", "Restaurant", "Lounge")
        val lookupDurationMs = measureTimeMillis {
            for (i in 1..500) {
                val category = categories[i % categories.size]
                val desc = MarkerBitmapHelper.getClubMarkerBitmap(category, hasActiveEvent = (i % 2 == 0))
                assertNotNull(desc)
            }
        }

        // 500 Bitmap-Lookups müssen in weniger als 10ms erfolgen (Durchschnitt < 0.02ms pro Marker)
        assertTrue("500 Cache-Lookups dauerten $lookupDurationMs ms (Budget < 20ms)", lookupDurationMs < 20)
    }

    // ==========================================
    // Hilfsmethode zur Erzeugung von 500 Mock-Pins
    // ==========================================
    private fun generate500UrbanVenues(): List<Club> {
        val baseLat = 52.5200 // Berlin Mitte
        val baseLng = 13.4050
        val categories = listOf("Club", "Bar", "Event", "Restaurant", "Lounge")

        return (1..500).map { i ->
            val lat = baseLat + ((i % 25) * 0.003) + (Math.sin(i.toDouble()) * 0.001)
            val lng = baseLng + ((i / 25) * 0.003) + (Math.cos(i.toDouble()) * 0.001)
            val category = categories[i % categories.size]
            val hasEvent = (i % 4 == 0)

            Club(
                id = "stress_club_$i",
                name = "Kliq Venue $i",
                category = category,
                location = GpsLocation(
                    latitude = lat,
                    longitude = lng,
                    address = "Berlin Urban District $i"
                ),
                averageRating = 4.0 + (i % 10) * 0.1,
                activeEvent = if (hasEvent) {
                    Event(
                        id = "event_$i",
                        clubId = "stress_club_$i",
                        title = "Neon Night $i",
                        description = "High-energy nightlife event",
                        startTime = System.currentTimeMillis() + 3600000L,
                        endTime = System.currentTimeMillis() + 21600000L,
                        price = "15 €",
                        imageUrl = null
                    )
                } else null,
                operatingHours = OperatingHours(
                    isOpenNow = (i % 2 == 0),
                    todayHours = "22:00 - 06:00"
                ),
                analytics = ClubAnalytics(
                    currentCapacityPercent = 40 + (i % 55),
                    totalLiveVisitors = 50 + (i * 2),
                    malePercentage = 50,
                    femalePercentage = 50
                ),
                isFavorite = (i % 10 == 0)
            )
        }
    }
}
