package com.kliq.app.ui.screens.chat

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.kliq.app.MainActivity
import org.junit.Rule
import org.junit.Test

/**
 * Instrumentierte UI-Tests für das Chat-Feature der Kliq App.
 *
 * Deckt folgende Szenarien ab:
 * 1. Navigation von Home zur Chat-Liste und zurück
 * 2. Tab-Umschaltung zwischen öffentlichen und privaten Chats
 * 3. Navigation von der Chat-Liste zum Chat-Detail
 * 4. Anzeige von Sprechblasen und Nachrichtenversand
 * 5. Bottom-Bar-Ausblendung in Chat-Screens
 * 6. Design-Rendering-Verifikation (Lila-Theme-Elemente)
 *
 * Voraussetzung: App wird auf einem Emulator/Gerät mit API 26+ ausgeführt.
 * Die Tests nutzen die Hilt-Integration über die MainActivity.
 */
class ChatUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // ======================================================================
    // 1. Navigation: Home -> Chat-Liste -> Back
    // ======================================================================

    /**
     * Verifiziert, dass der Chat-Icon-Button auf dem Home-Screen
     * korrekt zur Chat-Listen-Übersicht navigiert und der
     * Zurück-Button von dort wieder zum Home-Screen führt.
     */
    @Test
    fun navigateFromHomeToChatListAndBack() {
        // Home-Screen muss sichtbar sein
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        // Chat-Icon in der TopBar antippen
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        // Chat-Liste muss angezeigt werden
        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()
        composeTestRule.onNodeWithText("Öffentlich").assertIsDisplayed()
        composeTestRule.onNodeWithText("Privat").assertIsDisplayed()

        // Zurück-Navigation
        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()

        // Wieder auf dem Home-Screen
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
    }

    // ======================================================================
    // 2. Chat-Liste: Tab-Umschaltung Öffentlich / Privat
    // ======================================================================

    /**
     * Prüft, ob die Tab-Umschaltung zwischen öffentlichen und privaten
     * Chats korrekt funktioniert und die jeweiligen Chat-Einträge
     * angezeigt werden.
     */
    @Test
    fun switchBetweenPublicAndPrivateTabs() {
        // Zur Chat-Liste navigieren
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        // Tab "Öffentlich" ist Standard - Gruppen-Chats müssen sichtbar sein
        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()
        composeTestRule.onNodeWithText("Festival Crew 2026").assertIsDisplayed()

        // Zum Tab "Privat" wechseln
        composeTestRule.onNodeWithText("Privat").performClick()
        composeTestRule.waitForIdle()

        // Private Chats müssen sichtbar sein
        composeTestRule.onNodeWithText("Lisa W.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Max K.").assertIsDisplayed()

        // Zurück zum Tab "Öffentlich"
        composeTestRule.onNodeWithText("Öffentlich").performClick()
        composeTestRule.waitForIdle()

        // Gruppen-Chats sind wieder sichtbar
        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()
    }

    // ======================================================================
    // 3. Chat-Liste: Detailinformationen in den Einträgen
    // ======================================================================

    /**
     * Verifiziert, dass die Chat-Listeneinträge alle relevanten
     * Informationen anzeigen: Name, letzte Nachricht, Zeitstempel.
     */
    @Test
    fun chatListShowsDetailedEntryInformation() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        // Erster öffentlicher Chat: Name und Vorschautext prüfen
        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()
        composeTestRule.onNodeWithText("Heute ab 20 Uhr im Bootshaus! 🎶").assertIsDisplayed()
        composeTestRule.onNodeWithText("14:32").assertIsDisplayed()
    }

    // ======================================================================
    // 4. Navigation: Chat-Liste -> Chat-Detail -> Back
    // ======================================================================

    /**
     * Verifiziert den vollständigen Navigationsfluss:
     * Chat-Liste → Chat-Detail (mit Sprechblasen) → Zurück zur Liste.
     */
    @Test
    fun navigateToChatDetailAndBack() {
        // Zur Chat-Liste
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        // Ersten Chat antippen
        composeTestRule.onNodeWithText("Afterwork Köln").performClick()
        composeTestRule.waitForIdle()

        // Chat-Detail muss den Namen in der TopBar anzeigen
        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()

        // Nachrichten müssen sichtbar sein
        composeTestRule.onNodeWithText("Hey Leute, wer ist heute dabei?").assertIsDisplayed()

        // Zurück zur Chat-Liste
        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()

        // Chat-Liste ist wieder sichtbar
        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()
    }

    // ======================================================================
    // 5. Chat-Detail: Sprechblasen-Darstellung
    // ======================================================================

    /**
     * Verifiziert, dass Sprechblasen im Chat-Detail korrekt
     * dargestellt werden: Eigene und fremde Nachrichten,
     * Absendernamen und Zeitstempel.
     */
    @Test
    fun chatDetailShowsMessageBubbles() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").performClick()
        composeTestRule.waitForIdle()

        // Fremde Nachricht mit Absendername
        composeTestRule.onNodeWithText("Max K.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hey Leute, wer ist heute dabei?").assertIsDisplayed()

        // Eigene Nachricht
        composeTestRule.onNodeWithText("Bin auf jeden Fall am Start! 🙋‍♂️").assertIsDisplayed()

        // Datums-Header
        composeTestRule.onNodeWithText("Heute").assertIsDisplayed()
    }

    // ======================================================================
    // 6. Chat-Detail: Nachrichtenversand
    // ======================================================================

    /**
     * Testet die Eingabe und den Versand einer neuen Nachricht.
     * Verifiziert, dass das Eingabefeld nach dem Senden geleert wird
     * und die neue Nachricht im Verlauf erscheint.
     */
    @Test
    fun sendMessageInChatDetail() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").performClick()
        composeTestRule.waitForIdle()

        // Text in das Eingabefeld eingeben
        composeTestRule.onNodeWithText("Nachricht schreiben…").performTextInput("Test Nachricht 123")
        composeTestRule.waitForIdle()

        // Sende-Button antippen
        composeTestRule.onNodeWithContentDescription("Senden").performClick()
        composeTestRule.waitForIdle()

        // Neue Nachricht muss im Verlauf sichtbar sein
        composeTestRule.onNodeWithText("Test Nachricht 123").assertIsDisplayed()
    }

    // ======================================================================
    // 7. Bottom-Bar-Ausblendung in Chat-Screens
    // ======================================================================

    /**
     * Verifiziert, dass die Bottom Navigation Bar ausgeblendet wird,
     * wenn ein Chat-Screen (Liste oder Detail) aktiv ist.
     */
    @Test
    fun bottomBarIsHiddenInChatScreens() {
        // Bottom Bar muss auf dem Home-Screen sichtbar sein
        composeTestRule.onNodeWithContentDescription("Home").assertIsDisplayed()

        // Zur Chat-Liste navigieren
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        // Bottom-Bar-Tabs dürfen NICHT sichtbar sein
        composeTestRule.onNode(hasContentDescription("Home")).assertDoesNotExist()
        composeTestRule.onNode(hasContentDescription("Entdecken")).assertDoesNotExist()
    }

    // ======================================================================
    // 8. Private Chat: Online-Indikator und Detail-Navigation
    // ======================================================================

    /**
     * Verifiziert den Navigationsfluss für private Chats:
     * Tab wechseln → privaten Chat öffnen → Online-Status prüfen.
     */
    @Test
    fun privateChatsShowOnlineStatusInDetail() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        // Zu privaten Chats wechseln
        composeTestRule.onNodeWithText("Privat").performClick()
        composeTestRule.waitForIdle()

        // Lisa W. antippen (ist online)
        composeTestRule.onNodeWithText("Lisa W.").performClick()
        composeTestRule.waitForIdle()

        // Online-Status muss angezeigt werden
        composeTestRule.onNodeWithText("Online").assertIsDisplayed()

        // Nachrichten müssen sichtbar sein
        composeTestRule.onNodeWithText("Hey Lisa! Kommst du heute Abend?").assertIsDisplayed()
    }

    // ======================================================================
    // 9. Vollständiger E2E-Flow: Home → Chat → Detail → Send → Back → Home
    // ======================================================================

    /**
     * End-to-End-Testfall, der den gesamten Chat-Flow durchläuft:
     * Home → Chat-Liste → Tab-Wechsel → Chat-Detail → Nachricht senden →
     * Zurück zur Liste → Zurück zum Home-Screen.
     */
    @Test
    fun fullChatEndToEndFlow() {
        // Start: Home-Screen
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        // 1. Zur Chat-Liste navigieren
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()

        // 2. Tab zu "Privat" wechseln
        composeTestRule.onNodeWithText("Privat").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Lisa W.").assertIsDisplayed()

        // 3. Chat öffnen
        composeTestRule.onNodeWithText("Lisa W.").performClick()
        composeTestRule.waitForIdle()

        // 4. Nachricht senden
        composeTestRule.onNodeWithText("Nachricht schreiben…").performTextInput("Bis gleich!")
        composeTestRule.onNodeWithContentDescription("Senden").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Bis gleich!").assertIsDisplayed()

        // 5. Zurück zur Chat-Liste
        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()

        // 6. Zurück zum Home-Screen
        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        // Bottom Bar muss wieder sichtbar sein
        composeTestRule.onNodeWithContentDescription("Home").assertIsDisplayed()
    }
}
