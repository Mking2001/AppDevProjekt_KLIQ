package com.kliq.app.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.RegionSearchResult
import com.kliq.app.data.model.SearchFilterType
import com.kliq.app.ui.components.search.ClubSearchBar
import com.kliq.app.ui.components.search.ClubSearchEmptyState
import com.kliq.app.ui.components.search.ClubSearchFilterBadges
import com.kliq.app.ui.components.search.ClubSearchResultList
import com.kliq.app.ui.model.ClubHighContrastItemState
import com.kliq.app.ui.theme.KliqTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClubSearchEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCase1_realtimeSearchAndDebounceInput() {
        var queryInput = ""

        composeTestRule.setContent {
            KliqTheme {
                ClubSearchBar(
                    query = queryInput,
                    onQueryChange = { queryInput = it },
                    onClearClick = { queryInput = "" }
                )
            }
        }

        composeTestRule.onNodeWithText("Clubs, Regionen oder Genres suchen…").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clubs, Regionen oder Genres suchen…").performTextInput("Ber")
        assertEquals("Ber", queryInput)
    }

    @Test
    fun testCase2_regionFilterBadgesSwitch() {
        var selectedFilter = SearchFilterType.ALL

        composeTestRule.setContent {
            KliqTheme {
                ClubSearchFilterBadges(
                    activeFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Nach Region/Stadt").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nach Region/Stadt").performClick()
        assertEquals(SearchFilterType.REGION, selectedFilter)
    }

    @Test
    fun testCase3_emptyStateRenderingForInvalidSearch() {
        composeTestRule.setContent {
            KliqTheme {
                ClubSearchEmptyState(
                    title = "Keine Clubs in dieser Region gefunden",
                    description = "Versuche nach einem anderen Städtenamen oder Genre zu suchen."
                )
            }
        }

        composeTestRule.onNodeWithText("Keine Clubs in dieser Region gefunden").assertIsDisplayed()
        composeTestRule.onNodeWithText("Versuche nach einem anderen Städtenamen oder Genre zu suchen.").assertIsDisplayed()
    }

    @Test
    fun testSearchResultList_rendersRegionsAndClubs() {
        val sampleRegions = listOf(
            RegionSearchResult(regionName = "Berlin", clubCount = 12, isCity = true)
        )
        val sampleClubs = listOf(
            ClubHighContrastItemState(
                id = "c1",
                name = "Watergate Berlin",
                category = "Electro",
                ratingFormatted = "★ 4.8",
                addressFormatted = "Falckensteinstr. 49",
                distanceFormatted = "2.4 km entfernt",
                isOpenNow = true,
                openStatusBadgeText = "OFFEN",
                openStatusBadgeColorHex = "#00E676",
                isFavorite = false,
                geofenceRadiusFormatted = "200m",
                activeEventSummary = null,
                imageUrl = "",
                capacityPercent = 75,
                liveVisitors = 320
            )
        )

        composeTestRule.setContent {
            KliqTheme {
                ClubSearchResultList(
                    clubResults = sampleClubs,
                    regionResults = sampleRegions,
                    onClubClick = {},
                    onRegionClick = {},
                    onToggleFavorite = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Städte & Regionen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Berlin").assertIsDisplayed()
        composeTestRule.onNodeWithText("12 Clubs").assertIsDisplayed()
        composeTestRule.onNodeWithText("Watergate Berlin").assertIsDisplayed()
        composeTestRule.onNodeWithText("★ 4.8").assertIsDisplayed()
    }
}
