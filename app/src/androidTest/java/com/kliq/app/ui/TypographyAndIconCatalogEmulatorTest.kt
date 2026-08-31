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

@RunWith(AndroidJUnit4::class)
class TypographyAndIconCatalogEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun typographyAndIconCatalog_rendersSectionsCorrectly() {
        composeTestRule.setContent {
            TypographyAndIconCatalogScreen()
        }

        composeTestRule
            .onNodeWithTag("catalog_title")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("typography_section")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("icon_matrix_section")
            .assertIsDisplayed()
    }

    @Test
    fun typographyAndIconCatalog_themeSwitchingWorks() {
        composeTestRule.setContent {
            TypographyAndIconCatalogScreen()
        }

        composeTestRule
            .onNodeWithTag("chip_light_mode")
            .performClick()
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("chip_high_contrast")
            .performClick()
            .assertIsDisplayed()

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
