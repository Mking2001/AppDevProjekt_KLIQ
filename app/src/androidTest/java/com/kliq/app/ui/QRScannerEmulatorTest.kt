package com.kliq.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.domain.usecase.QRScanResult
import com.kliq.app.ui.screens.qr.QRScannerScreen
import com.kliq.app.ui.screens.qr.QRScannerUiState
import com.kliq.app.ui.screens.qr.QRScannerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QRScannerEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSzenario1_permissionDeniedUi_displaysCustomKliqPermissionRequestScreen() {
        composeTestRule.setContent {
            com.kliq.app.ui.screens.qr.CameraPermissionRequestScreen(
                onRequestPermission = {},
                onNavigateBack = {}
            )
        }

        composeTestRule.onNodeWithText("Kamera-Zugriff erforderlich").assertIsDisplayed()
        composeTestRule.onNodeWithText("Kamera erlauben").assertIsDisplayed()
        composeTestRule.onNodeWithText("Um Kliq QR-Codes für Freundesanfragen und Nutzer-Verifizierungen zu scannen, benötigt die App Zugriff auf deine Kamera.").assertIsDisplayed()
    }

    @Test
    fun testSzenario2_validKliqQrCodeScan_rendersSuccessResultCardWithNavigationAction() {
        var navigatedTargetUserId: String? = null

        val successResult = QRScanResult.Success(
            targetUserId = "user_789",
            username = "Sophie_Vibe",
            message = "Verifizierung erfolgreich! Freundesanfrage gesendet an Sophie_Vibe."
        )

        composeTestRule.setContent {
            com.kliq.app.ui.screens.qr.ScanResultCard(
                result = successResult,
                onRescan = {},
                onNavigateToUser = { userId ->
                    navigatedTargetUserId = userId
                }
            )
        }

        composeTestRule.onNodeWithText("Verifiziert!").assertIsDisplayed()
        composeTestRule.onNodeWithText("Verifizierung erfolgreich! Freundesanfrage gesendet an Sophie_Vibe.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Zum Profil").assertIsDisplayed()

        composeTestRule.onNodeWithText("Zum Profil").performClick()
        assertEquals("user_789", navigatedTargetUserId)
    }

    @Test
    fun testSzenario3_invalidQrCodeScan_rendersErrorCardWithRetryAction() {
        var isRescanCalled = false

        val invalidResult = QRScanResult.InvalidCode(
            message = "Ungültiger oder nicht erkannter Kliq QR-Code."
        )

        composeTestRule.setContent {
            com.kliq.app.ui.screens.qr.ScanResultCard(
                result = invalidResult,
                onRescan = { isRescanCalled = true },
                onNavigateToUser = {}
            )
        }

        composeTestRule.onNodeWithText("Ungültiger Code").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ungültiger oder nicht erkannter Kliq QR-Code.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Erneut versuchen").assertIsDisplayed()

        composeTestRule.onNodeWithText("Erneut versuchen").performClick()
        assertTrue(isRescanCalled)
    }
}
