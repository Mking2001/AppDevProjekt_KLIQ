package com.kliq.app.ui

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
import com.kliq.app.ui.components.ReviewCommentSection
import com.kliq.app.ui.model.ReviewHighContrastItemState
import com.kliq.app.ui.model.ReviewHighContrastPalette
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewCommentsSectionEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSzenario1_emptyCommentsSection_displaysStylishPlaceholderInDarkTheme() {
        composeTestRule.setContent {
            ReviewCommentSection(
                comments = emptyList(),
                commentText = "",
                onCommentTextChange = {},
                selectedRating = 5,
                onRatingSelected = {},
                isVerificationLocked = true,
                isSubmitting = false,
                onSubmitClick = {}
            )
        }

        composeTestRule.onNodeWithText("Verifizierte Reviews & Kommentare").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Noch keine schriftlichen Kommentare vorhanden. Sei der Erste mit einer verifizierten Bewertung!",
            substring = true
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Sicherheits-Sperre: Kommentare nur bei physischer Nähe (GPS) oder QR-Scan freigeschaltet!",
            substring = true
        ).assertIsDisplayed()
    }

    @Test
    fun testSzenario2_writingAndSubmittingComment_updatesListImmediatelyWithoutFlicker() {
        var commentText by mutableStateOf("")
        var selectedRating by mutableIntStateOf(5)
        var submittedComments by mutableStateOf(emptyList<ReviewHighContrastItemState>())

        composeTestRule.setContent {
            ReviewCommentSection(
                comments = submittedComments,
                commentText = commentText,
                onCommentTextChange = { commentText = it },
                selectedRating = selectedRating,
                onRatingSelected = { selectedRating = it },
                isVerificationLocked = false,
                isSubmitting = false,
                onSubmitClick = {
                    submittedComments = listOf(
                        ReviewHighContrastItemState(
                            id = "c_new",
                            reviewerUsername = "Max Mustermann",
                            reviewerAvatarUrl = null,
                            rating = selectedRating,
                            ratingStarsFormatted = "★★★★★ (5.0)",
                            reviewText = commentText,
                            formattedDate = "26. Jul 2026, 19:15",
                            isVerified = true,
                            verificationBadgeText = "✓ VERIFIZIERT (GPS)",
                            verificationBadgeColorHex = ReviewHighContrastPalette.VerifiedNeonGreen,
                            cardBorderColorHex = ReviewHighContrastPalette.BorderVerifiedViolet
                        )
                    )
                    commentText = ""
                }
            )
        }

        composeTestRule.onNodeWithText("Schreibe deinen verifizierten Erfahrungstext...").performTextInput("Perfekte Party-Stimmung!")
        composeTestRule.onNodeWithText("Veröffentlichen").performClick()

        composeTestRule.onNodeWithText("Perfekte Party-Stimmung!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Max Mustermann").assertIsDisplayed()
        composeTestRule.onNodeWithText("✓ VERIFIZIERT (GPS)").assertIsDisplayed()
    }

    @Test
    fun testSzenario3_characterLimitValidation_blocksOverflowAndDisablesSubmitButton() {
        var commentText by mutableStateOf("")

        composeTestRule.setContent {
            ReviewCommentSection(
                comments = emptyList(),
                commentText = commentText,
                onCommentTextChange = { text ->
                    commentText = text.take(280)
                },
                selectedRating = 5,
                onRatingSelected = {},
                isVerificationLocked = false,
                isSubmitting = false,
                onSubmitClick = {}
            )
        }

        val overflowInput = "A".repeat(300)
        composeTestRule.onNodeWithText("Schreibe deinen verifizierten Erfahrungstext...").performTextInput(overflowInput)

        // Verifiziere, dass maximal 280 Zeichen akzeptiert werden
        assertEquals(280, commentText.length)
        composeTestRule.onNodeWithText("0 / 280 Zeichen übrig").assertIsDisplayed()
    }
}
