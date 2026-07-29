package com.kliq.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.ChatListItem
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.LastMessage
import com.kliq.app.data.util.CityChatLocationMapper
import com.kliq.app.ui.components.CityChatHeaderBanner
import com.kliq.app.ui.components.CityChatSwitcherSheet
import com.kliq.app.ui.theme.KliqTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CityPublicChatEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCityChatHeaderBanner_rendersActiveCityAndDistance() {
        var isSwitchClicked = false
        val item = ChatListItem(
            id = "pub_1",
            title = "Berlin - Tonight",
            cityRegion = "Berlin",
            lastMessage = LastMessage(text = "Party in Watergate!"),
            avatarInitial = "B",
            chatType = ChatType.PUBLIC_CITY,
            distanceKm = 2.4,
            onlineMembersCount = 248,
            isGpsAssigned = true
        )

        composeTestRule.setContent {
            KliqTheme {
                CityChatHeaderBanner(
                    activeCityChat = item,
                    onSwitchCityClick = { isSwitchClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Berlin - Tonight").assertIsDisplayed()
        composeTestRule.onNodeWithText("⚡ 248 Feiernde online • 2.4 km entfernt").assertIsDisplayed()

        composeTestRule.onNodeWithText("Wechseln").performClick()
        assertTrue(isSwitchClicked)
    }

    @Test
    fun testCityChatSwitcherSheet_listsSupportedCitiesAndSelection() {
        var selectedCityTitle: String? = null

        composeTestRule.setContent {
            KliqTheme {
                CityChatSwitcherSheet(
                    supportedCities = CityChatLocationMapper.SUPPORTED_CITIES,
                    onCitySelected = { selectedCityTitle = it.title },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Öffentlichen Stadt-Chat wählen").assertIsDisplayed()
        composeTestRule.onNodeWithText("München - Party Radar").assertIsDisplayed()

        composeTestRule.onNodeWithText("München - Party Radar").performClick()
        assertEquals("München - Party Radar", selectedCityTitle)
    }
}
