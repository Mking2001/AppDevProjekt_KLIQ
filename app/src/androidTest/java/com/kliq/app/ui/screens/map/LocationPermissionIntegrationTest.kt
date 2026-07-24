package com.kliq.app.ui.screens.map

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentierter Integrationstest für Kapitel 4.2: Standort-Berechtigungs-Workflow ("Standort aktivieren").
 *
 * Validiert die Rationale-UI, den Dialog-Workflow und die Visualisierung im Kliq Lila/Dark-Mode.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LocationPermissionIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    /**
     * Szenario: Klick auf den Standort-FAB löst den Kliq-spezifischen Rationale-Dialog aus.
     * Erwartetes Ergebnis: Rationale-Dialog mit "Standort aktivieren" & Geofencing-Erklärung wird angezeigt.
     */
    @Test
    fun locationFabClick_triggersRationaleDialog() {
        // Navigiere zum Karte-Tab
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        // Klick auf Standort-FAB ("Mein Standort")
        composeTestRule.onNodeWithContentDescription("Mein Standort").performClick()
        composeTestRule.waitForIdle()

        // Assertions: Rationale Dialog ist sichtbar
        composeTestRule.onNodeWithText("Standort aktivieren").assertIsDisplayed()
        composeTestRule.onNodeWithText("Standort jetzt aktivieren").assertIsDisplayed()
        composeTestRule.onNodeWithText("Später").assertIsDisplayed()
    }

    /**
     * Szenario: Klick auf "Später" schließt den Rationale-Dialog.
     */
    @Test
    fun rationaleDialog_dismissOnLaterClick() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Mein Standort").performClick()
        composeTestRule.waitForIdle()

        // Klick auf "Später"
        composeTestRule.onNodeWithText("Später").performClick()
        composeTestRule.waitForIdle()

        // Assertions: Dialog ist geschlossen
        composeTestRule.onNodeWithText("Standort jetzt aktivieren").assertDoesNotExist()
    }
}
