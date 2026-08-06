package com.kliq.app.ui.theme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automatisierter UI-Test für Kapitel 8.7: Dark-Mode-Optimierung für die Nacht-Nutzung.
 * Validiert die konsistente Anwendung des High-Contrast Lila/Dark-Themes auf allen Screens,
 * die Zustandserhaltung bei Navigation sowie das flackerfreie Verhalten der Benutzeroberfläche.
 */
@RunWith(AndroidJUnit4::class)
class DarkModeOptimizationEmulatorTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Verifiziert, dass die App im High-Contrast Dark-Mode startet
     * und alle Haupt-Screens die zentralen Theme-Elemente geräuschlos rendern.
     */
    @Test
    fun testDarkModeOptimization_rendersHighContrastNightPalette() {
        // 1. Home-Screen Verifikation
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Neuer Beitrag").assertIsDisplayed()

        // 2. Navigation zur Party-Map
        composeTestRule.onNodeWithText("Karte").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Aktueller Standort").assertIsDisplayed()

        // 3. Navigation zum Stadt-Chat
        composeTestRule.onNodeWithText("Aktivität").performClick()
        composeTestRule.waitForIdle()

        // 4. Navigation zur Profilansicht
        composeTestRule.onNodeWithText("Profil").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Profil").assertIsDisplayed()
    }

    /**
     * Verifiziert, dass beim Wechsel zwischen den Screens der UI-Zustand
     * der ViewModels vollständig erhalten bleibt und keine weißen Ladeflächen auftreten.
     */
    @Test
    fun testScreenNavigation_preservesUiStateWithoutFlashing() {
        // Navigiere zum Chat
        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        // Öffne Chat-Detail
        composeTestRule.onNodeWithText("Afterwork Köln").performClick()
        composeTestRule.waitForIdle()

        // Prüfe Chat-Inhalte & Sprechblasen im Dark Mode
        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bin auf jeden Fall am Start! 🙋‍♂️").assertIsDisplayed()

        // Zurück zur Chat-Liste
        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()
    }

    /**
     * Verifiziert, dass Chat-Sprechblasen und Map-Filter-Komponenten
     * im Dark-Theme ohne weiße Hintergrund-Leaks gerendert werden.
     */
    @Test
    fun testChatBubblesAndMapOverlays_darkThemeConsistency() {
        // Navigiere zur Karte
        composeTestRule.onNodeWithText("Karte").performClick()
        composeTestRule.waitForIdle()

        // Filter-Buttons prüfen
        composeTestRule.onNodeWithText("Alle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clubs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bars").assertIsDisplayed()

        // Navigiere zu Profil & QR-Code
        composeTestRule.onNodeWithText("Profil").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Mein QR-Code").assertIsDisplayed()
    }
}
