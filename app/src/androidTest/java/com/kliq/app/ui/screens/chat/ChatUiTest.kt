package com.kliq.app.ui.screens.chat

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.kliq.app.MainActivity
import org.junit.Rule
import org.junit.Test

class ChatUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigateFromHomeToChatListAndBack() {

        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()
        composeTestRule.onNodeWithText("Öffentlich").assertIsDisplayed()
        composeTestRule.onNodeWithText("Privat").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()
    }

    @Test
    fun switchBetweenPublicAndPrivateTabs() {

        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()
        composeTestRule.onNodeWithText("Festival Crew 2026").assertIsDisplayed()

        composeTestRule.onNodeWithText("Privat").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Lisa W.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Max K.").assertIsDisplayed()

        composeTestRule.onNodeWithText("Öffentlich").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()
    }

    @Test
    fun chatListShowsDetailedEntryInformation() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()
        composeTestRule.onNodeWithText("Heute ab 20 Uhr im Bootshaus! 🎶").assertIsDisplayed()
        composeTestRule.onNodeWithText("14:32").assertIsDisplayed()
    }

    @Test
    fun navigateToChatDetailAndBack() {

        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()

        composeTestRule.onNodeWithText("Hey Leute, wer ist heute dabei?").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()
    }

    @Test
    fun chatDetailShowsMessageBubbles() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Max K.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hey Leute, wer ist heute dabei?").assertIsDisplayed()

        composeTestRule.onNodeWithText("Bin auf jeden Fall am Start! 🙋‍♂️").assertIsDisplayed()

        composeTestRule.onNodeWithText("Heute").assertIsDisplayed()
    }

    @Test
    fun sendMessageInChatDetail() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Nachricht schreiben…").performTextInput("Test Nachricht 123")
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithContentDescription("Senden").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Test Nachricht 123").assertIsDisplayed()
    }

    @Test
    fun bottomBarIsHiddenInChatScreens() {

        composeTestRule.onNodeWithContentDescription("Home").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNode(hasContentDescription("Home")).assertDoesNotExist()
        composeTestRule.onNode(hasContentDescription("Entdecken")).assertDoesNotExist()
    }

    @Test
    fun privateChatsShowOnlineStatusInDetail() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Privat").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Lisa W.").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Online").assertIsDisplayed()

        composeTestRule.onNodeWithText("Hey Lisa! Kommst du heute Abend?").assertIsDisplayed()
    }

    @Test
    fun fullChatEndToEndFlow() {

        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()

        composeTestRule.onNodeWithText("Privat").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Lisa W.").assertIsDisplayed()

        composeTestRule.onNodeWithText("Lisa W.").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Nachricht schreiben…").performTextInput("Bis gleich!")
        composeTestRule.onNodeWithContentDescription("Senden").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Bis gleich!").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Zurück").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Home").assertIsDisplayed()
    }
}
