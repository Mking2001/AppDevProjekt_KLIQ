package com.kliq.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 * This test verifies that the app starts correctly and renders the basic UI.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appStartsAndDisplaysWelcomeMessage() {
        // Check if the greeting text is displayed
        composeTestRule.onNodeWithText("Welcome to Kliq User!").assertIsDisplayed()
    }
}
