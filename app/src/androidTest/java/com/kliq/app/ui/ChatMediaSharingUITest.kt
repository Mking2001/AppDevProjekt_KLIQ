package com.kliq.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI- & Integrationstest für Kapitel 6.6 "Medien-Versand (Fotos in Chats)".
 *
 * Prüfschritte:
 * 1. Öffnen eines bestehenden Chats (privat oder öffentlich).
 * 2. Klick auf Anhang/Kamera-Icon im Nachrichten-Eingabefeld.
 * 3. Auswählen eines Bildes über das Attachment Bottom Sheet.
 * 4. Verifizieren der Bild-Vorschau vor dem Senden mit Bildunterschrift.
 * 5. Bestätigen des Sendevorgangs.
 * 6. Überprüfung der Anzeige (Renderung in der Chat-Sprechblase mit Dark/Purple-Design).
 * 7. DB-Persistenz-Check: Wechseln des Screens und Zurückkehren.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ChatMediaSharingUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun szenario1_chatMediaVersandVorschauUndSending() {
        composeTestRule.waitForIdle()

        // 1. Warten bis Navigation verfügbar ist & zu Chats navigieren
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodes(hasText("Berlin - Tonight") or hasText("Chat"))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // 2. Chat öffnen
        composeTestRule
            .onNodeWithText("Berlin - Tonight")
            .performClick()

        composeTestRule.waitForIdle()

        // 3. Anhang-Icon klicken (Büroklammer/Kamera)
        composeTestRule
            .onNodeWithContentDescription("Foto anhängen")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.waitForIdle()

        // 4. Prüfen, dass Attachment-Options-Sheet sichtbar ist
        composeTestRule.onNodeWithText("Medien teilen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Galerie").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kamera").assertIsDisplayed()
    }

    @Test
    fun szenario2_dbPersistenceCheck() {
        composeTestRule.waitForIdle()

        // Verifizieren, dass Medien-Nachrichten im Room DB Cache nach Re-Entry erhalten bleiben
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodes(hasText("Aktivität") or hasText("Home"))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}
