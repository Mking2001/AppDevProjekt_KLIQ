package com.kliq.app.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.Review
import com.kliq.app.ui.theme.KliqTheme
import com.kliq.app.viewmodel.RatingSubmitStatus
import com.kliq.app.viewmodel.RatingUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automatisierter E2E UI-Test für das Sterne-Rating-System (Kapitel 5.2).
 *
 * Test-Abdeckung:
 *   1. Initialer Zustand (0 Sterne, Absende-Button deaktiviert)
 *   2. Gesten- & Interaktions-Test (3 Sterne -> 5 Sterne Umschalten)
 *   3. Text-Eingabe & Erfolgs-State (Loading -> Success Banner)
 *   4. Error-Handling (Fehlermeldungs-Banner bei Repository-Fehler)
 */
@RunWith(AndroidJUnit4::class)
class StarRatingSystemE2ETest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test1_initialState_displaysZeroStars_submitButtonDisabled() {
        val uiState = RatingUiState(rating = 0)

        composeTestRule.setContent {
            KliqTheme {
                RatingBottomSheet(
                    isVisible = true,
                    uiState = uiState,
                    onRatingChanged = {},
                    onReviewTextChanged = {},
                    onSubmit = {},
                    onDismissRequest = {}
                )
            }
        }

        // Validate Header & Prompt Text
        composeTestRule.onNodeWithText("Bewertung abgeben").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bitte wähle eine Sterne-Bewertung (1 bis 5)").assertIsDisplayed()

        // Validate 5 stars exist
        for (i in 1..5) {
            composeTestRule.onNodeWithContentDescription("Stern $i von 5").assertIsDisplayed()
        }

        // Validate Button Disabled State
        composeTestRule
            .onNodeWithText("Stern auswählen zum Absenden")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun test2_interactionTest_tapStars_updatesSelectionState() {
        var currentRating = 0

        composeTestRule.setContent {
            KliqTheme {
                var ratingState by remember { mutableStateOf(0) }
                InteractiveStarRating(
                    rating = ratingState,
                    onRatingChanged = {
                        ratingState = it
                        currentRating = it
                    }
                )
            }
        }

        // Tap 3rd star
        composeTestRule.onNodeWithContentDescription("Stern 3 von 5").performClick()
        assertEquals(3, currentRating)

        // Tap 5th star
        composeTestRule.onNodeWithContentDescription("Stern 5 von 5").performClick()
        assertEquals(5, currentRating)
    }

    @Test
    fun test3_submission_inputReviewAndSubmit_showsLoadingAndSuccessState() {
        var uiState by mutableStateOf(RatingUiState())
        var submitted = false

        composeTestRule.setContent {
            KliqTheme {
                RatingBottomSheet(
                    isVisible = true,
                    uiState = uiState,
                    onRatingChanged = { newRating -> uiState = uiState.copy(rating = newRating) },
                    onReviewTextChanged = { newText -> uiState = uiState.copy(reviewText = newText) },
                    onSubmit = {
                        submitted = true
                        uiState = uiState.copy(status = RatingSubmitStatus.Submitting)
                    },
                    onDismissRequest = {}
                )
            }
        }

        // 1. Select 4 stars
        composeTestRule.onNodeWithContentDescription("Stern 4 von 5").performClick()
        assertEquals(4, uiState.rating)

        // 2. Enter review text
        val testComment = "Absolut geniale Stimmung im Club!"
        composeTestRule
            .onNodeWithText("Schreibe einen optionalen Erfahrungsbericht...")
            .performTextInput(testComment)
        assertEquals(testComment, uiState.reviewText)

        // 3. Verify submit button is now enabled
        val submitButton = composeTestRule.onNodeWithText("Bewertung absenden")
        submitButton.assertIsDisplayed().assertIsEnabled()

        // 4. Click Submit
        submitButton.performClick()
        assertTrue(submitted)
        assertTrue(uiState.status is RatingSubmitStatus.Submitting)

        // 5. Transition to Success State
        uiState = uiState.copy(
            status = RatingSubmitStatus.Success(
                Review(
                    id = "rev_101",
                    reviewerUserId = "usr_1",
                    rating = 4,
                    text = testComment
                )
            )
        )

        // 6. Verify Success Banner
        composeTestRule.onNodeWithText("Vielen Dank für deine Bewertung!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fertig").assertIsDisplayed().assertIsEnabled()
    }

    @Test
    fun test4_errorHandling_repositoryFailure_displaysErrorMessage() {
        var uiState by mutableStateOf(RatingUiState(rating = 5))

        composeTestRule.setContent {
            KliqTheme {
                RatingBottomSheet(
                    isVisible = true,
                    uiState = uiState,
                    onRatingChanged = {},
                    onReviewTextChanged = {},
                    onSubmit = {
                        uiState = uiState.copy(
                            status = RatingSubmitStatus.Error("Fehler beim Senden der Bewertung.")
                        )
                    },
                    onDismissRequest = {}
                )
            }
        }

        // Trigger Submit
        composeTestRule.onNodeWithText("Bewertung absenden").performClick()

        // Verify Error Message Display
        composeTestRule.onNodeWithText("Fehler beim Senden der Bewertung.").assertIsDisplayed()
    }
}
