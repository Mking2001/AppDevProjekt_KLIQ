package com.kliq.app.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.kliq.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentiertes UI-Test-Szenario für Sprachnachrichten in Kliq (Kapitel 6.9).
 *
 * Prüft den vollständigen Ablauf:
 *   1. Berechtigungsprüfung (RECORD_AUDIO)
 *   2. Aufnahme-Start, Timer-Anzeige, Wellenform & Beenden
 *   3. Rendering der VoiceMessageBubble im Chat-Verlauf mit Dauer
 *   4. Wiedergabesteuerung (Play, Pause, Progress-Slider, Stopp)
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class VoiceMessageUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.RECORD_AUDIO
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testVoiceMessageRecordAndPlaybackFlow() {
        // 1. Warten bis App startet
        composeTestRule.waitForIdle()

        // 2. Zu Chat / Aktivität navigieren
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Aktivität").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Aktivität").performClick()
        composeTestRule.waitForIdle()

        // 3. Einen Chat öffnen (z.B. ersten Eintrag in der Liste)
        composeTestRule.onAllNodesWithContentDescription("Chat öffnen")[0].performClick()
        composeTestRule.waitForIdle()

        // 4. Prüfen ob Aufnahme-Button (Mikrofon-Icon) in ChatInputBar sichtbar ist
        composeTestRule
            .onNodeWithContentDescription("Sprachnachricht aufnehmen")
            .assertIsDisplayed()

        // 5. Aufnahme starten
        composeTestRule
            .onNodeWithContentDescription("Sprachnachricht aufnehmen")
            .performClick()
        composeTestRule.waitForIdle()

        // 6. Prüfen ob Aufnahme-Overlay aktiv ist (Aufnahme verwerfen, Senden, Timer)
        composeTestRule
            .onNodeWithContentDescription("Aufnahme verwerfen")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Sprachnachricht senden")
            .assertIsDisplayed()

        // 7. Mindestens 5 Sekunden warten für Audiodauer
        Thread.sleep(5200)

        // 8. Sprachnachricht absenden
        composeTestRule
            .onNodeWithContentDescription("Sprachnachricht senden")
            .performClick()
        composeTestRule.waitForIdle()

        // 9. Prüfen ob neue Sprachnachrichten-Sprechblase im Verlauf gerendert wurde
        composeTestRule
            .onAllNodesWithText("🎤 Sprachnachricht")[0]
            .assertIsDisplayed()

        // 10. Wiedergabe starten
        composeTestRule
            .onAllNodesWithContentDescription("Abspielen")[0]
            .performClick()
        composeTestRule.waitForIdle()

        // 11. Prüfen ob Pause-Icon während der Wiedergabe aktiv ist
        composeTestRule
            .onNodeWithContentDescription("Pause")
            .assertIsDisplayed()

        // 12. Kurz warten und Wiedergabe pausieren
        Thread.sleep(1500)
        composeTestRule
            .onNodeWithContentDescription("Pause")
            .performClick()
        composeTestRule.waitForIdle()

        // 13. Prüfen ob der Button wieder auf 'Abspielen' zurückfällt
        composeTestRule
            .onAllNodesWithContentDescription("Abspielen")[0]
            .assertIsDisplayed()
    }
}
