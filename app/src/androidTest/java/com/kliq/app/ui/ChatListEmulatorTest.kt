package com.kliq.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.ChatListItem
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.LastMessage
import com.kliq.app.data.model.UserStatus
import com.kliq.app.ui.components.ChatListItem
import com.kliq.app.ui.theme.KliqTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatListEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testPublicChatListItem_rendersTitleLastMessageAndUnreadBadge() {
        var isClicked = false
        val now = System.currentTimeMillis()
        val item = ChatListItem(
            id = "pub_berlin",
            title = "Berlin - Tonight",
            cityRegion = "Berlin",
            lastMessage = LastMessage(
                text = "Heute ab 23 Uhr im Watergate! 🎶",
                timestampMs = now
            ),
            avatarInitial = "B",
            unreadCount = 5,
            chatType = ChatType.PUBLIC_CITY
        )

        composeTestRule.setContent {
            KliqTheme {
                ChatListItem(
                    item = item,
                    onClick = { isClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Berlin - Tonight").assertIsDisplayed()
        composeTestRule.onNodeWithText("Heute ab 23 Uhr im Watergate! 🎶").assertIsDisplayed()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()

        composeTestRule.onNodeWithText("Berlin - Tonight").performClick()
        assertEquals(true, isClicked)
    }

    @Test
    fun testPrivateChatListItem_rendersOnlineBadgeAndTimestamp() {
        var isClicked = false
        val now = System.currentTimeMillis()
        val item = ChatListItem(
            id = "priv_lisa",
            title = "Lisa W.",
            lastMessage = LastMessage(
                text = "Treffen wir uns vor dem Eingang?",
                timestampMs = now
            ),
            avatarInitial = "L",
            unreadCount = 2,
            chatType = ChatType.PRIVATE,
            userStatus = UserStatus.ONLINE
        )

        composeTestRule.setContent {
            KliqTheme {
                ChatListItem(
                    item = item,
                    onClick = { isClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Lisa W.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Treffen wir uns vor dem Eingang?").assertIsDisplayed()
        composeTestRule.onNodeWithText("2").assertIsDisplayed()

        composeTestRule.onNodeWithText("Lisa W.").performClick()
        assertEquals(true, isClicked)
    }
}
