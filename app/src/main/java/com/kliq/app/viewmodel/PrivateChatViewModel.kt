package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.DirectMessage
import com.kliq.app.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State fuer das 1-zu-1 Private Messaging.
 */
data class PrivateChatUiState(
    val currentUserId: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val receiverAvatarUrl: String? = null,
    val isOnline: Boolean = false,
    val messages: List<DirectMessage> = emptyList(),
    val currentInput: String = "",
    val isLoading: Boolean = false,
    val isEncryptedSession: Boolean = true,
    val errorMessage: String? = null
)

/**
 * ViewModel fuer die 1-zu-1 Chat-Logik nach MVVM.
 */
@HiltViewModel
class PrivateChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivateChatUiState())
    val uiState: StateFlow<PrivateChatUiState> = _uiState.asStateFlow()

    private var messageSubscriptionJob: Job? = null

    /**
     * Initialisiert den Konversations-State zwischen zwei Nutzern.
     */
    fun initConversation(
        currentUserId: String,
        receiverId: String,
        receiverName: String = "User",
        receiverAvatarUrl: String? = null,
        isOnline: Boolean = false
    ) {
        _uiState.update {
            it.copy(
                currentUserId = currentUserId,
                receiverId = receiverId,
                receiverName = receiverName,
                receiverAvatarUrl = receiverAvatarUrl,
                isOnline = isOnline,
                isLoading = true,
                errorMessage = null
            )
        }

        subscribeToMessages(currentUserId, receiverId)
    }

    private fun subscribeToMessages(currentUserId: String, receiverId: String) {
        messageSubscriptionJob?.cancel()
        messageSubscriptionJob = viewModelScope.launch {
            chatRepository.getDirectMessages(currentUserId, receiverId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) }
                }
                .collect { messageList ->
                    _uiState.update { state ->
                        state.copy(
                            messages = messageList,
                            isLoading = false
                        )
                    }
                    markAsRead(senderId = receiverId, receiverId = currentUserId)
                }
        }
    }

    /**
     * Reagiert auf Texteingabe-Aenderungen in der UI.
     */
    fun onInputChanged(text: String) {
        _uiState.update { it.copy(currentInput = text) }
    }

    /**
     * Sendet eine 1-zu-1 Direktnachricht an den Empfaenger.
     */
    fun sendMessage(
        receiverId: String = _uiState.value.receiverId,
        text: String = _uiState.value.currentInput
    ) {
        val trimmedText = text.trim()
        if (trimmedText.isEmpty()) return

        val currentUserId = _uiState.value.currentUserId
        if (currentUserId.isBlank() || receiverId.isBlank()) return

        viewModelScope.launch {
            val result = chatRepository.sendDirectMessage(
                senderId = currentUserId,
                receiverId = receiverId,
                text = trimmedText,
                isEncrypted = _uiState.value.isEncryptedSession
            )

            result.onSuccess { message ->
                _uiState.update { state -> state.copy(currentInput = "") }
                simulateStatusTransitions(message.messageId)
            }.onFailure { error ->
                _uiState.update { state -> state.copy(errorMessage = error.localizedMessage) }
            }
        }
    }

    /**
     * Sendet eine 1-zu-1 Sprachnachricht an den Empfaenger.
     */
    fun sendVoiceMessage(
        receiverId: String = _uiState.value.receiverId,
        audioUrl: String,
        audioDurationMs: Long
    ) {
        val currentUserId = _uiState.value.currentUserId
        if (currentUserId.isBlank() || receiverId.isBlank() || audioUrl.isBlank()) return

        viewModelScope.launch {
            val result = chatRepository.sendDirectVoiceMessage(
                senderId = currentUserId,
                receiverId = receiverId,
                audioUrl = audioUrl,
                audioDurationMs = audioDurationMs
            )

            result.onSuccess { message ->
                simulateStatusTransitions(message.messageId)
            }.onFailure { error ->
                _uiState.update { state -> state.copy(errorMessage = error.localizedMessage) }
            }
        }
    }

    private fun simulateStatusTransitions(messageId: String) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200)
            chatRepository.markDirectMessageAsDelivered(messageId)
            if (_uiState.value.isOnline) {
                kotlinx.coroutines.delay(2000)
                chatRepository.markDirectMessageAsRead(messageId)
            }
        }
    }

    /**
     * Verarbeitet eingehende Nachrichten in Echtzeit.
     */
    fun handleIncomingMessage(message: DirectMessage) {
        viewModelScope.launch {
            chatRepository.receiveDirectMessage(message)
        }
    }

    /**
     * Markiert die Konversation als gelesen.
     */
    fun markAsRead(
        senderId: String = _uiState.value.receiverId,
        receiverId: String = _uiState.value.currentUserId
    ) {
        if (senderId.isBlank() || receiverId.isBlank()) return
        viewModelScope.launch {
            chatRepository.markDirectConversationAsRead(senderId = senderId, receiverId = receiverId)
        }
    }

    /**
     * Umschalten der End-to-End Verschluesselung.
     */
    fun toggleEncryption(enabled: Boolean) {
        _uiState.update { it.copy(isEncryptedSession = enabled) }
    }
}
