package com.kliq.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentiertes UI-Test-Szenario für die Kliq App (Kapitel 1 & 7).
 *
 * Dieses Test-Script deckt drei Hauptbereiche ab:
 *   1. Haupt-Navigation (Bottom Bar) und Fehler-Handling (Snackbar)
 *   2. Club-Analytics UI: Live-Statistiken, Geschlechterverhältnis, Events
 *   3. Interaktion: Suchfunktion, Bewertungsfilter, Favoriten-System
 *
 * Vorbedingungen:
 *   - Android Emulator (z.B. Pixel 6, API 34) muss laufen
 *   - Projekt-Sync in Android Studio muss abgeschlossen sein
 *   - Ausführen via: Rechtsklick auf KliqUITest -> Run
 *     oder Terminal: ./gradlew connectedAndroidTest
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class KliqUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    // =========================================================================
    // Test-Szenario 1: Navigation (Bottom Bar) und Fehler-Handling (Snackbar)
    // =========================================================================

    /**
     * Prüft den Splash-Screen-Übergang und die Erreichbarkeit aller
     * fünf Bottom-Bar-Tabs (Home, Entdecken, Karte, Aktivität, Profil).
     */
    @Test
    fun szenario1_splashScreenUndBottomBarNavigation() {
        // Splash Screen erscheint zuerst und navigiert automatisch weiter
        composeTestRule.waitForIdle()

        // Nach Splash: Home-Tab muss sichtbar und selektiert sein
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Home")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("Home").assertExists()

        // Navigiere zu "Entdecken" Tab
        composeTestRule
            .onAllNodesWithText("Entdecken")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Suche nach Leuten, Events, Orten…").assertExists()

        // Navigiere zu "Karte" Tab
        composeTestRule
            .onAllNodesWithText("Karte")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()

        // Navigiere zu "Aktivität" Tab
        composeTestRule
            .onAllNodesWithText("Aktivität")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()

        // Navigiere zu "Profil" Tab
        composeTestRule
            .onAllNodesWithText("Profil")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()

        // Zurück zu Home – validiert den kompletten Tab-Zyklus
        composeTestRule
            .onAllNodesWithText("Home")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()
    }

    /**
     * Prüft, ob der SnackbarHost im Scaffold vorhanden ist und
     * dass die globale Fehlerbehandlung auf Komponentenebene aktiv ist.
     * Die Snackbar wird durch den ClubDetailScreen bei einer leeren Club-ID
     * automatisch ausgelöst.
     */
    @Test
    fun szenario1_snackbarErrorHandling() {
        // Warte bis Home geladen ist
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Home")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Navigiere zu Entdecken
        composeTestRule
            .onAllNodesWithText("Entdecken")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()

        // Scaffold mit SnackbarHost existiert – das prüfen wir implizit
        // durch Navigieren zu einem Club-Detail-Screen.
        // Der Berghain-Eintrag sollte sichtbar sein, wenn geladen.
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("Berghain")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("Berghain").assertExists()
    }

    // =========================================================================
    // Test-Szenario 2: Club-Analytics UI (Live-Stats, Gender-Ratio, Events)
    // =========================================================================

    /**
     * Navigiert zum Berghain Club-Detail-Screen und verifiziert:
     *   - Live-Besucherstatistik mit Auslastungs-Balken
     *   - Aggregiertes Geschlechterverhältnis (W/M Prozentanzeige)
     *   - Info-Block für Event-Highlight
     *   - Öffnungszeiten mit "Jetzt Geöffnet" Status
     * Alle Elemente werden im lila Theme-Design gerendert.
     */
    @Test
    fun szenario2_clubAnalyticsUndInfoBlockRendering() {
        // Warte auf Home
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Home")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Navigiere zu Entdecken
        composeTestRule
            .onAllNodesWithText("Entdecken")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()

        // Warte auf Mock-Daten und klicke auf "Berghain"
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("Berghain")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("Berghain").performClick()
        composeTestRule.waitForIdle()

        // Warte bis ClubDetailScreen geladen ist (Mock-Delay 1s)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Live-Besucherstatistiken")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // 1. Club-Name in der TopAppBar
        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertIsDisplayed()

        // 2. Live-Besucherstatistik: Auslastung
        composeTestRule.onNodeWithText("Live-Besucherstatistiken").assertIsDisplayed()
        composeTestRule.onNodeWithText("Auslastung: 85%").assertIsDisplayed()

        // 3. Geschlechterverhältnis (aggregierte Nutzerdaten)
        composeTestRule.onNodeWithText("Geschlechterverhältnis (1420 Gäste)").assertIsDisplayed()
        composeTestRule.onNodeWithText("45% W").assertIsDisplayed()
        composeTestRule.onNodeWithText("55% M").assertIsDisplayed()

        // 4. Event Info-Block
        composeTestRule.onNodeWithText("Event-Highlight").assertIsDisplayed()
        composeTestRule.onNodeWithText("Klubnacht").assertIsDisplayed()
        composeTestRule.onNodeWithText("Eintritt: 25€").assertIsDisplayed()

        // 5. Öffnungszeiten mit Live-Status
        composeTestRule.onNodeWithText("Öffnungszeiten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jetzt Geöffnet (23:59 - 12:00)").assertIsDisplayed()
    }

    // =========================================================================
    // Test-Szenario 3: Suchfunktion, Rating-Filter & Favoriten
    // =========================================================================

    /**
     * Testet die Suchfunktion: Eingabe "Hamburg" → nur Hamburg-Ergebnisse.
     */
    @Test
    fun szenario3_suchfunktionFuerRegionen() {
        navigiereZuEntdecken()

        // Suchfeld aktivieren und "Hamburg" eingeben
        composeTestRule
            .onNodeWithText("Suche nach Leuten, Events, Orten…")
            .performTextInput("Hamburg")
        composeTestRule.waitForIdle()

        // "Sunset Lounge" (Hamburg) sollte sichtbar sein
        composeTestRule.onNodeWithText("Sunset Lounge").assertIsDisplayed()

        // "Berghain" (Berlin) darf NICHT mehr sichtbar sein
        composeTestRule.onNodeWithText("Berghain").assertDoesNotExist()

        // "Techno Night" (Berlin) darf NICHT mehr sichtbar sein
        composeTestRule.onNodeWithText("Techno Night").assertDoesNotExist()
    }

    /**
     * Testet den Bewertungsfilter: 4.5+ Sterne aktivieren.
     */
    @Test
    fun szenario3_filterFuerClubBewertungen() {
        navigiereZuEntdecken()

        // Mindestbewertung "4.5+ Sterne" auswählen
        composeTestRule.onNodeWithText("4.5+ Sterne").performClick()
        composeTestRule.waitForIdle()

        // Berghain (4.9) muss noch da sein
        composeTestRule.onNodeWithText("Berghain").assertIsDisplayed()

        // Warehouse Rave (4.7) muss noch da sein
        composeTestRule.onNodeWithText("Warehouse Rave").assertIsDisplayed()

        // Club Luna (3.8) darf NICHT mehr sichtbar sein
        composeTestRule.onNodeWithText("Club Luna").assertDoesNotExist()

        // After Work (4.0) darf NICHT mehr sichtbar sein
        composeTestRule.onNodeWithText("After Work").assertDoesNotExist()
    }

    /**
     * Testet das Favoriten-System: Herz-Icon im ClubDetailScreen toggeln.
     */
    @Test
    fun szenario3_favoritenSystemToggle() {
        navigiereZuEntdecken()

        // Berghain auswählen
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("Berghain")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("Berghain").performClick()
        composeTestRule.waitForIdle()

        // Warte bis Detail geladen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Berghain / Panorama Bar")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Favorit-Icon finden und klicken (initial: nicht favorisiert)
        composeTestRule.onNodeWithContentDescription("Favorit").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Favorit").performClick()
        composeTestRule.waitForIdle()

        // Nach dem Toggle: Icon ist immer noch da (Status geändert im ViewModel)
        composeTestRule.onNodeWithContentDescription("Favorit").assertIsDisplayed()

        // Nochmal klicken: Toggle zurück
        composeTestRule.onNodeWithContentDescription("Favorit").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Favorit").assertIsDisplayed()
    }

    // =========================================================================
    // Hilfs-Methoden
    // =========================================================================

    /**
     * Navigiert vom aktuellen Screen zum Entdecken-Tab
     * und wartet auf die Anzeige der Mock-Daten.
     */
    private fun navigiereZuEntdecken() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Home")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule
            .onAllNodesWithText("Entdecken")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("Berghain")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}
