package com.kliq.app.ui.screens.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.MainActivity
import com.kliq.app.data.local.security.SessionStorage
import com.kliq.app.data.repository.SessionRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Instrumentierter Integrationstest für Kapitel 3.7: Persistente Session-Verwaltung (Auto-Login).
 *
 * Validiert die drei Kern-Szenarien im Emulator:
 *   1. Cold Start ohne bestehende Session: Token ist leer -> Weiterleitung zur Telefonnummer-Login UI.
 *   2. Persistenz nach erfolgreichem Login: Token im EncryptedSessionStorage hinterlegt -> Prozess-Kill & Neustart -> Direktes Routing zum Haupt-Navigations-Host (Bottom Bar / Home).
 *   3. Session-Invalidierung / Logout: Token aus Speicher gelöscht -> Neustart -> Auto-Login schlägt fehl -> Routing zur Phone-Login UI.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AutoLoginSessionIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var sessionStorage: SessionStorage

    @Inject
    lateinit var sessionRepository: SessionRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    /**
     * Szenario 1: Cold Start ohne bestehende Session.
     * Erwartetes Ergebnis: App startet -> Encrypted Token ist leer -> Auto-Login schlägt fehl -> Routing zur Telefonnummer-Login UI.
     */
    @Test
    fun szenario1_coldStartOhneSessionRoutetZuPhoneLoginUI() {
        // 1. Session-Speicher explizit leeren
        sessionStorage.clearSession()

        // 2. Assertions auf Verschlüsselungsspeicher-Zustand
        assertFalse("Session darf ohne Login nicht aktiv sein", sessionStorage.isSessionActive())
        assertNull("Auth-Token muss im Speicher null sein", sessionStorage.getAuthToken())
        assertNull("User-ID muss im Speicher null sein", sessionStorage.getUserId())

        // 3. Warten auf Splash-Screen Evaluierung und Routing
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Starte dein Nightlife-Erlebnis")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // 4. Assertions auf Ziel-Screen (Phone-Login UI)
        composeTestRule.onNodeWithText("Starte dein Nightlife-Erlebnis").assertIsDisplayed()
        composeTestRule.onNodeWithText("Verschlüsselt & DSGVO-konform").assertIsDisplayed()
    }

    /**
     * Szenario 2: Persistenz nach erfolgreichem Login und Prozess-Re-Initialization (Auto-Login).
     * Erwartetes Ergebnis: Valider Token ist hinterlegt -> Auto-Login greift -> Direktes Routing zum Haupt-Navigations-Host (Home-Tab mit Bottom Bar).
     */
    @Test
    fun szenario2_persistenterAutoLoginRoutetDirektZumHauptNavigationsHost() {
        // 1. Erfolgreichen Login simulieren und verschlüsselt speichern
        val testToken = "secure_encrypted_jwt_token_abc123"
        val testUserId = "usr_kliq_member_888"
        runBlocking {
            sessionRepository.saveSession(testToken, testUserId)
        }

        // 2. Assertions auf Verschlüsselungsspeicher-Zustand
        assertTrue("Session muss nach Login als aktiv markiert sein", sessionStorage.isSessionActive())
        assertEquals("Gespeichertes Token muss exakt übereinstimmen", testToken, sessionStorage.getAuthToken())
        assertEquals("Gespeicherte User-ID muss exakt übereinstimmen", testUserId, sessionStorage.getUserId())

        // 3. Prozess-Neustart simulieren: Activity neu herstellen / Auto-Login auslösen
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        // 4. Warten bis Home-Screen des Haupt-Navigations-Hosts geladen ist
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Home")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // 5. Assertion: Nutzer landet direkt im Haupt-Navigations-Host (Bottom Bar sichtbar)
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entdecken").assertIsDisplayed()
    }

    /**
     * Szenario 3: Session-Invalidierung / Logout.
     * Erwartetes Ergebnis: Logout -> Token gelöscht -> Neustart -> Auto-Login schlägt fehl -> Routing zur Login-UI.
     */
    @Test
    fun szenario3_logoutLoeschtSessionUndRoutetZurLoginUI() {
        // 1. Initiale Session setzen
        runBlocking {
            sessionRepository.saveSession("token_to_revoke", "usr_logout_test")
        }
        assertTrue(sessionStorage.isSessionActive())

        // 2. Logout durchführen (Session-Invalidierung)
        runBlocking {
            sessionRepository.clearSession()
        }

        // 3. Assertions auf Verschlüsselungsspeicher
        assertFalse("Session muss nach Logout inaktiv sein", sessionStorage.isSessionActive())
        assertNull("Auth-Token muss nach Logout gelöscht sein", sessionStorage.getAuthToken())
        assertNull("User-ID muss nach Logout gelöscht sein", sessionStorage.getUserId())

        // 4. Activity neu herstellen (App-Neustart simulieren)
        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        // 5. Warten bis Routing zur Phone Login UI abgeschlossen ist
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Starte dein Nightlife-Erlebnis")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // 6. Assertions: Telefon-Login UI ist sichtbar
        composeTestRule.onNodeWithText("Starte dein Nightlife-Erlebnis").assertIsDisplayed()
    }
}
