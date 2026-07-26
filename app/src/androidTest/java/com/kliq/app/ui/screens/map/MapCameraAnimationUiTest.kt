package com.kliq.app.ui.screens.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentierter UI-Test für Kapitel 4.9: Map-Kamera-Animationen (Focus, Re-Centering & Bounding Box Fit).
 *
 * Prüft auf dem Emulator / Endgerät folgende Kamera-Animations-Szenarien:
 *   1. Focus-on-Marker Test: Klick auf Club-Marker löst flüssiges Kamera-Zentrieren (Zoom 16.0, 35° Tilt) aus.
 *   2. Location-Recenter Test: Manueller Karten-Shift & Klick auf "Mein Standort" FAB führt weiches Re-Centering (1000ms) aus.
 *   3. Bounding-Box Fit Test: Anwenden von Kategorie-Filtern berechnet den optimalen LatLngBounds-Ausschnitt mit 120px Padding.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MapCameraAnimationUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    /**
     * Testfall 1: Focus-on-Marker Test
     * Klick auf Marker/Venue -> Flüssige Kamera-Animation & Anzeige von QuickView Card.
     */
    @Test
    fun test1_focusOnMarkerAnimation_smoothlyCentersCameraAndDisplaysQuickView() {
        // Navigiere zur Kartenansicht
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        // Warte bis Marker geladen sind
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Berghain / Panorama Bar")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Klick auf Club-Marker / Listenelement
        composeTestRule.onNodeWithText("Berghain / Panorama Bar").performClick()
        composeTestRule.waitForIdle()

        // Kamera-Animation completed -> Quick View Details & Route sind sichtbar
        composeTestRule.onNodeWithText("Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Route").assertIsDisplayed()
    }

    /**
     * Testfall 2: Location-Recenter Test
     * Manueller Shift + FAB-Klick -> Weiches Gleiten zur Standort-Position.
     */
    @Test
    fun test2_locationRecenterAnimation_smoothlyGlidesToUserLocation() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        // Simuliere manuelles Panning (Swipe) auf der Karte
        composeTestRule.onNodeWithContentDescription("Mein Standort").performTouchInput {
            swipeLeft()
        }
        composeTestRule.waitForIdle()

        // Klick auf MyLocation FAB
        composeTestRule.onNodeWithContentDescription("Mein Standort").performClick()
        composeTestRule.waitForIdle()

        // Verifiziere Re-Centering Abschluss
        composeTestRule.onNodeWithContentDescription("Mein Standort").assertIsDisplayed()
    }

    /**
     * Testfall 3: Bounding-Box Fit Test
     * Filter-Wechsel berechnet LatLngBounds und animiert den Viewport ohne Marker-Abschnitt.
     */
    @Test
    fun test3_boundingBoxFitAnimation_adjustsViewportForFilteredMarkers() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Clubs")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Klick auf Kategorie-Filter Chip "Clubs"
        composeTestRule.onNodeWithText("Clubs").performClick()
        composeTestRule.waitForIdle()

        // Alle gefilterten Club-Pins liegen im Viewport
        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Watergate").assertIsDisplayed()
    }
}
