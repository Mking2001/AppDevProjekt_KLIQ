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

        composeTestRule.onNodeWithText("Bewertung abgeben").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bitte wähle eine Sterne-Bewertung (1 bis 5)").assertIsDisplayed()

        for (i in 1..5) {
            composeTestRule.onNodeWithContentDescription("Stern $i von 5").assertIsDisplayed()
        }

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

        composeTestRule.onNodeWithContentDescription("Stern 3 von 5").performClick()
        assertEquals(3, currentRating)

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

        composeTestRule.onNodeWithContentDescription("Stern 4 von 5").performClick()
        assertEquals(4, uiState.rating)

        val testComment = "Absolut geniale Stimmung im Club!"
        composeTestRule
            .onNodeWithText("Schreibe einen optionalen Erfahrungsbericht...")
            .performTextInput(testComment)
        assertEquals(testComment, uiState.reviewText)

        val submitButton = composeTestRule.onNodeWithText("Bewertung absenden")
        submitButton.assertIsDisplayed().assertIsEnabled()

        submitButton.performClick()
        assertTrue(submitted)
        assertTrue(uiState.status is RatingSubmitStatus.Submitting)

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

        composeTestRule.onNodeWithText("Bewertung absenden").performClick()

        composeTestRule.onNodeWithText("Fehler beim Senden der Bewertung.").assertIsDisplayed()
    }
}
