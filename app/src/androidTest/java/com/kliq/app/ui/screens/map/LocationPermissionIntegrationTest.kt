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

    @Test
    fun szenario1_ersterAppStartZeigtCustomRationaleDialog() {

        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Mein Standort").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Standort aktivieren").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Kliq benötigt deinen Standort, um nahegelegene Clubs und Events anzuzeigen sowie für die automatische Standorts-Verifizierung bei Reviews und Geofencing im Nightlife."
        ).assertIsDisplayed()
        composeTestRule.onNodeWithText("Standort jetzt aktivieren").assertIsDisplayed()

        composeTestRule.onNodeWithText("Standort jetzt aktivieren").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Standort jetzt aktivieren").assertDoesNotExist()
    }

    @Test
    fun szenario2_berechtigungErteiltRoutetZurMapAnsicht() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.activity.runOnUiThread {
            permissionViewModel.onPermissionResult(isGranted = true, shouldShowRationale = false)
        }
        composeTestRule.waitForIdle()

        val state = permissionViewModel.uiState.value
        assertEquals(LocationPermissionState.Granted, state.permissionState)
        assertFalse("Rationale-Dialog darf nach Erteilung nicht sichtbar sein", state.showRationaleDialog)
        assertFalse("Settings-Dialog darf nach Erteilung nicht sichtbar sein", state.showPermanentlyDeniedDialog)

        composeTestRule.onNodeWithContentDescription("Mein Standort").assertIsDisplayed()
    }

    @Test
    fun szenario3_dauerhaftAbgelehntZeigtSettingsDeepLinkButton() {
        composeTestRule.onNodeWithContentDescription("Karte").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.activity.runOnUiThread {
            permissionViewModel.onPermissionResult(isGranted = false, shouldShowRationale = false)
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Standort-Zugriff erforderlich").assertIsDisplayed()
        composeTestRule.onNodeWithText("In Einstellungen öffnen").assertIsDisplayed()

        composeTestRule.onNodeWithText("In Einstellungen öffnen").performClick()
        composeTestRule.waitForIdle()

        Intents.intended(
            hasAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        )
    }
}
