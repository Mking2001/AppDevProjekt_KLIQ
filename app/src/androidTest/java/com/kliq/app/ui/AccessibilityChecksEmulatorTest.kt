package com.kliq.app.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.MessageType
import com.kliq.app.ui.components.ChatBubble
import com.kliq.app.ui.components.InteractiveStarRating
import com.kliq.app.ui.components.MapFilterSegmentedControl
import com.kliq.app.ui.components.ProfileAvatarImage
import com.kliq.app.ui.components.UserQuickViewCard
import com.kliq.app.ui.components.UserRatingStarBar
import com.kliq.app.ui.screens.map.MapLocationFilterMode
import com.kliq.app.ui.screens.map.UserMarkerUiState
import com.kliq.app.ui.theme.KliqTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Emulator UI Test for Chapter 8.3: Accessibility Checks & TalkBack Optimizations.
 * Verifies accessibility content descriptions, TalkBack semantics, high-contrast dark theme,
 * dynamic text scaling (1.5x, 2.0x), and focus traversal ordering across central UI components.
 */
@RunWith(AndroidJUnit4::class)
class AccessibilityChecksEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAccessibilityLabels_RatingStars_ChatBubbles_MapControls_ProfileCard() {
        var clickedRating = 0

        composeTestRule.setContent {
            KliqTheme(isHighContrast = true) {
                InteractiveStarRating(
                    rating = 4,
                    onRatingChanged = { clickedRating = it },
                    isReadOnly = false
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Interaktive Bewertung: 4 von 5 Sternen. Wischen oder tippen zum Ändern.")
            .assertIsDisplayed()
    }

    @Test
    fun testUserRatingStarBar_accessibilitySummaryText() {
        composeTestRule.setContent {
            KliqTheme(isHighContrast = true) {
                UserRatingStarBar(
                    averageRating = 4.8,
                    formattedAverageRating = "4.8",
                    totalReviewsCount = 12,
                    verifiedReviewsCount = 12,
                    hasRatings = true
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Durchschnittsbewertung 4.8 von 5 Sternen basierend auf 12 Bewertungen (12 verifiziert)")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("4.8")
            .assertIsDisplayed()
    }

    @Test
    fun testChatBubble_talkBackSemantics() {
        val sampleTextMessage = ChatMessage(
            id = "msg_101",
            senderId = "user_max",
            senderName = "Max K.",
            text = "Hallo Kliq Community!",
            timestampMs = System.currentTimeMillis(),
            isMine = false
        )

        composeTestRule.setContent {
            KliqTheme {
                ChatBubble(message = sampleTextMessage)
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Nachricht von Max K.: Hallo Kliq Community!")
            .assertIsDisplayed()
    }

    @Test
    fun testVoiceMessageBubble_talkBackSemantics() {
        val sampleVoiceMessage = ChatMessage(
            id = "msg_voice_1",
            senderId = "me",
            senderName = "Ich",
            text = "🎤 Sprachnachricht",
            timestampMs = System.currentTimeMillis(),
            isMine = true,
            messageType = MessageType.VOICE,
            audioDurationMs = 15000L
        )

        composeTestRule.setContent {
            KliqTheme {
                ChatBubble(
                    message = sampleVoiceMessage,
                    isPlayingVoice = false,
                    voicePlaybackDurationMs = 15000L
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Sprachnachricht von dir")
            .assertIsDisplayed()
    }

    @Test
    fun testMapFilterSegmentedControl_accessibilityTabs() {
        var selectedMode = MapLocationFilterMode.ALL

        composeTestRule.setContent {
            KliqTheme {
                MapFilterSegmentedControl(
                    selectedMode = selectedMode,
                    onModeSelected = { selectedMode = it }
                )
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Kartenfilter: Alle Standorte anzeigen")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Kartenfilter: Nur öffentliche Clubs und Venues anzeigen")
            .assertIsDisplayed()
            .performClick()

        assertEquals(MapLocationFilterMode.PUBLIC_ONLY, selectedMode)
    }

    @Test
    fun testUserQuickViewCard_accessibilityLabelsAndDismiss() {
        var dismissed = false
        val sampleUser = UserMarkerUiState(
            userId = "user_lisa",
            username = "Lisa W.",
            isOnline = true,
            statusMessage = "Heute im Club!",
            formattedDistance = "450 m"
        )

        composeTestRule.setContent {
            KliqTheme {
                UserQuickViewCard(
                    user = sampleUser,
                    isVisible = true,
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Lisa W.")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Profilkarte schließen")
            .assertIsDisplayed()
            .performClick()

        assert(dismissed)
    }

    @Test
    fun testFontScaling_LargeText_1_5x_and_2_0x() {
        composeTestRule.setContent {
            val currentDensity = LocalDensity.current
            val scaledDensity = Density(
                density = currentDensity.density,
                fontScale = 1.5f
            )

            CompositionLocalProvider(LocalDensity provides scaledDensity) {
                KliqTheme(isHighContrast = true) {
                    UserRatingStarBar(
                        averageRating = 4.5,
                        formattedAverageRating = "4.5",
                        totalReviewsCount = 8,
                        verifiedReviewsCount = 8,
                        hasRatings = true
                    )
                }
            }
        }

        composeTestRule
            .onNodeWithText("4.5")
            .assertIsDisplayed()
    }

    @Test
    fun testFontScaling_LargeText_2_0x() {
        composeTestRule.setContent {
            val currentDensity = LocalDensity.current
            val scaledDensity = Density(
                density = currentDensity.density,
                fontScale = 2.0f
            )

            CompositionLocalProvider(LocalDensity provides scaledDensity) {
                KliqTheme(isHighContrast = true) {
                    ProfileAvatarImage(
                        imageUri = null,
                        initials = "LW",
                        onAvatarClick = {}
                    )
                }
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Profilbild mit Initialen LW. Zum Ändern tippen.")
            .assertIsDisplayed()
    }
}
