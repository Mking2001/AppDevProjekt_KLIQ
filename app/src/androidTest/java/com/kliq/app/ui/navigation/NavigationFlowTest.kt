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

    @Test
    fun verifyBottomNavigationFlow() {
        // 1. Verifiziere, dass die App auf dem Home-Screen startet
        composeTestRule.onNodeWithText("Dein persönlicher Feed").assertIsDisplayed()

        // 2. Navigiere zum 'Entdecken'-Screen
        composeTestRule.onNode(hasText("Entdecken") and hasRole(Role.Tab)).performClick()
        // Kurze Wartezeit für die Animation ist in Compose-Tests implizit (Idle-Synchronisation)
        composeTestRule.onNodeWithText("Neue Leute & Events finden").assertIsDisplayed()

        // 3. Navigiere zum 'Karte'-Screen
        composeTestRule.onNode(hasText("Karte") and hasRole(Role.Tab)).performClick()
        composeTestRule.onNodeWithText("Entdecke was um dich herum passiert").assertIsDisplayed()

        // 4. Navigiere zum 'Aktivität'-Screen (Notifications)
        composeTestRule.onNode(hasText("Aktivität") and hasRole(Role.Tab)).performClick()
        composeTestRule.onNodeWithText("Deine Benachrichtigungen").assertIsDisplayed()

        // 5. Navigiere zum 'Profil'-Screen
        composeTestRule.onNode(hasText("Profil") and hasRole(Role.Tab)).performClick()
        composeTestRule.onNodeWithText("Dein persönliches Profil").assertIsDisplayed()

        // 6. Navigiere zurück zum 'Home'-Screen, um den vollständigen Loop zu testen
        composeTestRule.onNode(hasText("Home") and hasRole(Role.Tab)).performClick()
        composeTestRule.onNodeWithText("Dein persönlicher Feed").assertIsDisplayed()
    }
}
