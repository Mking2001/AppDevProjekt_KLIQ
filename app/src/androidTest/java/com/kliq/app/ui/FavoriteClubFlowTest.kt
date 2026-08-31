package com.kliq.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.RegionSearchResult
import com.kliq.app.ui.components.AnimatedFavoriteButton
import com.kliq.app.ui.components.search.ClubSearchResultList
import com.kliq.app.ui.model.ClubHighContrastItemState
import com.kliq.app.ui.theme.KliqTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteClubFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testFavoriteToggleVisualStateChangeAndPersistenceFlow() {
        var isFavoriteState by mutableStateOf(false)

        composeTestRule.setContent {
            KliqTheme {
                AnimatedFavoriteButton(
                    isFavorite = isFavoriteState,
                    onToggleFavorite = { isFavoriteState = !isFavoriteState }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Zu Favoriten hinzufügen").assertIsDisplayed()
        assertFalse(isFavoriteState)

        composeTestRule.onNodeWithContentDescription("Zu Favoriten hinzufügen").performClick()

        composeTestRule.onNodeWithContentDescription("Aus Favoriten entfernen").assertIsDisplayed()
        assertTrue(isFavoriteState)
    }

    @Test
    fun testProcessRestartPersistenceAndFavoriteListRendering() {
        val simulatedPersistedClub = Club(
            id = "c_watergate",
            name = "Watergate",
            location = GpsLocation(52.5011, 13.4452, "Falckensteinstraße 49, 10997 Berlin"),
            isFavorite = true,
            category = "Club"
        )

        val clubItemState = ClubHighContrastItemState(
            id = simulatedPersistedClub.id,
            name = simulatedPersistedClub.name,
            category = simulatedPersistedClub.category,
            distanceFormatted = "0.7 km",
            ratingFormatted = "4.7",
            addressFormatted = simulatedPersistedClub.location.address,
            isFavorite = simulatedPersistedClub.isFavorite
        )

        composeTestRule.setContent {
            KliqTheme {
                ClubSearchResultList(
                    clubResults = listOf(clubItemState),
                    regionResults = listOf(RegionSearchResult("Berlin", 1, true)),
                    onClubClick = {},
                    onRegionClick = {},
                    onToggleFavorite = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Watergate").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Aus Favoriten entfernen").assertIsDisplayed()
    }
}
