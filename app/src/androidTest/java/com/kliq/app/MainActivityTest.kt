/**
 * AI-GENERATED CODE
 * Dieser Test wurde aktualisiert durch KI für das neue Layout-Scaffolding.
 * Aktualisiert im Rahmen von Schritt 4: Layout-Scaffolding der Haupt-Screens.
 * Datum: 2026-05-11
 */
package com.kliq.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// ============================================================
// AI-generiert: Aktualisierter MainActivity-Start-Test.
// Prüft, ob die App korrekt startet und den Home-Screen rendert.
// ============================================================

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * AI-generiert: Prüft, ob die App korrekt startet und
     * den Home-Screen mit dem "Kliq"-Titel anzeigt.
     */
    @Test
    fun appStartsAndDisplaysHomeScreen() {
        // Der Home-Screen zeigt den App-Titel "Kliq" in der TopAppBar
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
    }
}
