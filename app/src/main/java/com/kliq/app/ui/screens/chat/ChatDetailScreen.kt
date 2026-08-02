package com.kliq.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.platform.LocalContext
import com.kliq.app.data.model.ChatType
import com.kliq.app.ui.components.AttachmentOptionsSheet
import com.kliq.app.ui.components.ChatBubble
import com.kliq.app.ui.components.ChatInputBar
import com.kliq.app.ui.components.GroupPresenceHeader
import com.kliq.app.ui.components.GroupPresenceParticipantSheet
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.PurplePrimaryLight
import com.kliq.app.viewmodel.GroupPresenceViewModel

/**
 * ChatDetailScreen - Unterstützt sowohl Stadt-Gruppenchats mit Who's Online Präsenz-Modul (Kapitel 6.8)
 * als auch Direktnachrichten und Sprachnachrichten (Kapitel 6.9).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    chatTitle: String = "Berlin - Tonight",
    chatType: ChatType = ChatType.PUBLIC_CITY,
    onNavigateBack: () -> Unit,
    chatViewModel: ChatDetailViewModel = hiltViewModel(),
    presenceViewModel: GroupPresenceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val chatUiState by chatViewModel.uiState.collectAsStateWithLifecycle()
    val presenceUiState by presenceViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(chatId) {
        chatViewModel.loadConversation(chatId)
        if (chatType == ChatType.PUBLIC_CITY) {
            presenceViewModel.loadGroupPresence(chatId)
        }
    }

    LaunchedEffect(chatUiState.errorMessage, presenceUiState.errorMessage) {
        val error = chatUiState.errorMessage ?: presenceUiState.errorMessage
        error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            chatViewModel.dismissMessage()
            presenceViewModel.clearError()
        }
    }

    LaunchedEffect(chatUiState.messages.size) {
        if (chatUiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatUiState.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    if (chatType == ChatType.PUBLIC_CITY) {
                        GroupPresenceHeader(
                            title = if (presenceUiState.chatTitle.isNotBlank()) presenceUiState.chatTitle else chatTitle,
                            onlineCount = presenceUiState.totalOnlineCount,
                            onHeaderClick = { presenceViewModel.toggleParticipantSheet() }
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = chatTitle.take(1),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = chatTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Zurück",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (chatType == ChatType.PUBLIC_CITY) {
                        IconButton(onClick = { presenceViewModel.toggleParticipantSheet() }) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "Teilnehmer anzeigen",
                                tint = PurplePrimaryLight
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                value = chatUiState.currentInput,
                onValueChange = chatViewModel::onInputChanged,
                onSend = chatViewModel::onSendMessage,
                onAttachClick = chatViewModel::openAttachmentSheet,
                isRecordingVoice = chatUiState.isRecordingVoice,
                recordingDurationMs = chatUiState.recordingDurationMs,
                recordingAmplitudes = chatUiState.recordingAmplitudes,
                onStartRecordVoice = { chatViewModel.startVoiceRecording(context) },
                onStopAndSendRecordVoice = chatViewModel::stopAndSendVoiceRecording,
                onCancelRecordVoice = chatViewModel::cancelVoiceRecording
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatUiState.messages.size) { index ->
                    val message = chatUiState.messages[index]
                    ChatBubble(
                        message = message,
                        isPlayingVoice = chatUiState.playingMessageId == message.id && chatUiState.isPlayingVoice,
                        voicePlaybackPositionMs = if (chatUiState.playingMessageId == message.id) chatUiState.voicePlaybackPositionMs else 0L,
                        voicePlaybackDurationMs = if (chatUiState.playingMessageId == message.id) chatUiState.voicePlaybackDurationMs else 0L,
                        onPlayPauseVoice = { chatViewModel.togglePlayVoiceMessage(message.id, message.mediaUrl) },
                        onSeekVoice = { posMs -> chatViewModel.seekVoiceMessage(posMs) }
                    )
                }
            }

            if (chatUiState.isAttachmentSheetVisible) {
                AttachmentOptionsSheet(
                    onOptionGallery = { },
                    onOptionCamera = { },
                    onDismiss = chatViewModel::closeAttachmentSheet
                )
            }

            if (presenceUiState.isParticipantSheetExpanded) {
                GroupPresenceParticipantSheet(
                    title = if (presenceUiState.chatTitle.isNotBlank()) presenceUiState.chatTitle else chatTitle,
                    onlineCount = presenceUiState.totalOnlineCount,
                    totalMembersCount = presenceUiState.totalMembersCount,
                    members = presenceUiState.filteredMembers,
                    searchQuery = presenceUiState.searchQuery,
                    currentStatus = presenceUiState.myPresenceStatus,
                    onSearchQueryChanged = presenceViewModel::onSearchQueryChanged,
                    onStatusSelected = presenceViewModel::updateMyPresenceStatus,
                    onDismiss = { presenceViewModel.setParticipantSheetExpanded(false) }
                )
            }
        }
    }
}
