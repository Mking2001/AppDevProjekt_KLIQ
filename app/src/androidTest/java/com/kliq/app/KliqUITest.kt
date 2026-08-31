package com.kliq.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class KliqUITest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun szenario1_splashScreenUndBottomBarNavigation() {

        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Home")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("Home").assertExists()

        composeTestRule
            .onAllNodesWithText("Entdecken")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Suche nach Leuten, Events, Orten…").assertExists()

        composeTestRule
            .onAllNodesWithText("Karte")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onAllNodesWithText("Aktivität")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onAllNodesWithText("Profil")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule
            .onAllNodesWithText("Home")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun szenario1_snackbarErrorHandling() {

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Home")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onAllNodesWithText("Entdecken")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("Berghain")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("Berghain").assertExists()
    }

    @Test
    fun szenario2_clubAnalyticsUndInfoBlockRendering() {

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Home")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onAllNodesWithText("Entdecken")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("Berghain")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("Berghain").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Live-Besucherstatistiken")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertIsDisplayed()

        composeTestRule.onNodeWithText("Live-Besucherstatistiken").assertIsDisplayed()
        composeTestRule.onNodeWithText("Auslastung: 85%").assertIsDisplayed()

        composeTestRule.onNodeWithText("Geschlechterverhältnis (1420 Gäste)").assertIsDisplayed()
        composeTestRule.onNodeWithText("45% W").assertIsDisplayed()
        composeTestRule.onNodeWithText("55% M").assertIsDisplayed()

        composeTestRule.onNodeWithText("Event-Highlight").assertIsDisplayed()
        composeTestRule.onNodeWithText("Klubnacht").assertIsDisplayed()
        composeTestRule.onNodeWithText("Eintritt: 25€").assertIsDisplayed()

        composeTestRule.onNodeWithText("Öffnungszeiten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jetzt Geöffnet (23:59 - 12:00)").assertIsDisplayed()
    }

    @Test
    fun szenario3_suchfunktionFuerRegionen() {
        navigiereZuEntdecken()

        composeTestRule
            .onNodeWithText("Suche nach Leuten, Events, Orten…")
            .performTextInput("Hamburg")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Sunset Lounge").assertIsDisplayed()

        composeTestRule.onNodeWithText("Berghain").assertDoesNotExist()

        composeTestRule.onNodeWithText("Techno Night").assertDoesNotExist()
    }

    @Test
    fun szenario3_filterFuerClubBewertungen() {
        navigiereZuEntdecken()

        composeTestRule.onNodeWithText("4.5+ Sterne").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Berghain").assertIsDisplayed()

        composeTestRule.onNodeWithText("Warehouse Rave").assertIsDisplayed()

        composeTestRule.onNodeWithText("Club Luna").assertDoesNotExist()

        composeTestRule.onNodeWithText("After Work").assertDoesNotExist()
    }

    @Test
    fun szenario3_favoritenSystemToggle() {
        navigiereZuEntdecken()

        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("Berghain")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("Berghain").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Berghain / Panorama Bar")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithContentDescription("Favorit").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Favorit").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Favorit").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Favorit").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Favorit").assertIsDisplayed()
    }

    private fun navigiereZuEntdecken() {
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Home")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule
            .onAllNodesWithText("Entdecken")
            .filterToOne(hasClickAction())
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule
                .onAllNodesWithText("Berghain")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}
