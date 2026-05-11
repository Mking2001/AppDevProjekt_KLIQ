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

class NavigationFlowTest {

    /**
     * Startet die MainActivity für den UI-Test.
     * Da MainActivity mit @AndroidEntryPoint annotiert ist und die Application-Klasse
     * @HiltAndroidApp verwendet, wird Hilt für diesen Test normal initialisiert.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Verifiziert den vollständigen Bottom-Navigation-Flow.
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
