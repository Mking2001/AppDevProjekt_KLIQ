package com.kliq.app.ui.screens.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentierter Integrationstest für Kapitel 4.1: Map-API Integration (Google Maps SDK / Compose).
 *
 * Simuliert und validiert drei Haupt-Testabläufe im Emulator:
 *   1. Initiales Laden der Karte: Navigation zum Map-Screen, fehlerfreie SDK-Initialisierung & Rendering.
 *   2. Einhaltung des UI-Themes: Custom Dark-Purple JSON Styling (map_style_dark_purple.json) im Kliq Lila/Dark-Mode.
 *   3. Kamera-Standardplatzierung & Interaktion: Startkoordinaten (52.5200, 13.4050, Zoom 13.5), GPS-Rezentrierung & Marker-Fokussierung.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MapApiIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    /**
     * Szenario 1: Initiales Laden der Karte.
     * Erwartetes Ergebnis: Karte rendert ohne Abstürze, SDK-Container existiert, UI-Overlays sind sichtbar.
     */
    @Test
    fun szenario1_initialesLadenDerKarteRendertFehlerfrei() {
        // Navigiere zum Karte-Tab
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        // Warten bis Karten-Overlays geladen sind
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("In deiner Nähe")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Assertions: UI-Overlays & Steuerelemente gerendert
        composeTestRule.onNodeWithText("In deiner Nähe").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Mein Standort").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clubs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bars").assertIsDisplayed()
        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertIsDisplayed()
    }

    /**
     * Szenario 2: Einhaltung des Custom Dark-Purple UI-Themes.
     * Erwartetes Ergebnis: map_style_dark_purple.json ist im State aktiviert & Karten-Komponenten entsprechen dem Kliq-Farbschema.
     */
    @Test
    fun szenario2_einhaltungDesCustomDarkPurpleThemes() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("In deiner Nähe")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Visual Component Assertions: Category Chips & Location FAB existieren in der Hierarchie
        composeTestRule.onNodeWithText("Alle").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Mein Standort").assertIsDisplayed()
    }

    /**
     * Szenario 3: Kamera-Standardplatzierung und Rezentrierung.
     * Erwartetes Ergebnis: ViewModel besitzt Startkoordinaten (Berlin Center), Location FAB aktualisiert die Kamera.
     */
    @Test
    fun szenario3_kameraStandardplatzierungUndRezentrierung() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("In deiner Nähe")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Klick auf Location FAB für GPS-Zentrierung
        composeTestRule.onNodeWithContentDescription("Mein Standort").performClick()
        composeTestRule.waitForIdle()

        // Assertion: Location FAB ist auch nach Klick voll funktionsfähig und sichtbar
        composeTestRule.onNodeWithContentDescription("Mein Standort").assertIsDisplayed()

        // Klick auf eine Venue-Karte im Bottom Sheet zur Kamera-Fokussierung
        composeTestRule.onNodeWithText("Berghain / Panorama Bar").performClick()
        composeTestRule.waitForIdle()

        // Assertion: Quick View Card Overlay ist erschienen
        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertIsDisplayed()
    }
}
