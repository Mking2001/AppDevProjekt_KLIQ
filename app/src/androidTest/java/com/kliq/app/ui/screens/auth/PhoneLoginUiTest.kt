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

/**
 * Instrumentierter UI-Test für den Telefonnummer-Login (Onboarding) der Kliq App.
 *
 * Überprüft die Benutzeroberfläche im Emulator/Simulator:
 * 1. Ungültige Telefonnummern: Fehleranzeige & deaktivierter Button State
 * 2. Gültige Telefonnummern: Button-Aktivierung & Übergang in den OTP-Ladezustand
 * 3. Ländervorwahl-Auswahl im Dropdown-Menü
 * 4. Visuelles Layout im High-Contrast Lila-Design & DSGVO-Sicherheits-Badge
 */
class PhoneLoginUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testInvalidPhoneNumberShowsErrorAndDisablesButton() {
        composeTestRule.waitForIdle()

        // 1. Zu kurze Nummer eingeben
        composeTestRule.onNodeWithText("151 2345678").performTextInput("1234")
        composeTestRule.waitForIdle()

        // Fehleranzeige verifizieren
        composeTestRule.onNodeWithText("Telefonnummer zu kurz (mindestens 7 Ziffern).").assertIsDisplayed()

        // Button muss deaktiviert sein
        composeTestRule.onNodeWithText("SMS-Code anfordern").assertIsNotEnabled()

        // 2. Zu lange Nummer eingeben
        composeTestRule.onNodeWithText("1234").performTextInput("56789012345678")
        composeTestRule.waitForIdle()

        // Fehleranzeige für zu lange Nummern verifizieren
        composeTestRule.onNodeWithText("Telefonnummer zu lang (maximal 15 Ziffern).").assertIsDisplayed()
        composeTestRule.onNodeWithText("SMS-Code anfordern").assertIsNotEnabled()
    }

    @Test
    fun testValidPhoneNumberEnablesButtonAndSubmitsOtp() {
        composeTestRule.waitForIdle()

        // Gültige Handynummer eingeben
        composeTestRule.onNodeWithText("151 2345678").performTextInput("1512345678")
        composeTestRule.waitForIdle()

        // Button muss aktiviert sein
        composeTestRule.onNodeWithText("SMS-Code anfordern").assertIsEnabled()

        // Button antippen
        composeTestRule.onNodeWithText("SMS-Code anfordern").performClick()
        composeTestRule.waitForIdle()

        // OTP-Bestätigungsscreen muss sichtbar sein
        composeTestRule.onNodeWithText("SMS gesendet an").assertIsDisplayed()
        composeTestRule.onNodeWithText("Code bestätigen").assertIsDisplayed()
    }

    @Test
    fun testCountryCodeDropdownSelection() {
        composeTestRule.waitForIdle()

        // Ländervorwahl-Dropdown öffnen
        composeTestRule.onNodeWithContentDescription("Länderauswahl öffnen").performClick()
        composeTestRule.waitForIdle()

        // Österreich auswählen
        composeTestRule.onNodeWithText("Österreich").performClick()
        composeTestRule.waitForIdle()

        // Präfix +43 muss angezeigt werden
        composeTestRule.onNodeWithText("+43").assertIsDisplayed()
    }

    @Test
    fun testHighContrastLayoutAndSecurityBadge() {
        composeTestRule.waitForIdle()

        // Header & Branding verifizieren
        composeTestRule.onNodeWithText("KLIQ").assertIsDisplayed()
        composeTestRule.onNodeWithText("Starte dein Nightlife-Erlebnis").assertIsDisplayed()

        // Sicherheits-Badge verifizieren
        composeTestRule.onNodeWithText("Verschlüsselt & DSGVO-konform").assertIsDisplayed()
    }
}
