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

/**
 * UI-Test-Szenario für die SMS-Code-Verifizierung (Kapitel 3.2).
 *
 * Überprüft die Use Cases:
 * 1. Erstanzeige (Timer läuft, Resend-Button deaktiviert)
 * 2. Fehleingabe ("000000" -> Fehlermeldung)
 * 3. Erfolgreiche Verifizierung ("123456" -> Erfolgsmeldung / Onboarding-Übergang)
 */
class SmsVerificationScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun navigateToVerificationScreen() {
        composeTestRule.waitForIdle()
        // Klick auf Overflow Menu -> Abmelden (Logout-Menüpunkt dient als Test-Einstiegspunkt für die SMS-Verifizierung)
        composeTestRule.onNodeWithContentDescription("Optionen").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Abmelden").performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun verificationScreen_initialState_displaysHeaderAndTimer() {
        // Erstanzeige: Header und Telefonnummer
        composeTestRule.onNodeWithText("Code eingeben").assertIsDisplayed()
        composeTestRule.onNodeWithText("+49 176 12345678").assertIsDisplayed()

        // Timer läuft rückwärts (Text "Code erneut senden in 30s" oder ähnlich)
        composeTestRule.onNodeWithText("Code erneut senden in", substring = true).assertIsDisplayed()
    }

    @Test
    fun verificationScreen_wrongCode_displaysErrorState() {
        composeTestRule.waitForIdle()

        // Verifizierungs-Screen ist geladen
        composeTestRule.onNodeWithText("Code eingeben").assertIsDisplayed()

        // Eingabe eines falschen Codes
        // Klick auf das unsichtbare TextField / Container und Eingabe
        composeTestRule.onNodeWithText("Code eingeben").performTextInput("000000")
        composeTestRule.waitForIdle()

        // Warten auf die Asynchronität (Mock-Dienst Ladezeit 1 Sekunde)
        composeTestRule.mainClock.advanceTimeBy(1200L)
        composeTestRule.waitForIdle()

        // Visual Feedback / Fehlermeldung wird angezeigt
        composeTestRule.onNodeWithText("Ungültiger Code. Bitte überprüfe deine Eingabe.").assertIsDisplayed()
    }

    @Test
    fun verificationScreen_correctCode_displaysSuccessState() {
        composeTestRule.waitForIdle()

        // Eingabe des validen Test-Codes
        composeTestRule.onNodeWithText("Code eingeben").performTextInput("123456")
        composeTestRule.waitForIdle()

        // Warten auf den Mock-Dienst (1s Ladezeit)
        composeTestRule.mainClock.advanceTimeBy(1200L)
        composeTestRule.waitForIdle()

        // Erfolgsmeldung sichtbar
        composeTestRule.onNodeWithText("Erfolgreich verifiziert").assertIsDisplayed()
    }
}
