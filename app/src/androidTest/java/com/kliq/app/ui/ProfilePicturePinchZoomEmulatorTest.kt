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

        composeTestRule.onNodeWithContentDescription("Schließen").assertDoesNotExist()

        composeTestRule.onNodeWithContentDescription("Profilbild. Zum Vergrößern tippen.").performClick()

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

        composeTestRule.onNodeWithText("2.5x").assertDoesNotExist()

        composeTestRule.onNodeWithContentDescription("Vollbild Profilbild Zoom").performTouchInput {
            doubleClick()
        }

        assertEquals(2.5f, viewerState.currentScale, 0.01f)
        composeTestRule.onNodeWithText("2.5x").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Vollbild Profilbild Zoom").performTouchInput {
            doubleClick()
        }

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

        composeTestRule.onNodeWithContentDescription("Vollbild Profilbild Zoom").performTouchInput {
            swipeLeft()
        }

        composeTestRule.onNodeWithContentDescription("Schließen").performClick()

        assertFalse(viewerState.isFullscreenVisible)
        assertEquals(1.0f, viewerState.currentScale, 0.001f)
        assertEquals(0.0f, viewerState.translationOffsetX, 0.001f)
        assertEquals(0.0f, viewerState.translationOffsetY, 0.001f)
    }
}
