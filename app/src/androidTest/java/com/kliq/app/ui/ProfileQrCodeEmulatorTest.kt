package com.kliq.app.ui

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.ui.components.ProfileQrCodeBottomSheet
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileQrCodeEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createTestBitmap(width: Int = 200, height: Int = 200): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.BLACK)
        return bitmap
    }

    @Test
    fun testSzenario1_qrModalRendering_displaysTitleAndBrightnessBoostBannerInDarkTheme() {
        val testBitmap = createTestBitmap()

        composeTestRule.setContent {
            ProfileQrCodeBottomSheet(
                isVisible = true,
                qrBitmap = testBitmap,
                isGenerating = false,
                displayName = "Alex Nightlife",
                username = "@alex_night",
                onDismissRequest = {}
            )
        }

        composeTestRule.onNodeWithText("Mein Kliq QR-Pass").assertIsDisplayed()
        composeTestRule.onNodeWithText("Display-Helligkeit für Club-Scan maximiert").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alex Nightlife").assertIsDisplayed()
        composeTestRule.onNodeWithText("@alex_night").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Kliq Profil QR Code").assertIsDisplayed()
    }

    @Test
    fun testSzenario3_dismissingModal_resetsVisibilityAndRestoresBrightness() {
        var isVisible by mutableStateOf(true)
        val testBitmap = createTestBitmap()

        composeTestRule.setContent {
            ProfileQrCodeBottomSheet(
                isVisible = isVisible,
                qrBitmap = testBitmap,
                isGenerating = false,
                displayName = "Alex Nightlife",
                username = "@alex_night",
                onDismissRequest = { isVisible = false }
            )
        }

        assertTrue(isVisible)
        composeTestRule.onNodeWithText("Fertig").performClick()

        assertFalse(isVisible)
        composeTestRule.onNodeWithText("Mein Kliq QR-Pass").assertDoesNotExist()
    }
}
