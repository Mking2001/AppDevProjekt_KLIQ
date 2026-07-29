package com.kliq.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.model.formatMsToIso
import com.kliq.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import android.net.Uri
import com.kliq.app.data.model.MessageType
import com.kliq.app.util.ImageCompressor
import javax.inject.Inject

data class ChatDetailUiState(
    val conversationName: String = "",
    val conversationInitial: String = "",
    val targetUserId: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val selectedImageUri: String? = null,
    val imageCaption: String = "",
    val isCompressingImage: Boolean = false,
    val isAttachmentSheetVisible: Boolean = false,
    val isOnline: Boolean = false,
    val isBlocked: Boolean = false,
    val isReportDialogVisible: Boolean = false,
    val isBlockConfirmationDialogVisible: Boolean = false,
    val actionSuccessMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    private var messageCounter = 100
    private var currentChatId: String = ""

    fun loadConversation(chatId: String) {
        currentChatId = chatId
        val (name, initial, online, targetId, messages) = getMockConversation(chatId)
        _uiState.update {
            it.copy(
                conversationName = name,
                conversationInitial = initial,
                targetUserId = targetId,
                messages = messages,
                isOnline = online
            )
        }

        viewModelScope.launch {
            userRepository.isUserBlocked("current_user", targetId)
                .catch { }
                .collect { isBlocked ->
                    _uiState.update { it.copy(isBlocked = isBlocked) }
                }
        }
    }

    fun onInputChanged(input: String) {
        if (_uiState.value.isBlocked) return
        _uiState.update { it.copy(currentInput = input) }
    }

    fun onSendMessage() {
        if (_uiState.value.isBlocked) return
        val text = _uiState.value.currentInput.trim()
        if (text.isEmpty()) return

        val now = System.currentTimeMillis()
        val newMessage = ChatMessage(
            id = "msg_${messageCounter++}",
            chatId = currentChatId.ifBlank { "mock_chat" },
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

    fun openAttachmentSheet() {
        if (_uiState.value.isBlocked) return
        _uiState.update { it.copy(isAttachmentSheetVisible = true) }
    }

    fun closeAttachmentSheet() {
        _uiState.update { it.copy(isAttachmentSheetVisible = false) }
    }

    fun onImageSelected(uri: String) {
        _uiState.update {
            it.copy(
                selectedImageUri = uri,
                imageCaption = "",
                isAttachmentSheetVisible = false
            )
        }
    }

    fun onImageCaptionChanged(caption: String) {
        _uiState.update { it.copy(imageCaption = caption) }
    }

    fun clearSelectedImage() {
        _uiState.update { it.copy(selectedImageUri = null, imageCaption = "", isCompressingImage = false) }
    }

    fun sendSelectedImage(context: Context) {
        val imageUriString = _uiState.value.selectedImageUri ?: return
        if (_uiState.value.isBlocked) return

        _uiState.update { it.copy(isCompressingImage = true) }

        viewModelScope.launch {
            try {
                val uri = Uri.parse(imageUriString)
                val compressedResult = imageCompressor.compressAndSaveImage(context, uri)
                val now = System.currentTimeMillis()

                val newMediaMessage = ChatMessage(
                    id = "msg_${messageCounter++}",
                    chatId = currentChatId.ifBlank { "mock_chat" },
                    senderUserId = "usr_current",
                    senderName = "Du",
                    text = _uiState.value.imageCaption.ifBlank { "📷 Foto" },
                    timestampMs = now,
                    timestampIso = formatMsToIso(now),
                    mediaUrl = compressedResult.mediaUrl,
                    thumbnailUrl = compressedResult.thumbnailUrl,
                    aspectRatio = compressedResult.aspectRatio,
                    mediaWidth = compressedResult.width,
                    mediaHeight = compressedResult.height,
                    captionText = _uiState.value.imageCaption,
                    messageType = MessageType.IMAGE,
                    status = MessageStatus.SENT,
                    isMine = true
                )

                _uiState.update { state ->
                    state.copy(
                        messages = state.messages + newMediaMessage,
                        selectedImageUri = null,
                        imageCaption = "",
                        isCompressingImage = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCompressingImage = false,
                        errorMessage = "Fehler beim Komprimieren/Senden des Bildes: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun openReportDialog() {
        _uiState.update { it.copy(isReportDialogVisible = true) }
    }

    fun closeReportDialog() {
        _uiState.update { it.copy(isReportDialogVisible = false) }
    }

    fun reportUser(reason: String, details: String = "") {
        val targetId = _uiState.value.targetUserId
        viewModelScope.launch {
            userRepository.reportUser("current_user", targetId, reason, details)
            _uiState.update {
                it.copy(
                    isReportDialogVisible = false,
                    actionSuccessMessage = "Nutzer wurde gemeldet. Das Kliq-Sicherheitsteam prüft die Meldung."
                )
            }
        }
    }

    fun openBlockConfirmationDialog() {
        _uiState.update { it.copy(isBlockConfirmationDialogVisible = true) }
    }

    fun closeBlockConfirmationDialog() {
        _uiState.update { it.copy(isBlockConfirmationDialogVisible = false) }
    }

    fun toggleBlockUser() {
        if (_uiState.value.isBlocked) {
            unblockUser()
        } else {
            openBlockConfirmationDialog()
        }
    }

    fun confirmBlockUser(reason: String? = null) {
        val targetId = _uiState.value.targetUserId
        viewModelScope.launch {
            userRepository.blockUser("current_user", targetId, reason)
            _uiState.update {
                it.copy(
                    isBlocked = true,
                    isBlockConfirmationDialogVisible = false,
                    actionSuccessMessage = "Nutzer wurde blockiert."
                )
            }
        }
    }

    fun unblockUser() {
        val targetId = _uiState.value.targetUserId
        viewModelScope.launch {
            userRepository.unblockUser("current_user", targetId)
            _uiState.update {
                it.copy(
                    isBlocked = false,
                    isBlockConfirmationDialogVisible = false,
                    actionSuccessMessage = "Blockierung aufgehoben."
                )
            }
        }
    }

    fun dismissMessage() {
        _uiState.update { it.copy(errorMessage = null, actionSuccessMessage = null) }
    }

    private fun getMockConversation(chatId: String): ConversationData {
        val now = System.currentTimeMillis()
        return when (chatId) {
            "pub_1" -> ConversationData(
                name = "Berlin - Tonight",
                initial = "B",
                isOnline = false,
                targetUserId = "usr_pub_group",
                messages = listOf(
                    ChatMessage(
                        id = "1",
                        chatId = chatId,
                        senderUserId = "usr_1",
                        senderName = "Max K.",
                        text = "Hey Leute, wer ist heute dabei?",
                        timestampMs = now - 3600000L,
                        timestampIso = formatMsToIso(now - 3600000L),
                        mediaUrl = null,
                        status = MessageStatus.READ,
                        isMine = false,
                        dateHeader = "Heute"
                    ),
                    ChatMessage(
                        id = "2",
                        chatId = chatId,
                        senderUserId = "usr_2",
                        senderName = "Du",
                        text = "Bin auf jeden Fall am Start! 🙋‍♂️",
                        timestampMs = now - 3000000L,
                        timestampIso = formatMsToIso(now - 3000000L),
                        mediaUrl = null,
                        status = MessageStatus.READ,
                        isMine = true
                    ),
                    ChatMessage(
                        id = "3",
                        chatId = chatId,
                        senderUserId = "usr_3",
                        senderName = "Lisa W.",
                        text = "Ich auch! Komme direkt nach der Arbeit",
                        timestampMs = now - 2400000L,
                        timestampIso = formatMsToIso(now - 2400000L),
                        mediaUrl = null,
                        status = MessageStatus.READ,
                        isMine = false
                    )
                )
            )
            "priv_1" -> ConversationData(
                name = "Lisa W.",
                initial = "L",
                isOnline = true,
                targetUserId = "usr_3",
                messages = listOf(
                    ChatMessage(
                        id = "1",
                        chatId = chatId,
                        senderUserId = "usr_2",
                        senderName = "Du",
                        text = "Hey Lisa! Kommst du heute Abend?",
                        timestampMs = now - 7200000L,
                        timestampIso = formatMsToIso(now - 7200000L),
                        mediaUrl = null,
                        status = MessageStatus.READ,
                        isMine = true,
                        dateHeader = "Heute"
                    ),
                    ChatMessage(
                        id = "2",
                        chatId = chatId,
                        senderUserId = "usr_3",
                        senderName = "Lisa W.",
                        text = "Hey! Ja klar, freue mich schon 🥳",
                        timestampMs = now - 3600000L,
                        timestampIso = formatMsToIso(now - 3600000L),
                        mediaUrl = "https://kliq-app.de/uploads/sample.jpg",
                        status = MessageStatus.READ,
                        isMine = false
                    )
                )
            )
            else -> ConversationData(
                name = "Unbekannter Chat",
                initial = "?",
                isOnline = false,
                targetUserId = "usr_unknown",
                messages = listOf(
                    ChatMessage(
                        id = "1",
                        chatId = chatId,
                        senderUserId = "usr_sys",
                        senderName = "System",
                        text = "Willkommen im Chat!",
                        timestampMs = now,
                        timestampIso = formatMsToIso(now),
                        mediaUrl = null,
                        status = MessageStatus.READ,
                        isMine = false,
                        dateHeader = "Heute"
                    )
                )
            )
        }
    }

    private data class ConversationData(
        val name: String,
        val initial: String,
        val isOnline: Boolean,
        val targetUserId: String,
        val messages: List<ChatMessage>
    )
}
