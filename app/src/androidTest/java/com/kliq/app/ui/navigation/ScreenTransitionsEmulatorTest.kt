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

@RunWith(AndroidJUnit4::class)
class ScreenTransitionsEmulatorTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testPrimaryTabTransitions_executesFluidDirectionalTransitions() {

        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("In deiner Nähe").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Entdecken").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Suche nach Leuten, Events, Orten…").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Aktivität").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Anna M. hat deinen Beitrag geliked").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Profil").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Max Mustermann").assertIsDisplayed()
    }

    @Test
    fun testDetailAndModalTransitions_handlesPushPopAndSlideUp() {

        composeTestRule.onNodeWithContentDescription("Profil").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("QR Scanner öffnen").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("QR-Code scannen").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Max Mustermann").assertIsDisplayed()
    }

    @Test
    fun testRapidNavigationInterruptions_remainsStableWithoutCrashes() {

        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.onNodeWithContentDescription("Entdecken").performClick()
        composeTestRule.onNodeWithContentDescription("Profil").performClick()
        composeTestRule.onNodeWithContentDescription("Home").performClick()

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
    }
}
