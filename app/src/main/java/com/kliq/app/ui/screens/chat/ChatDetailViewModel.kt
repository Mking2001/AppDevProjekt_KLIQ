package com.kliq.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import com.kliq.app.data.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Immutable UI-State für den Chat-Detail-Screen.
 * Bildet den vollständigen Zustand des Chatverlaufs ab,
 * einschließlich der Eingabeleiste.
 *
 * @param conversationName Anzeigename des Chat-Partners/Gruppe.
 * @param conversationInitial Avatar-Initiale.
 * @param messages Chronologischer Nachrichtenverlauf.
 * @param currentInput Aktueller Text im Eingabefeld.
 * @param isOnline Online-Status des Gesprächspartners.
 */
data class ChatDetailUiState(
    val conversationName: String = "",
    val conversationInitial: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val isOnline: Boolean = false
)

/**
 * ViewModel für den Chat-Detail-Screen.
 * Verwaltet den Nachrichtenverlauf und die Eingabe-Logik
 * für einen einzelnen Chat.
 *
 * Folgt strikt dem MVVM-Pattern:
 * - Kein Compose/Android-Import
 * - Immutable State via [StateFlow]
 * - Intent-basierte Actions
 */
@HiltViewModel
class ChatDetailViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    private var messageCounter = 100

    /**
     * Lädt den Chatverlauf anhand der Chat-ID.
     * In der Scaffolding-Phase werden Mock-Daten basierend auf
     * der ID geladen. Wird später durch Repository-Aufrufe ersetzt.
     *
     * @param chatId Eindeutige ID der Konversation.
     */
    fun loadConversation(chatId: String) {
        val (name, initial, online, messages) = getMockConversation(chatId)
        _uiState.update {
            it.copy(
                conversationName = name,
                conversationInitial = initial,
                messages = messages,
                isOnline = online
            )
        }
    }

    /**
     * Aktualisiert den Eingabetext bei jeder Zeichenänderung.
     * @param input Neuer Eingabetext.
     */
    fun onInputChanged(input: String) {
        _uiState.update { it.copy(currentInput = input) }
    }

    /**
     * Sendet die aktuelle Eingabe als neue Nachricht.
     * Fügt die Nachricht am Ende des Verlaufs hinzu und
     * leert das Eingabefeld.
     */
    fun onSendMessage() {
        val text = _uiState.value.currentInput.trim()
        if (text.isEmpty()) return

        val newMessage = ChatMessage(
            id = "msg_${messageCounter++}",
            senderName = "Du",
            text = text,
            timestamp = "Jetzt",
            isMine = true
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + newMessage,
                currentInput = ""
            )
        }
    }

    /**
     * Liefert Mock-Daten für den Chatverlauf basierend auf der Chat-ID.
     * Erzeugt realistische Konversationen mit abwechselnden Nachrichten.
     */
    private fun getMockConversation(chatId: String): ConversationData {
        return when (chatId) {
            "pub_1" -> ConversationData(
                name = "Afterwork Köln",
                initial = "A",
                isOnline = false,
                messages = listOf(
                    ChatMessage("1", "Max K.", "Hey Leute, wer ist heute dabei?", "13:45", false, "Heute"),
                    ChatMessage("2", "Du", "Bin auf jeden Fall am Start! 🙋‍♂️", "13:50", true),
                    ChatMessage("3", "Lisa W.", "Ich auch! Komme direkt nach der Arbeit", "14:02", false),
                    ChatMessage("4", "Du", "Top! Treffen wir uns am Eingang?", "14:10", true),
                    ChatMessage("5", "Max K.", "Ja, so gegen 20 Uhr?", "14:15", false),
                    ChatMessage("6", "Anna M.", "Perfekt, bin auch dabei 🎉", "14:20", false),
                    ChatMessage("7", "Du", "Bis dann! 👋", "14:25", true),
                    ChatMessage("8", "Max K.", "Heute ab 20 Uhr im Bootshaus! 🎶", "14:32", false)
                )
            )
            "pub_2" -> ConversationData(
                name = "Festival Crew 2026",
                initial = "F",
                isOnline = false,
                messages = listOf(
                    ChatMessage("1", "Tom S.", "Das Line-up ist endlich da!", "10:00", false, "Heute"),
                    ChatMessage("2", "Du", "Zeig mal her! 👀", "10:05", true),
                    ChatMessage("3", "Sarah B.", "Wir brauchen noch Camping-Equipment", "10:30", false),
                    ChatMessage("4", "Tom S.", "Hat jemand noch ein Zelt übrig?", "12:15", false)
                )
            )
            "priv_1" -> ConversationData(
                name = "Lisa W.",
                initial = "L",
                isOnline = true,
                messages = listOf(
                    ChatMessage("1", "Du", "Hey Lisa! Kommst du heute Abend?", "14:00", true, "Heute"),
                    ChatMessage("2", "Lisa W.", "Hey! Ja klar, freue mich schon 🥳", "14:10", false),
                    ChatMessage("3", "Du", "Super! Soll ich dich abholen?", "14:15", true),
                    ChatMessage("4", "Lisa W.", "Das wäre mega nett!", "14:30", false),
                    ChatMessage("5", "Du", "Alles klar, bin um 19:30 bei dir", "14:45", true),
                    ChatMessage("6", "Lisa W.", "Perfekt! Bis gleich 💜", "14:50", false),
                    ChatMessage("7", "Lisa W.", "Treffen wir uns vor dem Eingang?", "15:08", false)
                )
            )
            "priv_2" -> ConversationData(
                name = "Max K.",
                initial = "M",
                isOnline = true,
                messages = listOf(
                    ChatMessage("1", "Max K.", "Alter, gestern war mega!", "12:00", false, "Heute"),
                    ChatMessage("2", "Du", "Ja richtig! Bester Abend seit Langem", "12:30", true),
                    ChatMessage("3", "Max K.", "Die Location war auch top", "13:00", false),
                    ChatMessage("4", "Du", "Müssen wir wiederholen 🔄", "13:20", true),
                    ChatMessage("5", "Max K.", "War ein geiler Abend! 🔥", "13:45", false)
                )
            )
            "priv_3" -> ConversationData(
                name = "Anna M.",
                initial = "A",
                isOnline = false,
                messages = listOf(
                    ChatMessage("1", "Du", "Hey Anna, kommst du Samstag?", "10:00", true, "Heute"),
                    ChatMessage("2", "Anna M.", "Danke für die Einladung!", "11:20", false)
                )
            )
            else -> ConversationData(
                name = "Unbekannter Chat",
                initial = "?",
                isOnline = false,
                messages = listOf(
                    ChatMessage("1", "System", "Willkommen im Chat!", "Jetzt", false, "Heute")
                )
            )
        }
    }

    /**
     * Hilfsklasse für die strukturierte Rückgabe von Mock-Konversationsdaten.
     */
    private data class ConversationData(
        val name: String,
        val initial: String,
        val isOnline: Boolean,
        val messages: List<ChatMessage>
    )
}
