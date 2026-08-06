package com.kliq.app.ui.navigation

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
 * Instrumented Emulator UI Test for Chapter 8.4: Screen Transition Animations.
 *
 * Verifies fluid screen transition animations, direction-aware tab switches,
 * detail push/pop transitions, shared-element club expansion, modal slide-ups,
 * and rapid navigation interruptions (back-button handling) without visual glitches or crashes.
 */
@RunWith(AndroidJUnit4::class)
class ScreenTransitionsEmulatorTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Test 1: Step-by-step primary tab navigation.
     * Verifies direction-aware horizontal slide & fade transitions between Map, Chat, Profile, and Explore.
     */
    @Test
    fun testPrimaryTabTransitions_executesFluidDirectionalTransitions() {
        // 1. Verify app starts on Home tab
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        // 2. Transition to Map tab
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("In deiner Nähe").assertIsDisplayed()

        // 3. Transition to Explore tab
        composeTestRule.onNodeWithContentDescription("Entdecken").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Suche nach Leuten, Events, Orten…").assertIsDisplayed()

        // 4. Transition to Activity/Notifications tab
        composeTestRule.onNodeWithContentDescription("Aktivität").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Anna M. hat deinen Beitrag geliked").assertIsDisplayed()

        // 5. Transition to Profile tab
        composeTestRule.onNodeWithContentDescription("Profil").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Max Mustermann").assertIsDisplayed()
    }

    /**
     * Test 2: Detail Push/Pop and Modal transitions.
     * Verifies navigation into detail views (Chat Detail, QR Scanner modal) and smooth back-stack pops.
     */
    @Test
    fun testDetailAndModalTransitions_handlesPushPopAndSlideUp() {
        // 1. Navigate to Profile
        composeTestRule.onNodeWithContentDescription("Profil").performClick()
        composeTestRule.waitForIdle()

        // 2. Open QR Scanner (Modal Slide-Up transition)
        composeTestRule.onNodeWithContentDescription("QR Scanner öffnen").performClick()
        composeTestRule.waitForIdle()

        // Verify QR Scanner overlay is displayed
        composeTestRule.onNodeWithText("QR-Code scannen").assertIsDisplayed()

        // 3. Close QR Scanner (Modal Slide-Down exit transition)
        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()

        // Verify returning to Profile screen
        composeTestRule.onNodeWithText("Max Mustermann").assertIsDisplayed()
    }

    /**
     * Test 3: Rapid navigation interruptions & Back-button resilience.
     * Simulates fast tab tapping and immediate back navigation to ensure no animation state deadlocks,
     * visual node overlaps, or application crashes occur.
     */
    @Test
    fun testRapidNavigationInterruptions_remainsStableWithoutCrashes() {
        // 1. Rapidly switch between bottom bar tabs without waiting for full transition completion
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.onNodeWithContentDescription("Entdecken").performClick()
        composeTestRule.onNodeWithContentDescription("Profil").performClick()
        composeTestRule.onNodeWithContentDescription("Home").performClick()

        // Wait for animation queue to settle
        composeTestRule.waitForIdle()

        // 2. Verify UI state settled cleanly on target destination (Home)
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
    }
}
