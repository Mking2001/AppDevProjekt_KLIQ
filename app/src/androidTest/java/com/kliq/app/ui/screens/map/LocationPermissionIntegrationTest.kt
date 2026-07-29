package com.kliq.app.ui.screens.map

import android.content.Intent
import android.provider.Settings
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.MainActivity
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.viewmodel.PermissionViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Instrumentierter UI- & Integrationstest für Kapitel 4.2: Standort-Berechtigungs-Workflow ("Standort aktivieren").
 *
 * Validiert 3 Kern-Szenarien im Emulator:
 *   1. Erster App-Start (Berechtigung ungeklärt): Kliq-eigener Rationale-Dialog im Lila-Design.
 *   2. Berechtigung erteilt: Reaktiver Statuswechsel im PermissionViewModel und Weiterleitung zur Map-Ansicht.
 *   3. Berechtigung dauerhaft abgelehnt: Deep-Link Button löst Intent zu Android System-Einstellungen aus.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LocationPermissionIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var permissionViewModel: PermissionViewModel

    @Before
    fun setup() {
        hiltRule.inject()
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    /**
     * Szenario 1: Erster App-Start (Berechtigung ungeklärt).
     * Nutzer navigiert zur Karte -> Klick auf Location FAB -> Rationale-Dialog erscheint im Lila-Design.
     */
    @Test
    fun szenario1_ersterAppStartZeigtCustomRationaleDialog() {
        // 1. Navigiere zur Karte
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        // 2. Klick auf Standort-FAB ("Mein Standort")
        composeTestRule.onNodeWithContentDescription("Mein Standort").performClick()
        composeTestRule.waitForIdle()

        // 3. Assertions: Kliq-eigener Erklärungs-Dialog (Rationale) ist sichtbar
        composeTestRule.onNodeWithText("Standort aktivieren").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Kliq benötigt deinen Standort, um nahegelegene Clubs und Events anzuzeigen sowie für die automatische Standorts-Verifizierung bei Reviews und Geofencing im Nightlife."
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText("Standort jetzt aktivieren").assertIsDisplayed()

        // 4. Klick auf "Standort jetzt aktivieren" schließt den Rationale-Dialog und leitet zur Systemanfrage über
        composeTestRule.onNodeWithText("Standort jetzt aktivieren").performClick()
        composeTestRule.waitForIdle()

        // Rationale-Dialog wurde geschlossen
        composeTestRule.onNodeWithText("Standort jetzt aktivieren").assertDoesNotExist()
    }

    /**
     * Szenario 2: Berechtigung erteilt.
     * PermissionViewModel registriert den Statuswechsel auf Granted -> Weiterleitung zur Map-Ansicht.
     */
    @Test
    fun szenario2_berechtigungErteiltRoutetZurMapAnsicht() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        // Simulierte Berechtigungserteilung im ViewModel
        composeTestRule.activity.runOnUiThread {
            permissionViewModel.onPermissionResult(isGranted = true, shouldShowRationale = false)
        }
        composeTestRule.waitForIdle()

        // Assertions auf ViewModel-State & UI
        val state = permissionViewModel.uiState.value
        assertEquals(LocationPermissionState.Granted, state.permissionState)
        assertFalse("Rationale-Dialog darf nach Erteilung nicht sichtbar sein", state.showRationaleDialog)
        assertFalse("Settings-Dialog darf nach Erteilung nicht sichtbar sein", state.showPermanentlyDeniedDialog)

        // Karte und Venue-Overlays sind in der UI sichtbar
        composeTestRule.onNodeWithContentDescription("Mein Standort").assertIsDisplayed()
    }

    /**
     * Szenario 3: Berechtigung dauerhaft abgelehnt.
     * Zeigt Custom-Dialog mit Deep-Link Button zur System-Einstellung.
     */
    @Test
    fun szenario3_dauerhaftAbgelehntZeigtSettingsDeepLinkButton() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        // Simulierte dauerhafte Ablehnung im ViewModel
        composeTestRule.activity.runOnUiThread {
            permissionViewModel.onPermissionResult(isGranted = false, shouldShowRationale = false)
        }
        composeTestRule.waitForIdle()

        // Assertions: Permanently Denied Dialog & Button sind sichtbar
        composeTestRule.onNodeWithText("Standort-Zugriff erforderlich").assertIsDisplayed()
        composeTestRule.onNodeWithText("In Einstellungen öffnen").assertIsDisplayed()

        // Klick auf "In Einstellungen öffnen"
        composeTestRule.onNodeWithText("In Einstellungen öffnen").performClick()
        composeTestRule.waitForIdle()

        // Assertion: Intent zu Android System-Einstellungen (ACTION_APPLICATION_DETAILS_SETTINGS) wurde ausgelöst
        Intents.intended(
            hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        )
    }
}
