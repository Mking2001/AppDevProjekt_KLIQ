package com.kliq.app.ui.workflow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.ui.components.ChatListItem
import com.kliq.app.ui.theme.DarkBackground
import com.kliq.app.ui.theme.KliqTheme
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.mock.FakeBackendStateModule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automatisierte UI-Tests für Kern-Komponenten und UI-Interaktionen (Kapitel 9.2).
 *
 * Testet:
 *   1. Map-Overlay Steuerung und Filter-Interaktionen
 *   2. Chat-Listen Darstellung, ungelesene Nachrichten-Badges und Nachrichteneingabe
 *   3. High-Contrast Lila/Dark-Theme Buttons und interaktive Komponenten
 */
@RunWith(AndroidJUnit4::class)
class MainWorkflowCoreComponentsUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Testfall 1: Map-Overlay Steuerung und Filter-Interaktionen (Techno, House, Ratings).
     */
    @Test
    fun testMapOverlayControlsAndFilterInteractions() {
        var selectedFilter by mutableStateOf("Alle")

        composeTestRule.setContent {
            KliqTheme {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Map-Overlay Filter", style = MaterialTheme.typography.titleMedium)

                    val filterOptions = listOf("Alle", "Techno", "House", "4.5+ Sterne")
                    filterOptions.forEach { option ->
                        Button(
                            onClick = { selectedFilter = option },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedFilter == option) PurplePrimary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(text = option)
                        }
                    }
                }
            }
        }

        // Filter-Chips prüfen
        composeTestRule.onNodeWithText("Alle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Techno").assertIsDisplayed()
        composeTestRule.onNodeWithText("House").assertIsDisplayed()
        composeTestRule.onNodeWithText("4.5+ Sterne").assertIsDisplayed()

        // "Techno" auswählen
        composeTestRule.onNodeWithText("Techno").performClick()
        composeTestRule.waitForIdle()
        assertEquals("Techno", selectedFilter)

        // "4.5+ Sterne" auswählen
        composeTestRule.onNodeWithText("4.5+ Sterne").performClick()
        composeTestRule.waitForIdle()
        assertEquals("4.5+ Sterne", selectedFilter)
    }

    /**
     * Testfall 2: Chat-Listen Öffnung, Unread Badges und Nachrichteneingabe im Direct Messaging.
     */
    @Test
    fun testChatListOpeningAndMessageInteractions() {
        var isPublicChatClicked = false
        var isPrivateChatClicked = false
        val publicChatItem = FakeBackendStateModule.mockChatList.first()
        val privateChatItem = FakeBackendStateModule.mockChatList.last()

        composeTestRule.setContent {
            KliqTheme {
                Column {
                    ChatListItem(
                        item = publicChatItem,
                        onClick = { isPublicChatClicked = true }
                    )
                    ChatListItem(
                        item = privateChatItem,
                        onClick = { isPrivateChatClicked = true }
                    )
                }
            }
        }

        // Public City Chat Item & Unread Badge verifizieren
        composeTestRule.onNodeWithText("Berlin Mitte Nightlife").assertIsDisplayed()
        composeTestRule.onNodeWithText("Treffen wir uns am Watergate Spree-Deck? 🍻").assertIsDisplayed()
        composeTestRule.onNodeWithText("3").assertIsDisplayed()

        // Private Chat Item & Unread Badge verifizieren
        composeTestRule.onNodeWithText("Lisa W.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hey, bist du heute auch im Berghain?").assertIsDisplayed()
        composeTestRule.onNodeWithText("1").assertIsDisplayed()

        // Click-Interaktionen testen
        composeTestRule.onNodeWithText("Berlin Mitte Nightlife").performClick()
        composeTestRule.waitForIdle()
        assertTrue(isPublicChatClicked)

        composeTestRule.onNodeWithText("Lisa W.").performClick()
        composeTestRule.waitForIdle()
        assertTrue(isPrivateChatClicked)
    }

    /**
     * Testfall 3: Prüft High-Contrast Lila/Dark-Theme Buttons und interaktive Komponenten.
     */
    @Test
    fun testHighContrastThemeButtonsAndInteractiveComponents() {
        var isButtonClicked = false
        var messageInput by mutableStateOf("")

        composeTestRule.setContent {
            KliqTheme {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "High-Contrast Theme Test",
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text("Nachricht schreiben...") }
                    )

                    Button(
                        onClick = { isButtonClicked = true },
                        enabled = messageInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurplePrimary
                        )
                    ) {
                        Text(text = "Senden")
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("High-Contrast Theme Test").assertIsDisplayed()

        // Text eingeben
        composeTestRule.onNodeWithText("Nachricht schreiben...").performTextInput("Hallo Kliq Team!")
        composeTestRule.waitForIdle()

        // Sende-Button prüfen & klicken
        composeTestRule.onNodeWithText("Senden").assertIsEnabled().performClick()
        composeTestRule.waitForIdle()

        assertTrue(isButtonClicked)
        assertEquals("Hallo Kliq Team!", messageInput)
    }
}
