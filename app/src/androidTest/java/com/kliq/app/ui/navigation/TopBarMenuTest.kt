package com.kliq.app.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
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

/**
 * Instrumentierter UI-Test für die Top-App-Bar und die globale Menü-Logik.
 *
 * Testet die vollständige Funktionalität der KliqTopBar-Komponente:
 * - Sichtbarkeit des Overflow-Menü-Buttons auf allen Screens
 * - Korrekte Anzeige aller 5 Menü-Einträge bei Klick
 * - Menü-Schließung nach Auswahl eines Eintrags
 * - Dynamischer Titelwechsel bei Tab-Navigation
 * - Screen-spezifische Action-Icons (z.B. Filter/Chat auf Home)
 *
 * Ausführung:
 *   ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.kliq.app.ui.navigation.TopBarMenuTest
 */
@RunWith(AndroidJUnit4::class)
class TopBarMenuTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // ── Test 1: Overflow-Button ist sichtbar ──────────────────

    /**
     * Prüft, ob der Overflow-Menü-Button (⋮) auf dem Home-Screen
     * beim App-Start sichtbar und anklickbar ist.
     */
    @Test
    fun topBar_overflowButtonIsDisplayedOnStartup() {
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .assertIsDisplayed()
    }

    // ── Test 2: Menü öffnet sich mit allen Einträgen ──────────

    /**
     * Prüft, ob ein Klick auf den Overflow-Button alle 5 globalen
     * Menü-Einträge korrekt anzeigt:
     * Einstellungen, Profil bearbeiten, Darstellung, Über Kliq, Abmelden.
     */
    @Test
    fun topBar_menuShowsAllItemsOnClick() {
        composeTestRule.waitForIdle()

        // Menü öffnen
        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .performClick()
        composeTestRule.waitForIdle()

        // Alle 5 Menü-Einträge müssen sichtbar sein
        composeTestRule.onNodeWithText("Einstellungen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profil bearbeiten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Darstellung").assertIsDisplayed()
        composeTestRule.onNodeWithText("Über Kliq").assertIsDisplayed()
        composeTestRule.onNodeWithText("Abmelden").assertIsDisplayed()
    }

    // ── Test 3: Menü schließt nach Item-Auswahl ───────────────

    /**
     * Prüft, ob das Menü sich nach Auswahl eines Eintrags
     * automatisch schließt. "Einstellungen" wird angeklickt
     * und danach darf kein Menü-Eintrag mehr sichtbar sein.
     */
    @Test
    fun topBar_menuClosesAfterItemSelection() {
        composeTestRule.waitForIdle()

        // Menü öffnen
        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .performClick()
        composeTestRule.waitForIdle()

        // "Einstellungen" auswählen
        composeTestRule.onNodeWithText("Einstellungen").performClick()
        composeTestRule.waitForIdle()

        // Menü muss geschlossen sein → Menü-Einträge nicht mehr sichtbar
        composeTestRule.onNodeWithText("Über Kliq").assertDoesNotExist()
        composeTestRule.onNodeWithText("Abmelden").assertDoesNotExist()
    }

    // ── Test 4: Menü-Button auf allen Screens erreichbar ──────

    /**
     * Navigiert durch alle 5 Tabs und prüft, ob der Overflow-Button
     * auf jedem Screen vorhanden ist (globale Top-Bar).
     */
    @Test
    fun topBar_overflowButtonVisibleOnAllScreens() {
        val tabs = listOf("Home", "Entdecken", "Karte", "Aktivität", "Profil")

        tabs.forEach { tabLabel ->
            navigateToTab(tabLabel)

            composeTestRule
                .onNodeWithContentDescription("Menü öffnen")
                .assertIsDisplayed()
        }
    }

    // ── Test 5: Titel ändert sich bei Navigation ──────────────

    /**
     * Prüft den dynamischen Titelwechsel in der Top-Bar
     * bei Navigation zwischen den 5 Haupttabs.
     *
     * Erwartete Titel:
     *   Home → "Kliq"
     *   Entdecken → "Entdecken"
     *   Aktivität → "Aktivität"
     *   Profil → "Profil"
     */
    @Test
    fun topBar_titleUpdatesOnNavigation() {
        composeTestRule.waitForIdle()

        // Start: Home → Titel "Kliq"
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        // Tab: Entdecken → Titel "Entdecken"
        navigateToTab("Entdecken")
        composeTestRule.onNodeWithText("Entdecken").assertIsDisplayed()

        // Tab: Aktivität → Titel "Aktivität"
        navigateToTab("Aktivität")
        composeTestRule.onNodeWithText("Aktivität").assertIsDisplayed()

        // Tab: Profil → Titel "Profil"
        navigateToTab("Profil")
        composeTestRule.onNodeWithText("Profil").assertIsDisplayed()

        // Zurück zu Home → Titel "Kliq"
        navigateToTab("Home")
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
    }

    // ── Test 6: Screen-spezifische Action-Icons (Home) ────────

    /**
     * Prüft, ob der Home-Screen die screen-spezifischen
     * Action-Icons (Filter und Nachrichten) neben dem Overflow-Button anzeigt.
     */
    @Test
    fun homeScreen_displaysScreenSpecificActions() {
        composeTestRule.waitForIdle()

        // Filter-Icon
        composeTestRule
            .onNodeWithContentDescription("Filter")
            .assertIsDisplayed()

        // Chat/Nachrichten-Icon
        composeTestRule
            .onNodeWithContentDescription("Nachrichten")
            .assertIsDisplayed()
    }

    // ── Test 7: Notifications-Screen Action-Icon ──────────────

    /**
     * Prüft, ob der Notifications-Screen das screen-spezifische
     * "Alle gelesen"-Icon neben dem Overflow-Button anzeigt.
     */
    @Test
    fun notificationsScreen_displaysMarkAllReadAction() {
        navigateToTab("Aktivität")

        // "Alle gelesen"-Icon muss sichtbar sein (da ungelesene vorhanden)
        composeTestRule
            .onNodeWithContentDescription("Alle als gelesen markieren")
            .assertIsDisplayed()

        // Overflow-Button muss ebenfalls noch vorhanden sein
        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .assertIsDisplayed()
    }

    // ── Test 8: Menü nach Navigation konsistent ───────────────

    /**
     * Prüft, ob das Menü nach Tab-Navigation korrekt funktioniert:
     * Menü auf Home öffnen → zu Entdecken navigieren → Menü dort erneut öffnen.
     * Stellt sicher, dass der Menü-State bei Navigation zurückgesetzt wird.
     */
    @Test
    fun topBar_menuWorksAfterTabNavigation() {
        composeTestRule.waitForIdle()

        // Menü auf Home-Screen öffnen
        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Einstellungen").assertIsDisplayed()

        // Menü schließen durch Auswahl
        composeTestRule.onNodeWithText("Darstellung").performClick()
        composeTestRule.waitForIdle()

        // Zu Entdecken navigieren
        navigateToTab("Entdecken")

        // Menü auf Entdecken-Screen erneut öffnen
        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .performClick()
        composeTestRule.waitForIdle()

        // Alle Menü-Einträge müssen wieder sichtbar sein
        composeTestRule.onNodeWithText("Einstellungen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Abmelden").assertIsDisplayed()
    }

    // ── Test 9: Vollständiger Menü-Flow über mehrere Screens ──

    /**
     * End-to-End-Test: Öffnet das Menü auf 3 verschiedenen Screens
     * nacheinander und prüft jedes Mal die vollständige Menü-Anzeige.
     * Stellt die Robustheit der globalen Menü-Architektur sicher.
     */
    @Test
    fun topBar_fullMenuFlowAcrossScreens() {
        val screensToTest = listOf(
            "Home" to "Kliq",
            "Entdecken" to "Entdecken",
            "Profil" to "Profil"
        )

        screensToTest.forEach { (tabLabel, expectedTitle) ->
            // Zum Tab navigieren
            navigateToTab(tabLabel)

            // Titel prüfen
            composeTestRule.onNodeWithText(expectedTitle).assertIsDisplayed()

            // Menü öffnen
            composeTestRule
                .onNodeWithContentDescription("Menü öffnen")
                .performClick()
            composeTestRule.waitForIdle()

            // Alle Einträge vorhanden
            composeTestRule.onNodeWithText("Einstellungen").assertIsDisplayed()
            composeTestRule.onNodeWithText("Profil bearbeiten").assertIsDisplayed()
            composeTestRule.onNodeWithText("Darstellung").assertIsDisplayed()
            composeTestRule.onNodeWithText("Über Kliq").assertIsDisplayed()
            composeTestRule.onNodeWithText("Abmelden").assertIsDisplayed()

            // Menü wieder schließen
            composeTestRule.onNodeWithText("Einstellungen").performClick()
            composeTestRule.waitForIdle()
        }
    }

    // ── Hilfsfunktionen ──────────────────────────────────────

    /**
     * Navigiert zu einem Tab über die Bottom-Navigation-Bar.
     *
     * @param tabLabel Label des Ziel-Tabs (z.B. "Entdecken", "Karte").
     */
    private fun navigateToTab(tabLabel: String) {
        composeTestRule.onNodeWithContentDescription(tabLabel).performClick()
        composeTestRule.waitForIdle()
    }
}
