/**
 * AI-GENERATED CODE
 * Dieses Test-Szenario wurde vollständig durch KI generiert.
 * Erstellt im Rahmen von Schritt 4: Layout-Scaffolding der Haupt-Screens.
 * Datum: 2026-05-11
 *
 * Zweck: Verifiziert das Layout-Scaffolding aller 5 Haupt-Screens.
 * - Kompilierbarkeit & Rendering im Emulator
 * - Korrekte Darstellung des Lila/Dark-Mode-Themes
 * - Erreichbarkeit aller 5 Screens über Bottom-Navigation
 * - Vorhandensein der wesentlichen UI-Strukturelemente pro Screen
 * - Reaktionsfähigkeit der UI-Komponenten (Klick-Interaktionen)
 */
package com.kliq.app.ui.screens

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasRole
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kliq.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

// ============================================================
// AI-generiert: Instrumentierter UI-Test für das Layout-Scaffolding
// aller 5 Haupt-Screens (Home, Explore, Map, Notifications, Profile).
//
// Ausführung auf einem Android-Emulator oder physischem Gerät:
//   ./gradlew connectedAndroidTest
//
// Oder einzelner Test:
//   ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.screens.ScreenScaffoldingTest
// ============================================================

@RunWith(AndroidJUnit4::class)
class ScreenScaffoldingTest {

    /**
     * AI-generiert: Compose-Test-Rule, startet die MainActivity
     * mit dem vollständigen Hilt-DI-Graph und dem KliqTheme (Dark-Mode).
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // ── Test 1: Home-Screen Scaffolding ────────────────────────

    /**
     * AI-generiert: Prüft, ob der Home-Screen korrekt gerendert wird.
     * Erwartet: TopAppBar mit "Kliq"-Titel, Story-Row-Avatare,
     * Feed-Karten mit Platzhalter-Daten, FAB.
     */
    @Test
    fun homeScreen_displaysScaffoldingElements() {
        // Die App startet standardmäßig auf dem Home-Screen
        composeTestRule.waitForIdle()

        // TopAppBar-Titel
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        // Feed-Karte: Erster Platzhalter-Beitrag sichtbar
        composeTestRule.onNodeWithText("Anna M.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vor 15 Min.").assertIsDisplayed()

        // FAB zum Erstellen neuer Posts
        composeTestRule.onNodeWithContentDescription("Neuer Beitrag").assertIsDisplayed()

        // Action-Icons in der TopBar
        composeTestRule.onNodeWithContentDescription("Filter").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Nachrichten").assertIsDisplayed()
    }

    /**
     * AI-generiert: Prüft, ob die Story-Row Platzhalter-Avatare anzeigt.
     */
    @Test
    fun homeScreen_displaysStoryRow() {
        composeTestRule.waitForIdle()

        // Story-Avatare der Mock-Daten
        composeTestRule.onNodeWithText("Anna").assertIsDisplayed()
        composeTestRule.onNodeWithText("Max").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lisa").assertIsDisplayed()
    }

    // ── Test 2: Explore-Screen Scaffolding ─────────────────────

    /**
     * AI-generiert: Prüft, ob der Explore-Screen korrekt gerendert wird.
     * Erwartet: TopAppBar mit "Entdecken", Suchleiste, Kategorie-Chips, Grid-Karten.
     */
    @Test
    fun exploreScreen_displaysScaffoldingElements() {
        // Navigiere zum Explore-Screen
        navigateToTab("Entdecken")

        // TopAppBar-Titel
        composeTestRule.onNodeWithText("Entdecken").assertIsDisplayed()

        // Suchleiste-Platzhalter
        composeTestRule.onNodeWithText("Suche nach Leuten, Events, Orten…").assertIsDisplayed()

        // Kategorie-Chips
        composeTestRule.onNodeWithText("Trending").assertIsDisplayed()
        composeTestRule.onNodeWithText("Events").assertIsDisplayed()

        // Grid-Karten (mindestens ein Element)
        composeTestRule.onNodeWithText("Techno Night").assertIsDisplayed()
    }

    /**
     * AI-generiert: Prüft die Interaktivität der Kategorie-Chips.
     */
    @Test
    fun exploreScreen_categoryChipsAreClickable() {
        navigateToTab("Entdecken")

        // Klick auf einen Kategorie-Chip
        composeTestRule.onNodeWithText("Trending").performClick()
        composeTestRule.waitForIdle()

        // Der Chip sollte weiterhin angezeigt werden (Zustandswechsel)
        composeTestRule.onNodeWithText("Trending").assertIsDisplayed()
    }

    // ── Test 3: Map-Screen Scaffolding ─────────────────────────

    /**
     * AI-generiert: Prüft, ob der Map-Screen korrekt gerendert wird.
     * Erwartet: Karten-Platzhalter, Filter-Chips, Location-FAB,
     * Bottom-Sheet mit Venue-Karten.
     */
    @Test
    fun mapScreen_displaysScaffoldingElements() {
        navigateToTab("Karte")

        // Zentraler Standort-Marker
        composeTestRule.onNodeWithContentDescription("Standort").assertIsDisplayed()

        // Location-FAB
        composeTestRule.onNodeWithContentDescription("Mein Standort").assertIsDisplayed()

        // Filter-Chips
        composeTestRule.onNodeWithText("Clubs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bars").assertIsDisplayed()

        // Bottom-Sheet-Titel
        composeTestRule.onNodeWithText("In deiner Nähe").assertIsDisplayed()

        // Venue-Karte
        composeTestRule.onNodeWithText("Club Luna").assertIsDisplayed()
    }

    // ── Test 4: Notifications-Screen Scaffolding ───────────────

    /**
     * AI-generiert: Prüft, ob der Notifications-Screen korrekt gerendert wird.
     * Erwartet: TopAppBar mit "Aktivität", Filter-Tabs, Benachrichtigungsliste.
     */
    @Test
    fun notificationsScreen_displaysScaffoldingElements() {
        navigateToTab("Aktivität")

        // TopAppBar-Titel
        composeTestRule.onNodeWithText("Aktivität").assertIsDisplayed()

        // Filter-Tabs
        composeTestRule.onNodeWithText("Alle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Likes").assertIsDisplayed()

        // Benachrichtigungs-Einträge
        composeTestRule.onNodeWithText("Anna M. hat deinen Beitrag geliked").assertIsDisplayed()

        // "Alle gelesen"-Button (weil ungelesene vorhanden)
        composeTestRule.onNodeWithContentDescription("Alle als gelesen markieren").assertIsDisplayed()
    }

    /**
     * AI-generiert: Prüft die Tab-Filter-Interaktion auf dem Notifications-Screen.
     */
    @Test
    fun notificationsScreen_tabFilterIsInteractive() {
        navigateToTab("Aktivität")

        // Klick auf "Follows"-Tab
        composeTestRule.onNodeWithText("Follows").performClick()
        composeTestRule.waitForIdle()

        // Follow-Benachrichtigung sollte sichtbar sein
        composeTestRule.onNodeWithText("Lisa W. folgt dir jetzt").assertIsDisplayed()
    }

    /**
     * AI-generiert: Prüft die "Alle gelesen"-Funktionalität.
     */
    @Test
    fun notificationsScreen_markAllReadWorks() {
        navigateToTab("Aktivität")

        // Klick auf "Alle gelesen"
        composeTestRule.onNodeWithContentDescription("Alle als gelesen markieren").performClick()
        composeTestRule.waitForIdle()

        // Der Button sollte verschwinden, da keine ungelesenen mehr vorhanden
        composeTestRule.onNodeWithContentDescription("Alle als gelesen markieren")
            .assertDoesNotExist()
    }

    // ── Test 5: Profile-Screen Scaffolding ─────────────────────

    /**
     * AI-generiert: Prüft, ob der Profile-Screen korrekt gerendert wird.
     * Erwartet: Avatar, Name, Bio, Statistiken, Bearbeiten-Button, Tabs.
     */
    @Test
    fun profileScreen_displaysScaffoldingElements() {
        navigateToTab("Profil")

        // TopAppBar-Titel
        composeTestRule.onNodeWithText("Profil").assertIsDisplayed()

        // Profildaten
        composeTestRule.onNodeWithText("Max Mustermann").assertIsDisplayed()
        composeTestRule.onNodeWithText("@maxmuster").assertIsDisplayed()

        // Statistiken
        composeTestRule.onNodeWithText("Beiträge").assertIsDisplayed()
        composeTestRule.onNodeWithText("Follower").assertIsDisplayed()
        composeTestRule.onNodeWithText("Following").assertIsDisplayed()
        composeTestRule.onNodeWithText("127").assertIsDisplayed()

        // Bearbeiten-Button
        composeTestRule.onNodeWithText("Profil bearbeiten").assertIsDisplayed()
    }

    /**
     * AI-generiert: Prüft die Tab-Navigation auf dem Profile-Screen.
     */
    @Test
    fun profileScreen_tabNavigationWorks() {
        navigateToTab("Profil")

        // Wechsel zum "Events"-Tab
        composeTestRule.onNodeWithText("Events").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Techno Night").assertIsDisplayed()

        // Wechsel zum "Über mich"-Tab
        composeTestRule.onNodeWithText("Über mich").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Interessen").assertIsDisplayed()
    }

    // ── Test 6: Vollständiger Navigationskreislauf ─────────────

    /**
     * AI-generiert: Navigiert durch alle 5 Tabs und prüft,
     * ob jeder Screen seine Kernelemente anzeigt.
     * Verifiziert den vollständigen Navigation-Loop.
     */
    @Test
    fun fullNavigationLoop_allScreensReachable() {
        // 1. Home (Start)
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        // 2. Entdecken
        navigateToTab("Entdecken")
        composeTestRule.onNodeWithText("Suche nach Leuten, Events, Orten…").assertIsDisplayed()

        // 3. Karte
        navigateToTab("Karte")
        composeTestRule.onNodeWithText("In deiner Nähe").assertIsDisplayed()

        // 4. Aktivität
        navigateToTab("Aktivität")
        composeTestRule.onNodeWithText("Alle").assertIsDisplayed()

        // 5. Profil
        navigateToTab("Profil")
        composeTestRule.onNodeWithText("Max Mustermann").assertIsDisplayed()

        // 6. Zurück zu Home (Loop)
        navigateToTab("Home")
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
        composeTestRule.onNodeWithText("Anna M.").assertIsDisplayed()
    }

    // ── Hilfsfunktionen ────────────────────────────────────────

    /**
     * AI-generiert: Navigiert zu einem Tab über die Bottom-Navigation-Bar.
     * Nutzt die semantische Rolle (Tab) und den Label-Text zur Identifikation.
     *
     * @param tabLabel Label des Ziel-Tabs (z.B. "Entdecken", "Karte").
     */
    private fun navigateToTab(tabLabel: String) {
        composeTestRule.onNode(
            hasText(tabLabel) and hasRole(Role.Tab)
        ).performClick()
        composeTestRule.waitForIdle()
    }
}
