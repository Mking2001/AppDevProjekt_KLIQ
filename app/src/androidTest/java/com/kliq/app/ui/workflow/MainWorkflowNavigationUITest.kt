package com.kliq.app.ui.workflow

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.NavigationState
import com.kliq.app.ui.navigation.KliqBottomBar
import com.kliq.app.ui.navigation.NavigationRoute
import com.kliq.app.ui.screens.club.ClubDetailContent
import com.kliq.app.ui.screens.club.ClubDetailUiState
import com.kliq.app.ui.theme.KliqTheme
import com.kliq.app.mock.FakeBackendStateModule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainWorkflowNavigationUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testBottomBarNavigationHostTabSwitching() {
        var selectedRoute by mutableStateOf(NavigationRoute.Home.route)

        composeTestRule.setContent {
            KliqTheme {
                KliqBottomBar(
                    currentRoute = selectedRoute,
                    notificationBadgeCount = 3,
                    onTabSelected = { selectedRoute = it }
                )
            }
        }

        composeTestRule.onAllNodesWithText("Home").filterToOne(hasClickAction()).assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Entdecken").filterToOne(hasClickAction()).assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Karte").filterToOne(hasClickAction()).assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Aktivität").filterToOne(hasClickAction()).assertIsDisplayed()
        composeTestRule.onAllNodesWithText("Profil").filterToOne(hasClickAction()).assertIsDisplayed()

        assertEquals(NavigationRoute.Home.route, selectedRoute)

        composeTestRule.onAllNodesWithText("Entdecken").filterToOne(hasClickAction()).performClick()
        composeTestRule.waitForIdle()
        assertEquals(NavigationRoute.Explore.route, selectedRoute)

        composeTestRule.onAllNodesWithText("Karte").filterToOne(hasClickAction()).performClick()
        composeTestRule.waitForIdle()
        assertEquals(NavigationRoute.Map.route, selectedRoute)

        composeTestRule.onAllNodesWithText("Aktivität").filterToOne(hasClickAction()).performClick()
        composeTestRule.waitForIdle()
        assertEquals(NavigationRoute.Notifications.route, selectedRoute)

        composeTestRule.onAllNodesWithText("Profil").filterToOne(hasClickAction()).performClick()
        composeTestRule.waitForIdle()
        assertEquals(NavigationRoute.Profile.route, selectedRoute)

        composeTestRule.onAllNodesWithText("Home").filterToOne(hasClickAction()).performClick()
        composeTestRule.waitForIdle()
        assertEquals(NavigationRoute.Home.route, selectedRoute)
    }

    @Test
    fun testExploreToClubDetailNavigation() {
        val testClub = FakeBackendStateModule.mockClubList.first()
        val testEvents = FakeBackendStateModule.mockEvents

        composeTestRule.setContent {
            KliqTheme {
                ClubDetailContent(
                    uiState = ClubDetailUiState(
                        club = testClub,
                        events = testEvents,
                        isLoading = false
                    ),
                    onNavigateBack = {},
                    onFavoriteToggle = {},
                    onShareClick = {},
                    onEventClick = {},
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertIsDisplayed()

        composeTestRule.onNodeWithText("Live-Besucherstatistiken").assertIsDisplayed()
        composeTestRule.onNodeWithText("Auslastung: 85%").assertIsDisplayed()

        composeTestRule.onNodeWithText("Geschlechterverhältnis (1275 Gäste)").assertIsDisplayed()
        composeTestRule.onNodeWithText("45% W").assertIsDisplayed()
        composeTestRule.onNodeWithText("55% M").assertIsDisplayed()

        composeTestRule.onNodeWithText("Event-Highlight").assertIsDisplayed()
        composeTestRule.onNodeWithText("Klubnacht Weekend special").assertIsDisplayed()

        composeTestRule.onNodeWithText("Öffnungszeiten").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jetzt Geöffnet (23:59 - 12:00)").assertIsDisplayed()
    }

    @Test
    fun testProfileScreenNavigationAndActions() {
        var isQrScannerOpened by mutableStateOf(false)

        composeTestRule.setContent {
            KliqTheme {

                val user = FakeBackendStateModule.mockTestUser
                KliqBottomBar(
                    currentRoute = NavigationRoute.Profile.route,
                    notificationBadgeCount = 0,
                    onTabSelected = {
                        if (it == "qr_scanner") isQrScannerOpened = true
                    }
                )
            }
        }

        composeTestRule.onAllNodesWithText("Profil").filterToOne(hasClickAction()).assertIsDisplayed()
    }
}
