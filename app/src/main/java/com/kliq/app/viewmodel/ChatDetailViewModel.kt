package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.ui.model.MessageHighContrastBubbleState
import com.kliq.app.ui.model.toHighContrastBubbleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatDetailUiState(
    val chatId: String? = null,
    val chatTitle: String = "",
    val isPublicCityChat: Boolean = false,
    val cityRegion: String? = null,
    val currentUserId: String = "usr_current_user",
    val currentUserName: String = "Ich",
    val isLoading: Boolean = false,
    val messages: List<MessageHighContrastBubbleState> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    fun loadChatDetails(chatId: String, currentUserId: String = "usr_current_user", currentUserName: String = "Ich") {
        _uiState.update { 
            it.copy(chatId = chatId, currentUserId = currentUserId, currentUserName = currentUserName, isLoading = true) 
        }

        viewModelScope.launch {
            combine(
                chatRepository.getAllChats(),
                chatRepository.getMessagesForChat(chatId)
            ) { conversations, messageList ->
                val currentChat = conversations.find { it.id == chatId }
                val isCityGroup = currentChat?.chatType == ChatType.PUBLIC_CITY

                val bubbleStates = messageList.map { msg ->
                    val isMine = msg.senderUserId == currentUserId || msg.isMine
                    msg.copy(isMine = isMine).toHighContrastBubbleState(isGroupChat = isCityGroup)
                }

                Triple(currentChat, isCityGroup, bubbleStates)
            }
            .catch { throwable ->
                _uiState.update { it.copy(isLoading = false, errorMessage = throwable.localizedMessage) }
            }
            .collect { (currentChat, isCityGroup, bubbleStates) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        chatTitle = currentChat?.name ?: "Chat",
                        isPublicCityChat = isCityGroup,
                        cityRegion = currentChat?.cityRegion,
                        messages = bubbleStates
                    )
                }
            }
        }
    }

    fun sendTextMessage(text: String) {
        val chatId = _uiState.value.chatId ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            val result = chatRepository.sendTextMessage(
                chatId = chatId,
                senderUserId = _uiState.value.currentUserId,
                senderName = _uiState.value.currentUserName,
                text = text.trim()
            )
            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.localizedMessage) }
            }
        }
    }

    fun sendPhotoMessage(mediaUrl: String, captionText: String = "") {
        val chatId = _uiState.value.chatId ?: return
        if (mediaUrl.isBlank()) return

        viewModelScope.launch {
            val result = chatRepository.sendMediaMessage(
                chatId = chatId,
                senderUserId = _uiState.value.currentUserId,
                senderName = _uiState.value.currentUserName,
                text = captionText.ifBlank { "Foto" },
                mediaUrl = mediaUrl
            )
            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.localizedMessage) }
            }
        }
    }

    fun markMessagesAsRead() {
        val chatId = _uiState.value.chatId ?: return
        viewModelScope.launch {
            chatRepository.markChatAsRead(chatId)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
