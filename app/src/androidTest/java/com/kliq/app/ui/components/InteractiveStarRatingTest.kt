package com.kliq.app.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.ui.theme.KliqTheme
import com.kliq.app.viewmodel.RatingUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InteractiveStarRatingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun interactiveStarRating_displaysFiveStars() {
        composeTestRule.setContent {
            KliqTheme {
                InteractiveStarRating(
                    rating = 3,
                    onRatingChanged = {}
                )
            }
        }

        for (i in 1..5) {
            composeTestRule
                .onNodeWithContentDescription("Stern $i von 5")
                .assertIsDisplayed()
        }
    }

    @Test
    fun interactiveStarRating_tapStar_updatesRating() {
        var currentRating = 0

        composeTestRule.setContent {
            KliqTheme {
                InteractiveStarRating(
                    rating = currentRating,
                    onRatingChanged = { currentRating = it }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Stern 4 von 5")
            .performClick()

        assertEquals(4, currentRating)
    }

    @Test
    fun ratingBottomSheet_submitButtonDisabled_whenZeroStarsSelected() {
        val state = RatingUiState(rating = 0)

        composeTestRule.setContent {
            KliqTheme {
                RatingBottomSheet(
                    isVisible = true,
                    uiState = state,
                    onRatingChanged = {},
                    onReviewTextChanged = {},
                    onSubmit = {},
                    onDismissRequest = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Stern auswählen zum Absenden")
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }
}
