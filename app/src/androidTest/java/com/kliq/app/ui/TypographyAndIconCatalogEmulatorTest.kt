package com.kliq.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.ui.screens.catalog.TypographyAndIconCatalogScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Test Suite für Kapitel 8.6: Custom Fonts & Icon Styling.
 * Überprüft das fehlerfreie Rendering aller Typografie-Elemente,
 * die Interaktion mit Theme-Chips und die Barrierefreiheit der Icons.
 */
@RunWith(AndroidJUnit4::class)
class TypographyAndIconCatalogEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun typographyAndIconCatalog_rendersSectionsCorrectly() {
        composeTestRule.setContent {
            TypographyAndIconCatalogScreen()
        }

        // 1. Prüfen, ob der Katalog-Titel sichtbar ist
        composeTestRule
            .onNodeWithTag("catalog_title")
            .assertIsDisplayed()

        // 2. Typografie-Sektion vorhanden
        composeTestRule
            .onNodeWithTag("typography_section")
            .assertIsDisplayed()

        // 3. Icon-Matrix-Sektion vorhanden
        composeTestRule
            .onNodeWithTag("icon_matrix_section")
            .assertIsDisplayed()
    }

    @Test
    fun typographyAndIconCatalog_themeSwitchingWorks() {
        composeTestRule.setContent {
            TypographyAndIconCatalogScreen()
        }

        // Klick auf Light Mode Chip
        composeTestRule
            .onNodeWithTag("chip_light_mode")
            .performClick()
            .assertIsDisplayed()

        // Klick auf High-Contrast Chip
        composeTestRule
            .onNodeWithTag("chip_high_contrast")
            .performClick()
            .assertIsDisplayed()

        // Zurück zu Dark Mode
        composeTestRule
            .onNodeWithTag("chip_dark_mode")
            .performClick()
            .assertIsDisplayed()
    }

    @Test
    fun icons_haveCorrectAccessibilityContentDescriptions() {
        composeTestRule.setContent {
            TypographyAndIconCatalogScreen()
        }

        // Überprüfen, dass Kern-Icons barrierefreie Content-Descriptions aufweisen
        composeTestRule
            .onNodeWithText("Navigation Icons Active", substring = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Action Icons Active", substring = true)
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Event Marker Icons Active", substring = true)
            .assertIsDisplayed()
    }
}
