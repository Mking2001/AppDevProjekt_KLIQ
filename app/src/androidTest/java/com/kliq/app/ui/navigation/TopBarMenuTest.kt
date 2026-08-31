package com.kliq.app.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kliq.app.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class TopBarMenuTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun topBar_overflowButtonIsDisplayedOnStartup() {
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .assertIsDisplayed()
    }

    @Test
    fun topBar_menuShowsAllItemsOnClick() {
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Einstellungen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profil bearbeiten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Darstellung").assertIsDisplayed()
        composeTestRule.onNodeWithText("Über Kliq").assertIsDisplayed()
        composeTestRule.onNodeWithText("Abmelden").assertIsDisplayed()
    }

    @Test
    fun topBar_menuClosesAfterItemSelection() {
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Einstellungen").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Über Kliq").assertDoesNotExist()
        composeTestRule.onNodeWithText("Abmelden").assertDoesNotExist()
    }

    @Test
    fun topBar_overflowButtonVisibleOnAllScreens() {
        val tabs = listOf("Home", "Entdecken", "Karte", "Aktivität", "Profil")

        tabs.forEach { tabLabel ->
            navigateToTab(tabLabel)

            composeTestRule
                .onNodeWithContentDescription("Menü öffnen")
                .assertIsDisplayed()
        }
    }

    @Test
    fun topBar_titleUpdatesOnNavigation() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        navigateToTab("Entdecken")
        composeTestRule.onNodeWithText("Entdecken").assertIsDisplayed()

        navigateToTab("Aktivität")
        composeTestRule.onNodeWithText("Aktivität").assertIsDisplayed()

        navigateToTab("Profil")
        composeTestRule.onNodeWithText("Profil").assertIsDisplayed()

        navigateToTab("Home")
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysScreenSpecificActions() {
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Filter")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Nachrichten")
            .assertIsDisplayed()
    }

    @Test
    fun notificationsScreen_displaysMarkAllReadAction() {
        navigateToTab("Aktivität")

        composeTestRule
            .onNodeWithContentDescription("Alle als gelesen markieren")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .assertIsDisplayed()
    }

    @Test
    fun topBar_menuWorksAfterTabNavigation() {
        composeTestRule.waitForIdle()

        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Einstellungen").assertIsDisplayed()

        composeTestRule.onNodeWithText("Darstellung").performClick()
        composeTestRule.waitForIdle()

        navigateToTab("Entdecken")

        composeTestRule
            .onNodeWithContentDescription("Menü öffnen")
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Einstellungen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Abmelden").assertIsDisplayed()
    }

    @Test
    fun topBar_fullMenuFlowAcrossScreens() {
        val screensToTest = listOf(
            "Home" to "Kliq",
            "Entdecken" to "Entdecken",
            "Profil" to "Profil"
        )

        screensToTest.forEach { (tabLabel, expectedTitle) ->

            navigateToTab(tabLabel)

            composeTestRule.onNodeWithText(expectedTitle).assertIsDisplayed()

            composeTestRule
                .onNodeWithContentDescription("Menü öffnen")
                .performClick()
            composeTestRule.waitForIdle()

            composeTestRule.onNodeWithText("Einstellungen").assertIsDisplayed()
            composeTestRule.onNodeWithText("Profil bearbeiten").assertIsDisplayed()
            composeTestRule.onNodeWithText("Darstellung").assertIsDisplayed()
            composeTestRule.onNodeWithText("Über Kliq").assertIsDisplayed()
            composeTestRule.onNodeWithText("Abmelden").assertIsDisplayed()

            composeTestRule.onNodeWithText("Einstellungen").performClick()
            composeTestRule.waitForIdle()
        }
    }

    private fun navigateToTab(tabLabel: String) {
        composeTestRule.onNodeWithContentDescription(tabLabel).performClick()
        composeTestRule.waitForIdle()
    }
}
