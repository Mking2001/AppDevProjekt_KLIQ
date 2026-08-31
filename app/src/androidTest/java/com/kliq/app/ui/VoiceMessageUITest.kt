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

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Aktivität").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Aktivität").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onAllNodesWithContentDescription("Chat öffnen")[0].performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Sprachnachricht aufnehmen")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Sprachnachricht aufnehmen")
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Aufnahme verwerfen")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Sprachnachricht senden")
            .assertIsDisplayed()

        Thread.sleep(5200)

        composeTestRule
            .onNodeWithContentDescription("Sprachnachricht senden")
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onAllNodesWithText("🎤 Sprachnachricht")[0]
            .assertIsDisplayed()

        composeTestRule
            .onAllNodesWithContentDescription("Abspielen")[0]
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Pause")
            .assertIsDisplayed()

        Thread.sleep(1500)
        composeTestRule
            .onNodeWithContentDescription("Pause")
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onAllNodesWithContentDescription("Abspielen")[0]
            .assertIsDisplayed()
    }
}
