/**
 * AI-GENERATED CODE
 * Dieser Test wurde aktualisiert durch KI für das neue Layout-Scaffolding.
 * Aktualisiert im Rahmen von Schritt 4: Layout-Scaffolding der Haupt-Screens.
 * Datum: 2026-05-11
 *
 * Ursprünglicher Test geprüft: Navigationsfluss zwischen allen 5 Tabs.
 * Anpassung: Screen-Inhalte entsprechen nun dem neuen Scaffolding
 * (nicht mehr simple Platzhalter-Texte).
 */
package com.kliq.app.ui.navigation

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasRole
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kliq.app.MainActivity
import org.junit.Rule
import org.junit.Test

// ============================================================
// AI-generiert: Aktualisierter Navigationsfluss-Test.
// Angepasst an die neuen Screen-Scaffoldings (Schritt 4).
// ============================================================

class NavigationFlowTest {

    /**
     * AI-generiert: Startet die MainActivity für den UI-Test.
     * Da MainActivity mit @AndroidEntryPoint annotiert ist und die Application-Klasse
     * @HiltAndroidApp verwendet, wird Hilt für diesen Test normal initialisiert.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * AI-generiert: Verifiziert den vollständigen Bottom-Navigation-Flow.
     * Navigiert durch alle 5 Tabs und prüft ob jeder Screen
     * seine Kern-UI-Elemente korrekt anzeigt.
     */
    @Test
    fun verifyBottomNavigationFlow() {
        // 1. Verifiziere, dass die App auf dem Home-Screen startet
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
        composeTestRule.onNodeWithText("Anna M.").assertIsDisplayed()

        // 2. Navigiere zum 'Entdecken'-Screen
        composeTestRule.onNode(hasText("Entdecken") and hasRole(Role.Tab)).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Suche nach Leuten, Events, Orten…").assertIsDisplayed()

        // 3. Navigiere zum 'Karte'-Screen
        composeTestRule.onNode(hasText("Karte") and hasRole(Role.Tab)).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("In deiner Nähe").assertIsDisplayed()

        // 4. Navigiere zum 'Aktivität'-Screen (Notifications)
        composeTestRule.onNode(hasText("Aktivität") and hasRole(Role.Tab)).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Anna M. hat deinen Beitrag geliked").assertIsDisplayed()

        // 5. Navigiere zum 'Profil'-Screen
        composeTestRule.onNode(hasText("Profil") and hasRole(Role.Tab)).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Max Mustermann").assertIsDisplayed()

        // 6. Navigiere zurück zum 'Home'-Screen, um den vollständigen Loop zu testen
        composeTestRule.onNode(hasText("Home") and hasRole(Role.Tab)).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
    }
}
