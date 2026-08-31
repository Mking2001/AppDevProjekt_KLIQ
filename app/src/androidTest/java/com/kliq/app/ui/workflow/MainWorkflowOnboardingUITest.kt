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

@RunWith(AndroidJUnit4::class)
class MainWorkflowOnboardingUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

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

        composeTestRule.onNodeWithText("Starte dein Nightlife-Erlebnis").assertIsDisplayed()
        composeTestRule.onNodeWithText("Handynummer eingeben").assertIsDisplayed()

        composeTestRule.onNodeWithText("151 2345678").performTextInput("1512345678")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("SMS-Code anfordern").assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        assertTrue(isOtpSentState)
        composeTestRule.onNodeWithText("Bestätigungscode eingeben").assertIsDisplayed()

        composeTestRule.onNodeWithText("123456").performTextInput("123456")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Code bestätigen").assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        assertTrue(isLoginSuccessState)
        assertEquals("1512345678", enteredPhone)
        assertEquals("123456", enteredOtp)
    }

    @Test
    fun testProfileCreationFormAndValidation() {
        composeTestRule.setContent {
            KliqTheme {
                ProfileCreationScreen(
                    onProfileCreated = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Erstelle dein Profil").assertIsDisplayed()
        composeTestRule.onNodeWithText("Benutzername *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alter *").assertIsDisplayed()
        composeTestRule.onNodeWithText("Heimatstadt *").assertIsDisplayed()

        composeTestRule.onNodeWithText("z.B. alex_night").performTextInput("alex_night")
        composeTestRule.onNodeWithText("z.B. 24").performTextInput("24")
        composeTestRule.onNodeWithText("z.B. Berlin").performTextInput("Berlin")
        composeTestRule.onNodeWithText("Erzähle etwas über deine Musik- und Club-Präferenzen...").performTextInput("Techno Fan aus Berlin")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Profil erstellen").assertExists().assertHasClickAction()
    }

    @Test
    fun testIntentMatchingPreferenceSelection() {
        composeTestRule.setContent {
            KliqTheme {
                IntentMatchingScreen(
                    onIntentConfirmed = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Was suchst du bei Kliq?").assertIsDisplayed()

        composeTestRule.onNodeWithText("Neue Leute & Party-Buddies").assertIsDisplayed()
        composeTestRule.onNodeWithText("Flirten, Dating & Ausgehen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Offen für alles").assertIsDisplayed()

        composeTestRule.onNodeWithText("Offen für alles").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Auswahl bestätigen").assertExists().assertHasClickAction()
    }

    @Test
    fun testConsumptionHabitsSelectionFlow() {
        composeTestRule.setContent {
            KliqTheme {
                ConsumptionHabitsScreen(
                    onHabitsConfirmed = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Rauchen & Trinken").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rauchverhalten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Trinkverhalten").assertIsDisplayed()

        composeTestRule.onNodeWithText("Nichtraucher").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Gesellschaftstrinker").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Auswahl speichern & Weiter").assertExists().assertHasClickAction()
    }

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

        composeTestRule.onNodeWithText("Erstelle dein Profil").assertIsDisplayed()
        assertEquals(1, currentStep)
    }
}
