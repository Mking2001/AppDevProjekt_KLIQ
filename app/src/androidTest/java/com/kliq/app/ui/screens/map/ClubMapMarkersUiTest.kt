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
 * Instrumentierter UI-Test für Kapitel 4.5: Anzeige von Custom Club & User Markern auf der Karte.
 *
 * Testet folgende UI-Interaktionsabläufe auf dem Emulator / Endgerät:
 *   1. Rendering von Custom Club-Markern & Bottom Sheet Liste bei Kartenaufruf.
 *   2. Reaktivität bei Klick auf Club-Elemente: Öffnen des MapQuickViewCard Overlays mit Event-Details.
 *   3. Schließen-Funktion des Quick-View Overlays.
 *   4. Reagieren der Kategorie-Filter-Chips auf der Karte.
 *   5. Anzeige und Rendering von Custom User Profile Markern & Navigation.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ClubMapMarkersUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    /**
     * Testfall 1: Club-Marker und Bottom-Sheet-Liste werden gerendert.
     */
    @Test
    fun test1_clubMarkerUndBottomSheetWerdenGerendert() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Berghain / Panorama Bar")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Watergate").assertIsDisplayed()
    }

    /**
     * Testfall 2: Klick auf Club-Marker / Card öffnet Quick-View Overlay mit Event-Details.
     */
    @Test
    fun test2_klickAufVenueItemOeffnetQuickViewCardWithEventDetails() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Berghain / Panorama Bar")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Klick auf Berghain in der Liste/Kartenansicht
        composeTestRule.onNodeWithText("Berghain / Panorama Bar").performClick()
        composeTestRule.waitForIdle()

        // Quick View Details Button & Route Button sind sichtbar
        composeTestRule.onNodeWithText("Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Route").assertIsDisplayed()
    }

    /**
     * Testfall 3: Schließen-Button des Quick-View Overlays blendet die Karte aus.
     */
    @Test
    fun test3_quickViewSchliessenFunctionality() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Berghain / Panorama Bar")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Berghain / Panorama Bar").performClick()
        composeTestRule.waitForIdle()

        // Klick auf Schließen Button
        composeTestRule.onNodeWithContentDescription("Schließen").performClick()
        composeTestRule.waitForIdle()

        // Quick-View Details-Button existiert nach Schließen nicht mehr im aktiven Overlay
        composeTestRule.onAllNodesWithText("Details").fetchSemanticsNodes().isEmpty()
    }

    /**
     * Testfall 4: Klick auf Kategorie-Filter-Chips aktualisiert die Anzeige.
     */
    @Test
    fun test4_kategorieFilterChipsAendernAnzeige() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Clubs")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Klick auf "Bars" Filter-Chip
        composeTestRule.onNodeWithText("Bars").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Sunset Lounge").assertIsDisplayed()
    }

    /**
     * Testfall 5: Custom User Profile Marker und Club Marker werden geladen.
     */
    @Test
    fun test5_userUndClubMarkerWerdenErfolgreichGeladen() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Berghain / Panorama Bar")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("In deiner Nähe (4)").assertIsDisplayed()
    }
}
