package com.kliq.app.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
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
class ScreenScaffoldingTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreen_displaysScaffoldingElements() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        composeTestRule.onNodeWithText("Anna M.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vor 15 Min.").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Neuer Beitrag").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Filter").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Nachrichten").assertIsDisplayed()
    }

    @Test
    fun homeScreen_displaysStoryRow() {
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Anna").assertIsDisplayed()
        composeTestRule.onNodeWithText("Max").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lisa").assertIsDisplayed()
    }

    @Test
    fun exploreScreen_displaysScaffoldingElements() {
        navigateToTab("Entdecken")

        composeTestRule.onNodeWithText("Entdecken").assertIsDisplayed()

        composeTestRule.onNodeWithText("Suche nach Leuten, Events, Orten…").assertIsDisplayed()

        composeTestRule.onNodeWithText("Trending").assertIsDisplayed()
        composeTestRule.onNodeWithText("Events").assertIsDisplayed()

        composeTestRule.onNodeWithText("Techno Night").assertIsDisplayed()
    }

    @Test
    fun exploreScreen_categoryChipsAreClickable() {
        navigateToTab("Entdecken")

        composeTestRule.onNodeWithText("Trending").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Trending").assertIsDisplayed()
    }

    @Test
    fun mapScreen_displaysScaffoldingElements() {
        navigateToTab("Karte")

        composeTestRule.onNodeWithContentDescription("Standort").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Mein Standort").assertIsDisplayed()

        composeTestRule.onNodeWithText("Clubs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bars").assertIsDisplayed()

        composeTestRule.onNodeWithText("In deiner Nähe").assertIsDisplayed()

        composeTestRule.onNodeWithText("Club Luna").assertIsDisplayed()
    }

    @Test
    fun notificationsScreen_displaysScaffoldingElements() {
        navigateToTab("Aktivität")

        composeTestRule.onNodeWithText("Aktivität").assertIsDisplayed()

        composeTestRule.onNodeWithText("Alle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Likes").assertIsDisplayed()

        composeTestRule.onNodeWithText("Anna M. hat deinen Beitrag geliked").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Alle als gelesen markieren").assertIsDisplayed()
    }

    @Test
    fun notificationsScreen_tabFilterIsInteractive() {
        navigateToTab("Aktivität")

        composeTestRule.onNodeWithText("Follows").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Lisa W. folgt dir jetzt").assertIsDisplayed()
    }

    @Test
    fun notificationsScreen_markAllReadWorks() {
        navigateToTab("Aktivität")

        composeTestRule.onNodeWithContentDescription("Alle als gelesen markieren").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Alle als gelesen markieren")
            .assertDoesNotExist()
    }

    @Test
    fun profileScreen_displaysScaffoldingElements() {
        navigateToTab("Profil")

        composeTestRule.onNodeWithText("Profil").assertIsDisplayed()

        composeTestRule.onNodeWithText("Max Mustermann").assertIsDisplayed()
        composeTestRule.onNodeWithText("@maxmuster").assertIsDisplayed()

        composeTestRule.onNodeWithText("Beiträge").assertIsDisplayed()
        composeTestRule.onNodeWithText("Follower").assertIsDisplayed()
        composeTestRule.onNodeWithText("Following").assertIsDisplayed()
        composeTestRule.onNodeWithText("127").assertIsDisplayed()

        composeTestRule.onNodeWithText("Profil bearbeiten").assertIsDisplayed()
    }

    @Test
    fun profileScreen_tabNavigationWorks() {
        navigateToTab("Profil")

        composeTestRule.onNodeWithText("Events").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Techno Night").assertIsDisplayed()

        composeTestRule.onNodeWithText("Über mich").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Interessen").assertIsDisplayed()
    }

    @Test
    fun fullNavigationLoop_allScreensReachable() {

        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        navigateToTab("Entdecken")
        composeTestRule.onNodeWithText("Suche nach Leuten, Events, Orten…").assertIsDisplayed()

        navigateToTab("Karte")
        composeTestRule.onNodeWithText("In deiner Nähe").assertIsDisplayed()

        navigateToTab("Aktivität")
        composeTestRule.onNodeWithText("Alle").assertIsDisplayed()

        navigateToTab("Profil")
        composeTestRule.onNodeWithText("Max Mustermann").assertIsDisplayed()

        navigateToTab("Home")
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
        composeTestRule.onNodeWithText("Anna M.").assertIsDisplayed()
    }

    private fun navigateToTab(tabLabel: String) {
        composeTestRule.onNodeWithContentDescription(tabLabel).performClick()
        composeTestRule.waitForIdle()
    }
}
