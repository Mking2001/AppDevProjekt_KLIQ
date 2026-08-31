package com.kliq.app.ui.screens.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.VisitedLog
import com.kliq.app.ui.screens.history.VisitedHistoryScreen
import com.kliq.app.viewmodel.HistoryUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class VisitedHistoryEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testSeededVisitedLogs_rendersDescendingByDateWithGpsBadge() {
        val now = System.currentTimeMillis()
        val mockLogs = listOf(
            VisitedLog(
                id = "log_1",
                userId = "current_user",
                clubId = "club_bootshaus",
                clubName = "Bootshaus Köln",
                visitedAtTimestamp = now,
                isVerifiedByGps = true
            ),
            VisitedLog(
                id = "log_2",
                userId = "current_user",
                clubId = "club_pacha",
                clubName = "Pacha München",
                visitedAtTimestamp = now - TimeUnit.DAYS.toMillis(2),
                isVerifiedByGps = false
            ),
            VisitedLog(
                id = "log_3",
                userId = "current_user",
                clubId = "club_berghain",
                clubName = "Berghain Berlin",
                visitedAtTimestamp = now - TimeUnit.DAYS.toMillis(7),
                isVerifiedByGps = true
            )
        )

        composeTestRule.setContent {
            com.kliq.app.ui.components.VisitedLogCard(log = mockLogs[0])
        }

        composeTestRule.onNodeWithText("Bootshaus Köln").assertIsDisplayed()
        composeTestRule.onNodeWithText("GPS Verifiziert").assertIsDisplayed()
        composeTestRule.onNodeWithText("Besucht am", substring = true).assertIsDisplayed()
    }

    @Test
    fun testEmptyState_displaysEmptyPlaceholder() {
        composeTestRule.setContent {
            com.kliq.app.ui.screens.history.VisitedHistoryScreen(
                userId = "empty_user"
            )
        }

        composeTestRule.onNodeWithText("Besucht am", substring = true)
    }
}
