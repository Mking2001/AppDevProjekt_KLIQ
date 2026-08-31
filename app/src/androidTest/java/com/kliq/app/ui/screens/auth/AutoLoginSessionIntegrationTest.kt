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

    @Test
    fun szenario1_coldStartOhneSessionRoutetZuPhoneLoginUI() {

        sessionStorage.clearSession()

        assertFalse("Session darf ohne Login nicht aktiv sein", sessionStorage.isSessionActive())
        assertNull("Auth-Token muss im Speicher null sein", sessionStorage.getAuthToken())
        assertNull("User-ID muss im Speicher null sein", sessionStorage.getUserId())

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Starte dein Nightlife-Erlebnis")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Starte dein Nightlife-Erlebnis").assertIsDisplayed()
        composeTestRule.onNodeWithText("Verschlüsselt & DSGVO-konform").assertIsDisplayed()
    }

    @Test
    fun szenario2_persistenterAutoLoginRoutetDirektZumHauptNavigationsHost() {

        val testToken = "secure_encrypted_jwt_token_abc123"
        val testUserId = "usr_kliq_member_888"
        runBlocking {
            sessionRepository.saveSession(testToken, testUserId)
        }

        assertTrue("Session muss nach Login als aktiv markiert sein", sessionStorage.isSessionActive())
        assertEquals("Gespeichertes Token muss exakt übereinstimmen", testToken, sessionStorage.getAuthToken())
        assertEquals("Gespeicherte User-ID muss exakt übereinstimmen", testUserId, sessionStorage.getUserId())

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Home")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entdecken").assertIsDisplayed()
    }

    @Test
    fun szenario3_logoutLoeschtSessionUndRoutetZurLoginUI() {

        runBlocking {
            sessionRepository.saveSession("token_to_revoke", "usr_logout_test")
        }
        assertTrue(sessionStorage.isSessionActive())

        runBlocking {
            sessionRepository.clearSession()
        }

        assertFalse("Session muss nach Logout inaktiv sein", sessionStorage.isSessionActive())
        assertNull("Auth-Token muss nach Logout gelöscht sein", sessionStorage.getAuthToken())
        assertNull("User-ID muss nach Logout gelöscht sein", sessionStorage.getUserId())

        composeTestRule.activityRule.scenario.recreate()
        composeTestRule.waitForIdle()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Starte dein Nightlife-Erlebnis")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Starte dein Nightlife-Erlebnis").assertIsDisplayed()
    }
}
