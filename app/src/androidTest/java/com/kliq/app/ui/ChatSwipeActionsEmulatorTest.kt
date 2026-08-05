package com.kliq.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.ChatListItem
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.LastMessage
import com.kliq.app.data.model.UserStatus
import com.kliq.app.ui.components.ChatListItem
import com.kliq.app.ui.components.DeleteChatConfirmationDialog
import com.kliq.app.ui.components.SwipeableActionRow
import com.kliq.app.ui.theme.KliqTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented Emulator UI Test for Chapter 8.2: Swipe-to-Action in Chat-Listen (Löschen/Archivieren).
 * Programmatically verifies swipe left (Archive) gesture, swipe right (Delete) gesture with confirmation dialog,
 * and permanent chat removal.
 */
@RunWith(AndroidJUnit4::class)
class ChatSwipeActionsEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testChatItem = ChatListItem(
        id = "priv_lisa",
        title = "Lisa W.",
        lastMessage = LastMessage(
            text = "Treffen wir uns vor dem Eingang?",
            timestampMs = System.currentTimeMillis()
        ),
        avatarInitial = "L",
        unreadCount = 2,
        chatType = ChatType.PRIVATE,
        userStatus = UserStatus.ONLINE
    )

    @Test
    fun testSwipeLeft_triggersArchiveCallback() {
        var isArchived = false

        composeTestRule.setContent {
            KliqTheme {
                SwipeableActionRow(
                    onDelete = {},
                    onArchive = { isArchived = true }
                ) {
                    ChatListItem(item = testChatItem, onClick = {})
                }
            }
        }

        composeTestRule.onNodeWithText("Lisa W.").assertIsDisplayed()

        // Simulate Swipe Left gesture (End-to-Start / Archive)
        composeTestRule.onNodeWithText("Lisa W.").performTouchInput {
            swipeLeft()
        }

        assertTrue(isArchived)
    }

    @Test
    fun testSwipeRight_showsDeleteConfirmationDialog() {
        var showDialog by mutableStateOf(false)

        composeTestRule.setContent {
            KliqTheme {
                if (showDialog) {
                    DeleteChatConfirmationDialog(
                        chatTitle = testChatItem.title,
                        onDismiss = { showDialog = false },
                        onConfirmDelete = { showDialog = false }
                    )
                }

                SwipeableActionRow(
                    onDelete = { showDialog = true },
                    onArchive = {}
                ) {
                    ChatListItem(item = testChatItem, onClick = {})
                }
            }
        }

        composeTestRule.onNodeWithText("Lisa W.").assertIsDisplayed()

        // Simulate Swipe Right gesture (Start-to-End / Delete)
        composeTestRule.onNodeWithText("Lisa W.").performTouchInput {
            swipeRight()
        }

        // Verify Delete Confirmation Dialog is displayed with correct title and message
        composeTestRule.onNodeWithText("Chat löschen?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Möchtest du den Chat mit „Lisa W.“ wirklich löschen?").assertIsDisplayed()
    }

    @Test
    fun testConfirmDeleteInDialog_invokesDeleteCallback() {
        var deleteConfirmed = false
        var showDialog by mutableStateOf(true)

        composeTestRule.setContent {
            KliqTheme {
                if (showDialog) {
                    DeleteChatConfirmationDialog(
                        chatTitle = testChatItem.title,
                        onDismiss = { showDialog = false },
                        onConfirmDelete = {
                            deleteConfirmed = true
                            showDialog = false
                        }
                    )
                }
            }
        }

        // Verify dialog is open
        composeTestRule.onNodeWithText("Chat löschen?").assertIsDisplayed()

        // Click "Chat löschen" confirm button
        composeTestRule.onNodeWithText("Chat löschen").performClick()

        assertTrue(deleteConfirmed)
        assertFalse(showDialog)
    }
}
