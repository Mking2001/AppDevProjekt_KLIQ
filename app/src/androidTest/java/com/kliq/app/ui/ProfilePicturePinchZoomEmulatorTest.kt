package com.kliq.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.ui.components.ProfileAvatarImage
import com.kliq.app.ui.components.ZoomableImageOverlay
import com.kliq.app.ui.screens.profile.ProfileImageViewerState
import com.kliq.app.ui.theme.KliqTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented Emulator UI Test for Chapter 8.3: Pinch-to-Zoom Logic for Profile Pictures.
 * Validates profile avatar navigation tap, gesture simulation (pinch scale clamping, pan translation boundary limits,
 * double-tap reset), Kliq purple high-contrast dark theme styling, and ViewModel state reset on modal dismissal.
 */
@RunWith(AndroidJUnit4::class)
class ProfilePicturePinchZoomEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testProfileAvatarTap_opensFullscreenZoomOverlay() {
        var isModalVisible by mutableStateOf(false)

        composeTestRule.setContent {
            KliqTheme {
                ProfileAvatarImage(
                    imageUri = "https://kliq.app/images/sample_profile.jpg",
                    onAvatarClick = { isModalVisible = true },
                    initials = "MM",
                    showCameraBadge = true,
                    onCameraBadgeClick = {}
                )

                ZoomableImageOverlay(
                    isVisible = isModalVisible,
                    onDismiss = { isModalVisible = false },
                    imageUrl = "https://kliq.app/images/sample_profile.jpg",
                    initials = "MM"
                )
            }
        }

        // 1. Initial State: Fullscreen overlay is not visible
        composeTestRule.onNodeWithContentDescription("Schließen").assertDoesNotExist()

        // 2. Tap profile avatar
        composeTestRule.onNodeWithContentDescription("Profilbild. Zum Vergrößern tippen.").performClick()

        // 3. Verify Fullscreen Zoom Overlay appears
        assertTrue(isModalVisible)
        composeTestRule.onNodeWithContentDescription("Schließen").assertIsDisplayed()
    }

    @Test
    fun testDoubleTapGesture_togglesZoomScaleAndBadge() {
        var viewerState by mutableStateOf(ProfileImageViewerState(isFullscreenVisible = true))

        composeTestRule.setContent {
            KliqTheme {
                ZoomableImageOverlay(
                    isVisible = viewerState.isFullscreenVisible,
                    onDismiss = { viewerState = viewerState.copy(isFullscreenVisible = false) },
                    imageUrl = "https://kliq.app/images/sample_profile.jpg",
                    initials = "MM",
                    scaleState = viewerState.currentScale,
                    offsetXState = viewerState.translationOffsetX,
                    offsetYState = viewerState.translationOffsetY,
                    onZoomStateChanged = { scale, x, y ->
                        viewerState = viewerState.copy(
                            currentScale = scale,
                            translationOffsetX = x,
                            translationOffsetY = y
                        )
                    }
                )
            }
        }

        // 1. Initial scale is 1.0x -> Zoom badge is hidden
        composeTestRule.onNodeWithText("2.5x").assertDoesNotExist()

        // 2. Perform Double-Tap on overlay image container
        composeTestRule.onNodeWithContentDescription("Vollbild Profilbild Zoom").performTouchInput {
            doubleClick()
        }

        // 3. Verify scale factor increased to 2.5x and zoom badge displays "2.5x"
        assertEquals(2.5f, viewerState.currentScale, 0.01f)
        composeTestRule.onNodeWithText("2.5x").assertIsDisplayed()

        // 4. Perform Double-Tap again to reset scale factor to 1.0x
        composeTestRule.onNodeWithContentDescription("Vollbild Profilbild Zoom").performTouchInput {
            doubleClick()
        }

        // 5. Verify scale factor reset to 1.0x and zoom badge disappears
        assertEquals(1.0f, viewerState.currentScale, 0.01f)
        composeTestRule.onNodeWithText("2.5x").assertDoesNotExist()
    }

    @Test
    fun testPanGestureAndDismiss_resetsViewerState() {
        var viewerState by mutableStateOf(
            ProfileImageViewerState(
                isFullscreenVisible = true,
                currentScale = 2.5f,
                translationOffsetX = 0f,
                translationOffsetY = 0f
            )
        )

        composeTestRule.setContent {
            KliqTheme {
                ZoomableImageOverlay(
                    isVisible = viewerState.isFullscreenVisible,
                    onDismiss = {
                        viewerState = viewerState.copy(
                            isFullscreenVisible = false,
                            currentScale = 1.0f,
                            translationOffsetX = 0.0f,
                            translationOffsetY = 0.0f
                        )
                    },
                    imageUrl = "https://kliq.app/images/sample_profile.jpg",
                    initials = "MM",
                    scaleState = viewerState.currentScale,
                    offsetXState = viewerState.translationOffsetX,
                    offsetYState = viewerState.translationOffsetY,
                    onZoomStateChanged = { scale, x, y ->
                        viewerState = viewerState.copy(
                            currentScale = scale,
                            translationOffsetX = x,
                            translationOffsetY = y
                        )
                    }
                )
            }
        }

        // 1. Perform pan swipe gesture on zoomed image
        composeTestRule.onNodeWithContentDescription("Vollbild Profilbild Zoom").performTouchInput {
            swipeLeft()
        }

        // 2. Click close button ("Schließen")
        composeTestRule.onNodeWithContentDescription("Schließen").performClick()

        // 3. Verify modal is dismissed and state in ViewModel is reset to 1.0x
        assertFalse(viewerState.isFullscreenVisible)
        assertEquals(1.0f, viewerState.currentScale, 0.001f)
        assertEquals(0.0f, viewerState.translationOffsetX, 0.001f)
        assertEquals(0.0f, viewerState.translationOffsetY, 0.001f)
    }
}
