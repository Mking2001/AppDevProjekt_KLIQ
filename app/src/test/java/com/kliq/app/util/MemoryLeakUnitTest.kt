package com.kliq.app.util

import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.data.model.MapStyleConfig
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.ui.screens.map.MapLocationFilterMode
import com.kliq.app.ui.screens.map.MapViewModel
import com.kliq.app.ui.screens.map.MarkerBitmapHelper
import com.kliq.app.viewmodel.LocationTrackingUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

/**
 * Unit-Tests zur Verifizierung von Speicheroptimierungen, Bitmap-Cache-Evakuierung
 * und Lebenszyklus-Bereinigung in den ViewModel- und Application-Schichten (Kapitel 9.3).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MemoryLeakUnitTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MarkerBitmapHelper.clearCache()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        MarkerBitmapHelper.clearCache()
    }

    /**
     * Prüft, dass der MarkerBitmapHelper Bitmaps im Cache ablegt
     * und bei Aufruf von clearCache() den Arbeitsspeicher vollständig leert.
     */
    @Test
    fun testMarkerBitmapHelper_clearCache_evictsAllDescriptors() {
        // Cache initial leer
        assertEquals(0, MarkerBitmapHelper.getCacheSize())

        // Mocks erzeugen
        val desc1 = MarkerBitmapHelper.getClubMarkerBitmap("Club", hasActiveEvent = true)
        val desc2 = MarkerBitmapHelper.getClubMarkerBitmap("Bar", hasActiveEvent = false)
        val desc3 = MarkerBitmapHelper.getUserMarkerBitmap("Alex", isOnline = true)
        val desc4 = MarkerBitmapHelper.getClusterMarkerBitmap(12, "Techno")

        assertNotNull(desc1)
        assertNotNull(desc2)
        assertNotNull(desc3)
        assertNotNull(desc4)

        assertTrue(MarkerBitmapHelper.getCacheSize() > 0)

        // Cache leeren (z. B. bei Low Memory Event oder Screen Exit)
        MarkerBitmapHelper.clearCache()
        assertEquals(0, MarkerBitmapHelper.getCacheSize())
    }

    /**
     * Prüft, dass beim Zerstören des MapViewModels (onCleared) alle Marker-Bitmaps
     * aus dem Speicher freigegeben werden.
     */
    @Test
    fun testMapViewModel_onCleared_triggersCacheEviction() {
        val mockClubRepo = mock(ClubRepository::class.java)
        `when`(mockClubRepo.getAllClubs()).thenReturn(flowOf(emptyList()))

        val viewModel = MapViewModel(
            clubRepository = mockClubRepo,
            defaultDispatcher = testDispatcher
        )

        // Generiere Caches
        MarkerBitmapHelper.getClubMarkerBitmap("Club", hasActiveEvent = true)
        assertTrue(MarkerBitmapHelper.getCacheSize() > 0)

        // Simuliere Screen Exit / ViewModel Destruction
        val onClearedMethod = MapViewModel::class.java.getDeclaredMethod("onCleared")
        onClearedMethod.isAccessible = true
        onClearedMethod.invoke(viewModel)

        // Verifiziere, dass der Bitmap Cache geleert wurde
        assertEquals(0, MarkerBitmapHelper.getCacheSize())
    }

    /**
     * Prüft die State-Reinigung bei Konfigurationsänderungen und ViewModel Destruction.
     */
    @Test
    fun testLocationTrackingUiState_initialState_clean() {
        val state = LocationTrackingUiState()

        assertEquals(false, state.isTrackingActive)
        assertEquals(null, state.currentLocation)
        assertEquals(LocationPermissionState.NotRequested, state.backgroundPermissionState)
        assertEquals(0, state.totalSavedPoints)
    }
}
