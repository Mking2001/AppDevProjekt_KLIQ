package com.kliq.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.model.formatMsToIso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ChatDetailUiState(
    val conversationName: String = "",
    val conversationInitial: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val isOnline: Boolean = false
)

@HiltViewModel
class ChatDetailViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    private var messageCounter = 100

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

    fun onInputChanged(input: String) {
        _uiState.update { it.copy(currentInput = input) }
    }

    fun onSendMessage() {
        val text = _uiState.value.currentInput.trim()
        if (text.isEmpty()) return

        val now = System.currentTimeMillis()
        val newMessage = ChatMessage(
            id = "msg_${messageCounter++}",
            chatId = "mock_chat",
            senderUserId = "usr_current",
            senderName = "Du",
            text = text,
            timestampMs = now,
            timestampIso = formatMsToIso(now),
            status = MessageStatus.SENT,
            isMine = true
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + newMessage,
                currentInput = ""
            )
        }
    }

    private fun getMockConversation(chatId: String): ConversationData {
        val now = System.currentTimeMillis()
        return when (chatId) {
            "pub_1" -> ConversationData(
                name = "Berlin - Tonight",
                initial = "B",
                isOnline = false,
                messages = listOf(
                    ChatMessage("1", chatId, "usr_1", "Max K.", "Hey Leute, wer ist heute dabei?", now - 3600000L, formatMsToIso(now - 3600000L), mediaUrl = null, status = MessageStatus.READ, isMine = false, dateHeader = "Heute"),
                    ChatMessage("2", chatId, "usr_2", "Du", "Bin auf jeden Fall am Start! 🙋‍♂️", now - 3000000L, formatMsToIso(now - 3000000L), mediaUrl = null, status = MessageStatus.READ, isMine = true),
                    ChatMessage("3", chatId, "usr_3", "Lisa W.", "Ich auch! Komme direkt nach der Arbeit", now - 2400000L, formatMsToIso(now - 2400000L), mediaUrl = null, status = MessageStatus.READ, isMine = false)
                )
            )
            "priv_1" -> ConversationData(
                name = "Lisa W.",
                initial = "L",
                isOnline = true,
                messages = listOf(
                    ChatMessage("1", chatId, "usr_2", "Du", "Hey Lisa! Kommst du heute Abend?", now - 7200000L, formatMsToIso(now - 7200000L), mediaUrl = null, status = MessageStatus.READ, isMine = true, dateHeader = "Heute"),
                    ChatMessage("2", chatId, "usr_3", "Lisa W.", "Hey! Ja klar, freue mich schon 🥳", "https://kliq-app.de/uploads/sample.jpg", now - 3600000L, formatMsToIso(now - 3600000L), MessageStatus.READ, isMine = false)
                )
            )
            else -> ConversationData(
                name = "Unbekannter Chat",
                initial = "?",
                isOnline = false,
                messages = listOf(
                    ChatMessage("1", chatId, "usr_sys", "System", "Willkommen im Chat!", now, formatMsToIso(now), mediaUrl = null, status = MessageStatus.READ, isMine = false, dateHeader = "Heute")
                )
            )
        }
    }

    private data class ConversationData(
        val name: String,
        val initial: String,
        val isOnline: Boolean,
        val messages: List<ChatMessage>
    )
}
