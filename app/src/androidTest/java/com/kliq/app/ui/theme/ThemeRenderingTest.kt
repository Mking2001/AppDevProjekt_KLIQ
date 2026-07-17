package com.kliq.app.ui.theme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kliq.app.MainActivity
import org.junit.Rule
import org.junit.Test

/**
 * Instrumentierte Tests zur Verifikation des Lila/Dark-Mode
 * Theme-Renderings in der Kliq App.
 *
 * Prüft, ob die High-Contrast-Lila-Theme-Elemente korrekt
 * dargestellt werden auf den verschiedenen Screens:
 * - Bottom Bar mit Lila-Akzent
 * - Chat-Listen-Tabs mit Lila-Indikator
 * - Chat-Sprechblasen in Lila-Farbgebung
 * - TopBar-Elemente und FAB-Buttons
 *
 * Die Tests validieren die korrekte Komposition der UI-Elemente,
 * da Farbwerte in Compose-Tests nicht direkt ausgelesen werden.
 * Die visuelle Verifikation (Farben, Gradienten) muss ergänzend
 * im Emulator manuell geprüft werden.
 */
class ThemeRenderingTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // ======================================================================
    // 1. Home-Screen: Theme-Elemente werden korrekt gerendert
    // ======================================================================

    /**
     * Verifiziert, dass der Home-Screen mit allen Theme-Elementen
     * korrekt gerendert wird: TopBar, Story-Row, Feed-Karten,
     * Bottom-Bar und FAB.
     */
    @Test
    fun homeScreenRendersAllThemeElements() {
        // TopBar-Titel
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        // Story-Row-Elemente (Avatar-Initialen)
        composeTestRule.onNodeWithText("Anna").assertIsDisplayed()

        // Feed-Karten
        composeTestRule.onNodeWithText("Anna M.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vor 15 Min.").assertIsDisplayed()

        // FAB (Neuer Beitrag)
        composeTestRule.onNodeWithContentDescription("Neuer Beitrag").assertIsDisplayed()

        // Filter-Icon in der TopBar
        composeTestRule.onNodeWithContentDescription("Filter").assertIsDisplayed()

        // Chat-Icon in der TopBar
        composeTestRule.onNodeWithContentDescription("Nachrichten").assertIsDisplayed()
    }

    // ======================================================================
    // 2. Chat-Liste: Tab-Layout und Theme-konsistente Darstellung
    // ======================================================================

    /**
     * Verifiziert, dass die Chat-Liste alle Theme-Elemente korrekt
     * anzeigt: TopBar, Tab-Row, Chat-Einträge mit Avataren,
     * Ungelesen-Badges und FAB.
     */
    @Test
    fun chatListRendersThemeElements() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        // TopBar mit Titel und Aktionen
        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Suchen").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Zurück").assertIsDisplayed()

        // Tab-Row
        composeTestRule.onNodeWithText("Öffentlich").assertIsDisplayed()
        composeTestRule.onNodeWithText("Privat").assertIsDisplayed()

        // Chat-Einträge mit allen Details
        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()
        composeTestRule.onNodeWithText("Heute ab 20 Uhr im Bootshaus! 🎶").assertIsDisplayed()
        composeTestRule.onNodeWithText("14:32").assertIsDisplayed()

        // FAB (Neuer Chat)
        composeTestRule.onNodeWithContentDescription("Neuer Chat").assertIsDisplayed()
    }

    // ======================================================================
    // 3. Chat-Detail: Sprechblasen-Rendering
    // ======================================================================

    /**
     * Verifiziert, dass der Chat-Detail-Screen alle Theme-Elemente
     * korrekt rendert: TopBar mit Avatar, Sprechblasen,
     * Datums-Header, Eingabeleiste und Sende-Button.
     */
    @Test
    fun chatDetailRendersAllThemeElements() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Afterwork Köln").performClick()
        composeTestRule.waitForIdle()

        // TopBar mit Chat-Name und Avatar-Initiale
        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()

        // Datums-Trennlinie
        composeTestRule.onNodeWithText("Heute").assertIsDisplayed()

        // Fremde Nachrichten (mit Absendername)
        composeTestRule.onNodeWithText("Max K.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hey Leute, wer ist heute dabei?").assertIsDisplayed()

        // Eigene Nachrichten
        composeTestRule.onNodeWithText("Bin auf jeden Fall am Start! 🙋‍♂️").assertIsDisplayed()

        // Eingabeleiste
        composeTestRule.onNodeWithText("Nachricht schreiben…").assertIsDisplayed()

        // Sende-Button
        composeTestRule.onNodeWithContentDescription("Senden").assertIsDisplayed()

        // Mehr-Optionen in der TopBar
        composeTestRule.onNodeWithContentDescription("Mehr").assertIsDisplayed()
    }

    // ======================================================================
    // 4. Privater Chat: Online-Status-Rendering
    // ======================================================================

    /**
     * Verifiziert, dass der Online-Status im privaten Chat-Detail
     * korrekt als grüner Text "Online" angezeigt wird.
     */
    @Test
    fun privateChatDetailShowsOnlineIndicator() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Privat").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Lisa W.").performClick()
        composeTestRule.waitForIdle()

        // Online-Status und Avatar-Initiale
        composeTestRule.onNodeWithText("Online").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lisa W.").assertIsDisplayed()
    }

    // ======================================================================
    // 5. Vollständige Bottom-Bar-Rendering-Verifikation
    // ======================================================================

    /**
     * Verifiziert, dass alle 5 Bottom-Bar-Tabs korrekt gerendert
     * werden und ihre Labels anzeigen.
     */
    @Test
    fun bottomBarRendersAllFiveTabs() {
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entdecken").assertIsDisplayed()
        composeTestRule.onNodeWithText("Karte").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aktivität").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profil").assertIsDisplayed()
    }
}
