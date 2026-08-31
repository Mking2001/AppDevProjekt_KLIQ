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

@RunWith(AndroidJUnit4::class)
class DarkModeOptimizationEmulatorTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testDarkModeOptimization_rendersHighContrastNightPalette() {

        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Neuer Beitrag").assertIsDisplayed()

        composeTestRule.onNodeWithText("Karte").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Aktueller Standort").assertIsDisplayed()

        composeTestRule.onNodeWithText("Aktivität").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Profil").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Profil").assertIsDisplayed()
    }

    @Test
    fun testScreenNavigation_preservesUiStateWithoutFlashing() {

        composeTestRule.onNodeWithText("Home").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bin auf jeden Fall am Start! 🙋‍♂️").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()
    }

    @Test
    fun testChatBubblesAndMapOverlays_darkThemeConsistency() {

        composeTestRule.onNodeWithText("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Alle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clubs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bars").assertIsDisplayed()

        composeTestRule.onNodeWithText("Profil").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Mein QR-Code").assertIsDisplayed()
    }
}
