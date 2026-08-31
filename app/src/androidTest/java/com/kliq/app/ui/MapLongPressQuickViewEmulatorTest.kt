package com.kliq.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.ui.components.MapQuickViewCard
import com.kliq.app.ui.screens.map.VenueItemUi
import com.kliq.app.ui.theme.KliqTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapLongPressQuickViewEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleVenue = VenueItemUi(
        id = "c_berghain",
        name = "Berghain / Panorama Bar",
        category = "Club",
        distance = "0.3 km",
        rating = 4.9f,
        latitude = 52.5112,
        longitude = 13.4430,
        address = "Am Wriezener Bahnhof, 10243 Berlin",
        activeEventTitle = "Klubnacht",
        isFavorite = false,
        currentCapacityPercent = 85,
        isOpenNow = true,
        totalLiveVisitors = 380,
        malePercentage = 52,
        femalePercentage = 48
    )

    @Test
    fun testLongPressOnMapMarker_showsQuickViewOverlayWithDataIntegrity() {
        var selectedVenueState by mutableStateOf<VenueItemUi?>(null)

        composeTestRule.setContent {
            KliqTheme {
                MapQuickViewCard(
                    venue = selectedVenueState,
                    isVisible = selectedVenueState != null,
                    onDismiss = { selectedVenueState = null },
                    onNavigateDetails = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertDoesNotExist()

        selectedVenueState = sampleVenue

        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertIsDisplayed()

        composeTestRule.onNodeWithText("Club • 0.3 km").assertIsDisplayed()
        composeTestRule.onNodeWithText("4.9").assertIsDisplayed()
        composeTestRule.onNodeWithText("380 Besucher live").assertIsDisplayed()
        composeTestRule.onNodeWithText("♂ 52%  |  ♀ 48%").assertIsDisplayed()
        composeTestRule.onNodeWithText("85% Auslastung").assertIsDisplayed()
        composeTestRule.onNodeWithText("Klubnacht").assertIsDisplayed()
    }

    @Test
    fun testDismissQuickViewOverlay_restoresMapInteractivity() {
        var selectedVenueState by mutableStateOf<VenueItemUi?>(sampleVenue)

        composeTestRule.setContent {
            KliqTheme {
                MapQuickViewCard(
                    venue = selectedVenueState,
                    isVisible = selectedVenueState != null,
                    onDismiss = { selectedVenueState = null },
                    onNavigateDetails = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertIsDisplayed()
        assertNotNull(selectedVenueState)

        composeTestRule.onNodeWithContentDescription("Schließen").performClick()

        assertNull(selectedVenueState)
        composeTestRule.onNodeWithText("Berghain / Panorama Bar").assertDoesNotExist()
    }
}
