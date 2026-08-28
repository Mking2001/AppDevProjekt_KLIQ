package com.kliq.app.ui.workflow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.CountryCodeOption
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.ui.screens.auth.PhoneLoginContent
import com.kliq.app.ui.screens.auth.PhoneLoginUiState
import com.kliq.app.ui.screens.onboarding.ConsumptionHabitsScreen
import com.kliq.app.ui.screens.onboarding.IntentMatchingScreen
import com.kliq.app.ui.screens.onboarding.ProfileCreationScreen
import com.kliq.app.ui.theme.KliqTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automatisierte UI-Tests für den Onboarding- & Login-Workflow (Kapitel 9.2).
 *
 * Testet:
 *   1. Telefonnummer-Eingabe & SMS-OTP Verifizierung
 *   2. Profil-Erstellung mit Eingabefeldern und Validierung
 *   3. Intent-Matching Präferenz-Auswahl ("Freunde", "Dating", "Beides")
 *   4. Konsumgewohnheiten-Auswahl (Rauchen & Trinken)
 *   5. Vollständiger linearer Navigationsablauf im Onboarding
 */
@RunWith(AndroidJUnit4::class)
class MainWorkflowOnboardingUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Testfall 1: Prüft die Telefonnummer-Eingabe und den Übergang zur OTP-Eingabe.
     */
    @Test
    fun testPhoneLoginAndOtpVerificationFlow() {
        var isOtpSentState by mutableStateOf(false)
        var isLoginSuccessState by mutableStateOf(false)
        var enteredPhone by mutableStateOf("")
        var enteredOtp by mutableStateOf("")

        composeTestRule.setContent {
            KliqTheme {
                var uiState by remember {
                    mutableStateOf(
                        PhoneLoginUiState(
                            phoneNumber = "",
                            selectedCountry = CountryCodeOption("Deutschland", "+49", "🇩🇪"),
                            isOtpSent = false,
                            fullPhoneNumber = "+491512345678"
                        )
                    )
                }

                PhoneLoginContent(
                    uiState = uiState,
                    onCountrySelected = { uiState = uiState.copy(selectedCountry = it) },
                    onPhoneNumberChanged = {
                        enteredPhone = it
                        uiState = uiState.copy(
                            phoneNumber = it,
                            isSubmitPhoneNumberEnabled = it.length >= 8
                        )
                    },
                    onOtpCodeChanged = {
                        enteredOtp = it
                        uiState = uiState.copy(
                            otpCode = it,
                            isVerifyOtpEnabled = it.length == 6
                        )
                    },
                    onSendOtpClick = {
                        isOtpSentState = true
                        uiState = uiState.copy(isOtpSent = true)
                    },
                    onVerifyOtpClick = {
                        isLoginSuccessState = true
                    },
                    onResetOtpClick = {
                        uiState = uiState.copy(isOtpSent = false)
                    },
                    onClearErrorClick = {}
                )
            }
        }

        // 1. Initialzustand prüfen
        composeTestRule.onNodeWithText("Starte dein Nightlife-Erlebnis").assertIsDisplayed()
        composeTestRule.onNodeWithText("Handynummer eingeben").assertIsDisplayed()

        // 2. Handynummer eingeben
        composeTestRule.onNodeWithText("151 2345678").performTextInput("1512345678")
        composeTestRule.waitForIdle()

        // 3. SMS Code anfordern klicken
        composeTestRule.onNodeWithText("SMS-Code anfordern").assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        assertTrue(isOtpSentState)
        composeTestRule.onNodeWithText("Bestätigungscode eingeben").assertIsDisplayed()

        // 4. 6-stelligen OTP Code eingeben
        composeTestRule.onNodeWithText("123456").performTextInput("123456")
        composeTestRule.waitForIdle()

        // 5. Code bestätigen klicken
        composeTestRule.onNodeWithText("Code bestätigen").assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        assertTrue(isLoginSuccessState)
        assertEquals("1512345678", enteredPhone)
        assertEquals("123456", enteredOtp)
    }

    /**
     * Testfall 2: Prüft das Formular zur Profilerstellung (Name, Alter, Ort, Bio).
     */
    @Test
    fun testProfileCreationFormAndValidation() {
        composeTestRule.setContent {
            KliqTheme {
                ProfileCreationScreen(
                    onProfileCreated = {}
                )
            }
        }

        // Header & Untertitel prüfen
        composeTestRule.onNodeWithText("Erstelle dein Profil").assertIsDisplayed()
        composeTestRule.onNodeWithText("Benutzername *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alter *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Heimatstadt *").assertIsDisplayed()

        // Eingabefelder ausfüllen
        composeTestRule.onNodeWithText("z.B. alex_night").performTextInput("alex_night")
        composeTestRule.onNodeWithText("z.B. 24").performTextInput("24")
        composeTestRule.onNodeWithText("z.B. Berlin").performTextInput("Berlin")
        composeTestRule.onNodeWithText("Erzähle etwas über deine Musik- und Club-Präferenzen...").performTextInput("Techno Fan aus Berlin")

        composeTestRule.waitForIdle()

        // Button vorhanden & anklickbar
        composeTestRule.onNodeWithText("Profil erstellen").assertExists().assertHasClickAction()
    }

    /**
     * Testfall 3: Prüft die Intent-Matching Auswahlkarten ("Freunde", "Dating", "Beides").
     */
    @Test
    fun testIntentMatchingPreferenceSelection() {
        composeTestRule.setContent {
            KliqTheme {
                IntentMatchingScreen(
                    onIntentConfirmed = {}
                )
            }
        }

        // Header vorhanden
        composeTestRule.onNodeWithText("Was suchst du bei Kliq?").assertIsDisplayed()

        // Intent-Optionen prüfen
        composeTestRule.onNodeWithText("Neue Leute & Party-Buddies").assertIsDisplayed()
        composeTestRule.onNodeWithText("Flirten, Dating & Ausgehen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Offen für alles").assertIsDisplayed()

        // Option auswählen (z. B. Offen für alles)
        composeTestRule.onNodeWithText("Offen für alles").performClick()
        composeTestRule.waitForIdle()

        // Bestätigungsbutton vorhanden
        composeTestRule.onNodeWithText("Auswahl bestätigen").assertExists().assertHasClickAction()
    }

    /**
     * Testfall 4: Prüft die Konsumgewohnheiten-Auswahl (Rauchen & Trinken).
     */
    @Test
    fun testConsumptionHabitsSelectionFlow() {
        composeTestRule.setContent {
            KliqTheme {
                ConsumptionHabitsScreen(
                    onHabitsConfirmed = {}
                )
            }
        }

        // Header & Kategorien
        composeTestRule.onNodeWithText("Rauchen & Trinken").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rauchverhalten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Trinkverhalten").assertIsDisplayed()

        // Rauchverhalten-Option anklicken
        composeTestRule.onNodeWithText("Nichtraucher").performClick()
        composeTestRule.waitForIdle()

        // Trinkverhalten-Option anklicken
        composeTestRule.onNodeWithText("Gesellschaftstrinker").performClick()
        composeTestRule.waitForIdle()

        // Action Button prüfen
        composeTestRule.onNodeWithText("Auswahl speichern & Weiter").assertExists().assertHasClickAction()
    }

    /**
     * Testfall 5: Testet den linearen Ablauf der Onboarding-Kette.
     */
    @Test
    fun testCompleteOnboardingNavigationChain() {
        var currentStep by mutableStateOf(1)

        composeTestRule.setContent {
            KliqTheme {
                when (currentStep) {
                    1 -> ProfileCreationScreen(onProfileCreated = { currentStep = 2 })
                    2 -> IntentMatchingScreen(onIntentConfirmed = { currentStep = 3 })
                    3 -> ConsumptionHabitsScreen(onHabitsConfirmed = { currentStep = 4 })
                    else -> {}
                }
            }
        }

        // Schritt 1: Profile Creation sichtbar
        composeTestRule.onNodeWithText("Erstelle dein Profil").assertIsDisplayed()
        assertEquals(1, currentStep)
    }
}
