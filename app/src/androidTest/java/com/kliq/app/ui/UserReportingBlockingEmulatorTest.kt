package com.kliq.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.ui.components.BlockConfirmationDialog
import com.kliq.app.ui.components.UserReportBottomSheet
import com.kliq.app.ui.screens.chat.ChatDetailScreen
import com.kliq.app.ui.screens.chat.ChatDetailUiState
import com.kliq.app.ui.screens.profile.OtherUserProfileScreen
import com.kliq.app.ui.screens.profile.OtherUserProfileUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserReportingBlockingEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testReportingBottomSheet_selectReasonAndSubmit_invokesCallback() {
        var submittedReason: String? = null
        var submittedDetails: String? = null
        var isDismissed = false

        composeTestRule.setContent {
            UserReportBottomSheet(
                targetUsername = "Max Mustermann",
                onDismiss = { isDismissed = true },
                onReportSubmit = { reason, details ->
                    submittedReason = reason
                    submittedDetails = details
                }
            )
        }

        // Verify title & reasons
        composeTestRule.onNodeWithText("Max Mustermann melden").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fake-Profil").assertIsDisplayed()
        composeTestRule.onNodeWithText("Spam").assertIsDisplayed()

        // Select 'Fake-Profil'
        composeTestRule.onNodeWithText("Fake-Profil").performClick()

        // Enter details
        composeTestRule.onNodeWithText("Zusätzliche Details (optional)").performTextInput("Verwendet fremde Fotos")

        // Click submit
        composeTestRule.onNodeWithText("Meldung absenden").performClick()

        assertEquals("Fake-Profil", submittedReason)
        assertEquals("Verwendet fremde Fotos", submittedDetails)
    }

    @Test
    fun testBlockConfirmationDialog_confirmAction_invokesCallback() {
        var isConfirmed = false
        var isDismissed = false

        composeTestRule.setContent {
            BlockConfirmationDialog(
                targetUsername = "Lisa W.",
                onDismiss = { isDismissed = true },
                onConfirmBlock = { isConfirmed = true }
            )
        }

        // Verify title & warning
        composeTestRule.onNodeWithText("Lisa W. blockieren?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Möchtest du Lisa W. wirklich blockieren?").assertIsDisplayed()

        // Click confirm
        composeTestRule.onNodeWithText("Blockieren").performClick()

        assertTrue(isConfirmed)
    }

    @Test
    fun testProfileScreen_blockedState_displaysBlockedNoticeBanner() {
        val state = OtherUserProfileUiState(
            username = "Alex K.",
            isBlocked = true,
            isLoading = false
        )

        composeTestRule.setContent {
            OtherUserProfileScreen(
                uiState = state,
                onBackClick = {},
                onRateUserClick = {},
                onReportUserClick = {},
                onBlockToggleClick = {},
                onConfirmBlockClick = {},
                onReportSubmitClick = { _, _ -> },
                onDismissDialogs = {}
            )
        }

        composeTestRule.onNodeWithText("Du hast Alex K. blockiert.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entblocken").assertIsDisplayed()
    }

    @Test
    fun testChatDetailScreen_blockedState_disablesInputAndShowsBanner() {
        val state = ChatDetailUiState(
            conversationName = "Lisa W.",
            isBlocked = true
        )

        composeTestRule.setContent {
            ChatDetailScreen(
                uiState = state,
                onBackClick = {},
                onInputChanged = {},
                onSendMessage = {},
                onReportUserClick = {},
                onBlockToggleClick = {},
                onConfirmBlockClick = {},
                onReportSubmitClick = { _, _ -> },
                onDismissDialogs = {},
                onDismissBanner = {}
            )
        }

        composeTestRule.onNodeWithText("Du hast diesen Nutzer blockiert. Du kannst keine Nachrichten senden.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Blockiert").assertIsDisplayed()
    }
}
