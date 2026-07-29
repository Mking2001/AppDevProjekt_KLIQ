package com.kliq.app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.ui.components.ChatBubble
import com.kliq.app.ui.components.ChatDateDivider
import com.kliq.app.ui.components.ChatInputBar
import com.kliq.app.ui.theme.KliqTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatDetailScreenEmulatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testOutgoingChatBubble_rendersTextAndStatus() {
        val message = ChatMessage(
            id = "msg_1",
            chatId = "chat_1",
            senderUserId = "usr_current",
            senderName = "Du",
            text = "Bin in 5 Minuten da! 🚀",
            status = MessageStatus.READ,
            isMine = true
        )

        composeTestRule.setContent {
            KliqTheme {
                ChatBubble(message = message)
            }
        }

        composeTestRule.onNodeWithText("Bin in 5 Minuten da! 🚀").assertIsDisplayed()
    }

    @Test
    fun testIncomingChatBubble_rendersSenderNameAndText() {
        val message = ChatMessage(
            id = "msg_2",
            chatId = "chat_1",
            senderUserId = "usr_lisa",
            senderName = "Lisa W.",
            text = "Alles klar, bis gleich!",
            status = MessageStatus.READ,
            isMine = false
        )

        composeTestRule.setContent {
            KliqTheme {
                ChatBubble(message = message)
            }
        }

        composeTestRule.onNodeWithText("Lisa W.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Alles klar, bis gleich!").assertIsDisplayed()
    }

    @Test
    fun testChatDateDivider_rendersDatePill() {
        composeTestRule.setContent {
            KliqTheme {
                ChatDateDivider(dateText = "Heute")
            }
        }

        composeTestRule.onNodeWithText("Heute").assertIsDisplayed()
    }

    @Test
    fun testChatInputBar_typingTextEnablesSendButton() {
        var inputVal = ""
        var isSent = false

        composeTestRule.setContent {
            KliqTheme {
                ChatInputBar(
                    value = inputVal,
                    onValueChange = { inputVal = it },
                    onSend = { isSent = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Nachricht schreiben…").performTextInput("Hallo!")
        assertEquals("Hallo!", inputVal)
    }
}
