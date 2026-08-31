package com.kliq.app.ui.screens.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.kliq.app.MainActivity
import org.junit.Rule
import org.junit.Test

class PhoneLoginUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testInvalidPhoneNumberShowsErrorAndDisablesButton() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("151 2345678").performTextInput("1234")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Telefonnummer zu kurz (mindestens 7 Ziffern).").assertIsDisplayed()

        composeTestRule.onNodeWithText("SMS-Code anfordern").assertIsNotEnabled()

        composeTestRule.onNodeWithText("1234").performTextInput("56789012345678")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Telefonnummer zu lang (maximal 15 Ziffern).").assertIsDisplayed()
        composeTestRule.onNodeWithText("SMS-Code anfordern").assertIsNotEnabled()
    }

    @Test
    fun testValidPhoneNumberEnablesButtonAndSubmitsOtp() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("151 2345678").performTextInput("1512345678")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("SMS-Code anfordern").assertIsEnabled()

        composeTestRule.onNodeWithText("SMS-Code anfordern").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("SMS gesendet an").assertIsDisplayed()
        composeTestRule.onNodeWithText("Code bestätigen").assertIsDisplayed()
    }

    @Test
    fun testCountryCodeDropdownSelection() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Länderauswahl öffnen").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Österreich").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("+43").assertIsDisplayed()
    }

    @Test
    fun testHighContrastLayoutAndSecurityBadge() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("KLIQ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Starte dein Nightlife-Erlebnis").assertIsDisplayed()

        composeTestRule.onNodeWithText("Verschlüsselt & DSGVO-konform").assertIsDisplayed()
    }
}
