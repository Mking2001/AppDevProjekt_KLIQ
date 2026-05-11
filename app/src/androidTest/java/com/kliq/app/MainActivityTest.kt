package com.kliq.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Prüft, ob die App korrekt startet und
     * den Home-Screen mit dem "Kliq"-Titel anzeigt.
     */
    @Test
    fun appStartsAndDisplaysHomeScreen() {
        // Der Home-Screen zeigt den App-Titel "Kliq" in der TopAppBar
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
    }
}
