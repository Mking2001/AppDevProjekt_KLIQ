package com.kliq.app.ui.screens.verification

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.kliq.app.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SmsVerificationScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun navigateToVerificationScreen() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Optionen").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Abmelden").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun verificationScreen_initialState_displaysHeaderAndTimer() {

        composeTestRule.onNodeWithText("Code eingeben").assertIsDisplayed()
        composeTestRule.onNodeWithText("+49 176 12345678").assertIsDisplayed()

        composeTestRule.onNodeWithText("Code erneut senden in", substring = true).assertIsDisplayed()
    }

    @Test
    fun verificationScreen_wrongCode_displaysErrorState() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Code eingeben").assertIsDisplayed()

        composeTestRule.onNodeWithText("Code eingeben").performTextInput("000000")
        composeTestRule.waitForIdle()

        composeTestRule.mainClock.advanceTimeBy(1200L)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Ungültiger Code. Bitte überprüfe deine Eingabe.").assertIsDisplayed()
    }

    @Test
    fun verificationScreen_correctCode_displaysSuccessState() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Code eingeben").performTextInput("123456")
        composeTestRule.waitForIdle()

        composeTestRule.mainClock.advanceTimeBy(1200L)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Erfolgreich verifiziert").assertIsDisplayed()
    }
}
