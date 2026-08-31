package com.kliq.app.ui.screens.chat

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.MessageType
import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.domain.CurrentUserProvider
import com.kliq.app.util.ImageCompressor
import com.kliq.app.util.VoicePlayerManager
import com.kliq.app.util.VoiceRecorderManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatDetailUiState(
    val conversationName: String = "",
    val conversationInitial: String = "",
    val targetUserId: String = "",
    val chatType: ChatType = ChatType.PRIVATE,
    val messages: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val selectedImageUri: String? = null,
    val imageCaption: String = "",
    val isCompressingImage: Boolean = false,
    val isAttachmentSheetVisible: Boolean = false,
    val isRecordingVoice: Boolean = false,
    val recordingDurationMs: Long = 0L,
    val recordingAmplitudes: List<Float> = emptyList(),
    val playingMessageId: String? = null,
    val isPlayingVoice: Boolean = false,
    val voicePlaybackPositionMs: Long = 0L,
    val voicePlaybackDurationMs: Long = 0L,
    val isOnline: Boolean = false,
    val isBlocked: Boolean = false,
    val isReportDialogVisible: Boolean = false,
    val isBlockConfirmationDialogVisible: Boolean = false,
    val actionSuccessMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val imageCompressor: ImageCompressor,
    private val voiceRecorderManager: VoiceRecorderManager,
    private val voicePlayerManager: VoicePlayerManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    private var currentChatId: String = ""
    private var recordingTickerJob: Job? = null
    private var messageObserverJob: Job? = null
    private var chatObserverJob: Job? = null
    private var blockObserverJob: Job? = null
    private var syncJob: Job? = null

    fun loadConversation(chatId: String) {
        if (chatId.isBlank()) return

        val chatType = resolveChatType(chatId)
        val targetUserId = resolveTargetUserId(chatId)

        currentChatId = chatId
        _uiState.update {
            it.copy(
                targetUserId = targetUserId,
                chatType = chatType
            )
        }

        viewModelScope.launch {
            var chatTitle = resolveFallbackTitle(chatId, chatType)
            var avatarUrl: String? = null
            var avatarInitial = chatTitle.take(1).uppercase()

            if (chatType == ChatType.PRIVATE && targetUserId.isNotBlank()) {
                userRepository.syncUserProfile(targetUserId)
                val targetUser = try {
                    userRepository.getUserById(targetUserId).first()
                } catch (e: Exception) {
                    null
                }
                if (targetUser != null && targetUser.username.isNotBlank()) {
                    chatTitle = targetUser.username
                    avatarUrl = targetUser.profilePictureUrl
                    avatarInitial = chatTitle.take(1).uppercase()
                }
            }

            _uiState.update {
                it.copy(
                    conversationName = chatTitle,
                    conversationInitial = avatarInitial
                )
            }

            chatRepository.createChatIfMissing(
                chatId = chatId,
                name = chatTitle,
                chatType = chatType
            ).onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = "Chat konnte nicht geöffnet werden: ${error.localizedMessage}")
                }
            }

            if (chatTitle.isNotBlank() && chatTitle != "Direktnachricht" && chatTitle != "Chat") {
                chatRepository.updateChatName(chatId, chatTitle)
            }

            chatRepository.markChatAsRead(chatId)
            chatRepository.markAllSentMessagesAsDelivered(chatId)
        }

        observeChatMetadata(chatId)
        observeMessages(chatId)
        observeBlockedState()

        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            while (isActive) {
                chatRepository.syncChatMessages(chatId)
                val targetId = _uiState.value.targetUserId
                if (targetId.isNotBlank()) {
                    chatRepository.syncDirectMessages(currentUserProvider.userId(), targetId)
                }
                delay(3000L)
            }
        }
    }

    private fun observeChatMetadata(chatId: String) {
        chatObserverJob?.cancel()
        chatObserverJob = viewModelScope.launch {
            chatRepository.getChatById(chatId)
                .catch { error ->
                    _uiState.update {
                        it.copy(errorMessage = "Chatdaten konnten nicht geladen werden: ${error.localizedMessage}")
                    }
                }
                .collect { conversation ->
                    if (conversation == null) return@collect
                    val currentTitle = _uiState.value.conversationName
                    val displayTitle = if (conversation.chatType == ChatType.PRIVATE) {
                        if (conversation.name.isNotBlank() && conversation.name != "Direktnachricht" && conversation.name != "Kliq Chat" && conversation.name != "Chat") {
                            conversation.name
                        } else if (currentTitle.isNotBlank() && currentTitle != "Direktnachricht" && currentTitle != "Kliq Chat" && currentTitle != "Chat") {
                            currentTitle
                        } else {
                            conversation.name
                        }
                    } else {
                        conversation.name
                    }
                    val displayInitial = displayTitle.take(1).uppercase().ifBlank { "K" }
                    _uiState.update { state ->
                        state.copy(
                            conversationName = displayTitle,
                            conversationInitial = displayInitial,
                            chatType = conversation.chatType,
                            isOnline = conversation.isOnline
                        )
                    }
                }
        }
    }

    private fun observeMessages(chatId: String) {
        messageObserverJob?.cancel()
        messageObserverJob = viewModelScope.launch {
            chatRepository.getMessagesForChat(chatId)
                .catch { error ->
                    _uiState.update {
                        it.copy(errorMessage = "Nachrichten konnten nicht geladen werden: ${error.localizedMessage}")
                    }
                }
                .collect { messages ->
                    _uiState.update { it.copy(messages = withDateHeaders(messages)) }
                }
        }
    }

    private fun observeBlockedState() {
        blockObserverJob?.cancel()
        blockObserverJob = viewModelScope.launch {
            val currentUserId = currentUserProvider.userId()
            val targetId = _uiState.value.targetUserId
            if (targetId.isBlank()) return@launch

            userRepository.isUserBlocked(currentUserId, targetId)
                .catch { }
                .collect { isBlocked ->
                    _uiState.update { it.copy(isBlocked = isBlocked) }
                }
        }
    }

    private fun resolveChatType(chatId: String): ChatType =
        if (chatId.startsWith("pub_")) ChatType.PUBLIC_CITY else ChatType.PRIVATE

    private fun resolveFallbackTitle(chatId: String, chatType: ChatType): String = when {
        chatType == ChatType.PUBLIC_CITY -> "Kliq Stadt-Chat"
        chatId.startsWith("chat_") -> "Chat"
        else -> "Chat"
    }

    private fun resolveTargetUserId(chatId: String): String = when {
        chatId.startsWith("chat_") -> chatId.removePrefix("chat_")
        chatId.startsWith("priv_") -> "usr_${chatId.removePrefix("priv_")}"
        else -> chatId
    }

    private fun withDateHeaders(messages: List<ChatMessage>): List<ChatMessage> {
        var lastDay = ""
        return messages.map { message ->
            val day = message.timestampIso.take(10)
            if (day != lastDay) {
                lastDay = day
                message.copy(dateHeader = formatDateHeader(message.timestampMs))
            } else {
                message.copy(dateHeader = null)
            }
        }
    }

    private fun formatDateHeader(timestampMs: Long): String {
        val dayMs = 24L * 60L * 60L * 1000L
        val today = System.currentTimeMillis() / dayMs
        val messageDay = timestampMs / dayMs
        return when (today - messageDay) {
            0L -> "Heute"
            1L -> "Gestern"
            else -> java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.GERMAN)
                .format(java.util.Date(timestampMs))
        }
    }

    fun onInputChanged(input: String) {
        if (_uiState.value.isBlocked) return
        _uiState.update { it.copy(currentInput = input) }
    }

    private suspend fun resolveCurrentUserName(senderId: String): String {
        return try {
            val user = userRepository.getUserById(senderId).first()
            user?.username?.ifBlank { "User" } ?: "User"
        } catch (e: Exception) {
            "User"
        }
    }

    fun onSendMessage() {
        if (_uiState.value.isBlocked) return
        val text = _uiState.value.currentInput.trim()
        if (text.isEmpty() || currentChatId.isBlank()) return

        _uiState.update { it.copy(currentInput = "") }

        viewModelScope.launch {
            val senderId = currentUserProvider.userId()
            val senderName = resolveCurrentUserName(senderId)

            chatRepository.sendTextMessage(
                chatId = currentChatId,
                senderUserId = senderId,
                senderName = senderName,
                text = text
            ).onSuccess { message ->
                advanceMessageStatus(message.id)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        currentInput = text,
                        errorMessage = "Nachricht konnte nicht gesendet werden: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun advanceMessageStatus(messageId: String) {
        viewModelScope.launch {
            delay(DELIVERY_DELAY_MS)
            chatRepository.markMessageAsDelivered(messageId)

            if (_uiState.value.isOnline) {
                delay(READ_DELAY_MS)
                chatRepository.markMessageAsRead(messageId)
            }
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
        if (_uiState.value.isBlocked || currentChatId.isBlank()) return

        _uiState.update { it.copy(isCompressingImage = true) }

        viewModelScope.launch {
            try {
                val uri = Uri.parse(imageUriString)
                val compressedResult = imageCompressor.compressAndSaveImage(context, uri)
                val caption = _uiState.value.imageCaption
                val senderId = currentUserProvider.userId()
                val senderName = resolveCurrentUserName(senderId)

                chatRepository.sendMediaMessage(
                    chatId = currentChatId,
                    senderUserId = senderId,
                    senderName = senderName,
                    text = caption.ifBlank { "Foto" },
                    mediaUrl = compressedResult.mediaUrl,
                    messageType = MessageType.IMAGE,
                    thumbnailUrl = compressedResult.thumbnailUrl,
                    aspectRatio = compressedResult.aspectRatio,
                    mediaWidth = compressedResult.width,
                    mediaHeight = compressedResult.height,
                    captionText = caption.ifBlank { null }
                ).onSuccess { message ->
                    _uiState.update {
                        it.copy(
                            selectedImageUri = null,
                            imageCaption = "",
                            isCompressingImage = false
                        )
                    }
                    advanceMessageStatus(message.id)
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isCompressingImage = false,
                            errorMessage = "Bild konnte nicht gesendet werden: ${error.localizedMessage}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCompressingImage = false,
                        errorMessage = "Fehler beim Komprimieren des Bildes: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun deleteCurrentChat(onDeleted: () -> Unit) {
        if (currentChatId.isBlank()) return
        viewModelScope.launch {
            chatRepository.deleteChat(currentChatId)
            onDeleted()
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
            userRepository.reportUser(currentUserProvider.userId(), targetId, reason, details)
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
            userRepository.blockUser(currentUserProvider.userId(), targetId, reason)
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
            userRepository.unblockUser(currentUserProvider.userId(), targetId)
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

    fun startVoiceRecording(context: Context) {
        if (_uiState.value.isBlocked) return
        val success = voiceRecorderManager.startRecording(context)
        if (!success) {
            _uiState.update { it.copy(errorMessage = "Sprachaufnahme konnte nicht gestartet werden.") }
            return
        }

        _uiState.update {
            it.copy(
                isRecordingVoice = true,
                recordingDurationMs = 0L,
                recordingAmplitudes = emptyList()
            )
        }

        recordingTickerJob?.cancel()
        recordingTickerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive && voiceRecorderManager.isRecording) {
                val duration = System.currentTimeMillis() - startTime
                val amp = voiceRecorderManager.getMaxAmplitudeNormalized()
                _uiState.update { state ->
                    val updatedAmplitudes = (state.recordingAmplitudes + amp).takeLast(RECORDING_WAVEFORM_SAMPLES)
                    state.copy(
                        recordingDurationMs = duration,
                        recordingAmplitudes = updatedAmplitudes
                    )
                }
                delay(RECORDING_TICK_MS)
            }
        }
    }

    fun stopAndSendVoiceRecording() {
        recordingTickerJob?.cancel()
        recordingTickerJob = null

        val result = voiceRecorderManager.stopRecording()
        _uiState.update {
            it.copy(
                isRecordingVoice = false,
                recordingDurationMs = 0L,
                recordingAmplitudes = emptyList()
            )
        }

        if (result == null || currentChatId.isBlank()) return

        viewModelScope.launch {
            val senderId = currentUserProvider.userId()
            val senderName = resolveCurrentUserName(senderId)

            chatRepository.sendVoiceMessage(
                chatId = currentChatId,
                senderUserId = senderId,
                senderName = senderName,
                audioUrl = result.filePath,
                audioDurationMs = result.durationMs
            ).onSuccess { message ->
                advanceMessageStatus(message.id)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = "Sprachnachricht konnte nicht gesendet werden: ${error.localizedMessage}")
                }
            }
        }
    }

    fun cancelVoiceRecording() {
        recordingTickerJob?.cancel()
        recordingTickerJob = null
        voiceRecorderManager.cancelRecording()
        _uiState.update {
            it.copy(
                isRecordingVoice = false,
                recordingDurationMs = 0L,
                recordingAmplitudes = emptyList()
            )
        }
    }

    fun togglePlayVoiceMessage(messageId: String, audioUrl: String?) {
        if (audioUrl.isNullOrBlank()) return

        if (_uiState.value.playingMessageId == messageId && _uiState.value.isPlayingVoice) {
            voicePlayerManager.pause()
            _uiState.update { it.copy(isPlayingVoice = false) }
        } else {
            voicePlayerManager.play(
                messageId = messageId,
                audioUrl = audioUrl,
                onProgressUpdate = { currentMs, durationMs ->
                    _uiState.update {
                        it.copy(
                            playingMessageId = messageId,
                            isPlayingVoice = true,
                            voicePlaybackPositionMs = currentMs,
                            voicePlaybackDurationMs = durationMs
                        )
                    }
                },
                onCompletion = {
                    _uiState.update {
                        it.copy(
                            playingMessageId = null,
                            isPlayingVoice = false,
                            voicePlaybackPositionMs = 0L,
                            voicePlaybackDurationMs = 0L
                        )
                    }
                }
            )
        }
    }

    fun seekVoiceMessage(positionMs: Long) {
        voicePlayerManager.seekTo(positionMs)
        _uiState.update { it.copy(voicePlaybackPositionMs = positionMs) }
    }

    override fun onCleared() {
        super.onCleared()
        recordingTickerJob?.cancel()
        voiceRecorderManager.release()
        voicePlayerManager.release()
    }

    private companion object {
        const val DELIVERY_DELAY_MS = 1_200L
        const val READ_DELAY_MS = 2_000L
        const val RECORDING_TICK_MS = 100L
        const val RECORDING_WAVEFORM_SAMPLES = 30
    }
}
