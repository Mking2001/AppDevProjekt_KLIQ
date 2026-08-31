package com.kliq.app.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.ClubContactInfo
import com.kliq.app.data.model.LiveOpeningStatus
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.ui.components.ClubExternalInfoBlock
import com.kliq.app.ui.theme.KliqTheme
import com.kliq.app.viewmodel.ClubExternalInfoUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClubExternalInfoEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testScenario1_overMidnightOpeningHoursAndLiveStatusOpen() {
        val mockState = ClubExternalInfoUiState(
            isLoading = false,
            clubId = "club_berghain",
            clubName = "Berghain / Panorama Bar",
            address = "Am Wriezener Bahnhof, 10243 Berlin",
            websiteUrl = "https://berghain.berlin",
            phoneNumber = "+49 30 293600",
            contactEmail = "info@berghain.de",
            operatingHours = OperatingHours(
                isOpenNow = true,
                todayHours = "23:00 - 06:00",
                weeklySchedule = mapOf("Freitag" to "23:00 - 06:00", "Samstag" to "23:59 - Open End")
            ),
            liveStatus = LiveOpeningStatus.OPEN_NOW,
            contactInfo = ClubContactInfo(
                phoneNumber = "+49 30 293600",
                email = "info@berghain.de",
                websiteUrl = "https://berghain.berlin"
            )
        )

        composeTestRule.setContent {
            KliqTheme {
                ClubExternalInfoBlock(state = mockState)
            }
        }

        composeTestRule.onNodeWithText("Club Information").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jetzt geöffnet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Am Wriezener Bahnhof, 10243 Berlin").assertIsDisplayed()
        composeTestRule.onNodeWithText("Website").assertIsDisplayed()
        composeTestRule.onNodeWithText("Anrufen").assertIsDisplayed()
    }

    @Test
    fun testScenario2_closedClubWithoutWebsiteAndMissingData() {
        val mockClosedState = ClubExternalInfoUiState(
            isLoading = false,
            clubId = "club_closed",
            clubName = "Schattenwerk Club",
            address = "Musterstraße 42, 10115 Berlin",
            websiteUrl = null,
            phoneNumber = null,
            contactEmail = null,
            operatingHours = OperatingHours(
                isOpenNow = false,
                todayHours = "Geschlossen",
                weeklySchedule = mapOf("Montag" to "Geschlossen")
            ),
            liveStatus = LiveOpeningStatus.CLOSED,
            contactInfo = ClubContactInfo()
        )

        composeTestRule.setContent {
            KliqTheme {
                ClubExternalInfoBlock(state = mockClosedState)
            }
        }

        composeTestRule.onNodeWithText("Club Information").assertIsDisplayed()
        composeTestRule.onNodeWithText("Geschlossen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Musterstraße 42, 10115 Berlin").assertIsDisplayed()
    }

    @Test
    fun testScenario3_expandableScheduleInteraction() {
        val mockState = ClubExternalInfoUiState(
            isLoading = false,
            clubId = "club_watergate",
            clubName = "Watergate",
            address = "Falckensteinstraße 49, 10997 Berlin",
            websiteUrl = "https://water-gate.de",
            phoneNumber = "+49 30 6128030",
            operatingHours = OperatingHours(
                isOpenNow = true,
                todayHours = "23:00 - 05:00",
                weeklySchedule = mapOf(
                    "Montag" to "Geschlossen",
                    "Freitag" to "23:00 - 05:00",
                    "Samstag" to "23:00 - 08:00"
                )
            ),
            liveStatus = LiveOpeningStatus.OPEN_NOW
        )

        composeTestRule.setContent {
            KliqTheme {
                ClubExternalInfoBlock(state = mockState)
            }
        }

        composeTestRule.onNodeWithText("Website").performClick()
        composeTestRule.onNodeWithText("Anrufen").performClick()
    }
}
