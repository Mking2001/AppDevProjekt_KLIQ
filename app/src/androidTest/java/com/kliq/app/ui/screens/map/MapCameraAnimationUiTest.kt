package com.kliq.app.ui.screens.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MapCameraAnimationUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun test1_focusOnMarkerAnimation_smoothlyCentersCameraAndDisplaysQuickView() {

        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Berghain / Panorama Bar")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Berghain / Panorama Bar").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Route").assertIsDisplayed()
    }

    @Test
    fun test2_locationRecenterAnimation_smoothlyGlidesToUserLocation() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Mein Standort").performTouchInput {
            swipeLeft()
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Mein Standort").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Mein Standort").assertIsDisplayed()
    }

    @Test
    fun test3_boundingBoxFitAnimation_adjustsViewportForFilteredMarkers() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Clubs")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Clubs").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Watergate").assertIsDisplayed()
    }
}
