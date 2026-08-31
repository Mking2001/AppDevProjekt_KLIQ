package com.kliq.app.ui.theme

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kliq.app.MainActivity
import org.junit.Rule
import org.junit.Test

class ThemeRenderingTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreenRendersAllThemeElements() {

        composeTestRule.onNodeWithText("Kliq").assertIsDisplayed()

        composeTestRule.onNodeWithText("Anna").assertIsDisplayed()

        composeTestRule.onNodeWithText("Anna M.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vor 15 Min.").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Neuer Beitrag").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Filter").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Nachrichten").assertIsDisplayed()
    }

    @Test
    fun chatListRendersThemeElements() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Chats").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Suchen").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Zurück").assertIsDisplayed()

        composeTestRule.onNodeWithText("Öffentlich").assertIsDisplayed()
        composeTestRule.onNodeWithText("Privat").assertIsDisplayed()

        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()
        composeTestRule.onNodeWithText("Heute ab 20 Uhr im Bootshaus! 🎶").assertIsDisplayed()
        composeTestRule.onNodeWithText("14:32").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Neuer Chat").assertIsDisplayed()
    }

    @Test
    fun chatDetailRendersAllThemeElements() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Afterwork Köln").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Afterwork Köln").assertIsDisplayed()

        composeTestRule.onNodeWithText("Heute").assertIsDisplayed()

        composeTestRule.onNodeWithText("Max K.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hey Leute, wer ist heute dabei?").assertIsDisplayed()

        composeTestRule.onNodeWithText("Bin auf jeden Fall am Start! 🙋‍♂️").assertIsDisplayed()

        composeTestRule.onNodeWithText("Nachricht schreiben…").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Senden").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Mehr").assertIsDisplayed()
    }

    @Test
    fun privateChatDetailShowsOnlineIndicator() {
        composeTestRule.onNodeWithContentDescription("Nachrichten").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Privat").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Lisa W.").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Online").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lisa W.").assertIsDisplayed()
    }

    @Test
    fun bottomBarRendersAllFiveTabs() {
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entdecken").assertIsDisplayed()
        composeTestRule.onNodeWithText("Karte").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aktivität").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profil").assertIsDisplayed()
    }
}
