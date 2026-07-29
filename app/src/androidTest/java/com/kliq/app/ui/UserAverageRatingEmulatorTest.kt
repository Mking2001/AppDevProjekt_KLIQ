package com.kliq.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.ui.components.UserRatingStarBar
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserAverageRatingEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSzenario1_profileWithoutRatings_displaysPlaceholderInDarkTheme() {
        composeTestRule.setContent {
            UserRatingStarBar(
                averageRating = 0.0,
                formattedAverageRating = "0.0",
                totalReviewsCount = 0,
                verifiedReviewsCount = 0,
                hasRatings = false
            )
        }

        composeTestRule.onNodeWithText("Keine Bewertungen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Noch keine verifizierten Ratings").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Stern 1").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Stern 5").assertIsDisplayed()
    }

    @Test
    fun testSzenario2_correctAverageCalculation_displaysRounded4Dot3AndVerifiedBadge() {
        composeTestRule.setContent {
            UserRatingStarBar(
                averageRating = 4.333333333333333,
                formattedAverageRating = "4.3",
                totalReviewsCount = 3,
                verifiedReviewsCount = 3,
                hasRatings = true
            )
        }

        composeTestRule.onNodeWithText("4.3").assertIsDisplayed()
        composeTestRule.onNodeWithText("/ 5.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("3 Bewertungen (3 verifiziert)").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Stern 1").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Stern 4").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Stern 5").assertIsDisplayed()
    }

    @Test
    fun testSzenario3_extremeValues_perfectFiveStarsAndHighVolume_rendersWithoutLayoutOverflow() {
        composeTestRule.setContent {
            UserRatingStarBar(
                averageRating = 5.0,
                formattedAverageRating = "5.0",
                totalReviewsCount = 9999,
                verifiedReviewsCount = 9999,
                hasRatings = true
            )
        }

        composeTestRule.onNodeWithText("5.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("/ 5.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("9999 Bewertungen (9999 verifiziert)").assertIsDisplayed()
        for (i in 1..5) {
            composeTestRule.onNodeWithContentDescription("Stern $i").assertIsDisplayed()
        }
    }
}
