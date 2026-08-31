package com.kliq.app.ui.screens.chat

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.data.model.ChatType
import com.kliq.app.ui.components.AttachmentOptionsSheet
import com.kliq.app.ui.components.ChatBubble
import com.kliq.app.ui.components.ChatInputBar
import com.kliq.app.ui.components.GroupInfoSheet
import com.kliq.app.ui.components.GroupPresenceHeader
import com.kliq.app.ui.components.GroupPresenceParticipantSheet
import com.kliq.app.ui.components.ImagePreviewSendDialog
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight
import com.kliq.app.viewmodel.GroupPresenceViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    onNavigateBack: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit = {},
    chatViewModel: ChatDetailViewModel = hiltViewModel(),
    presenceViewModel: GroupPresenceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val chatUiState by chatViewModel.uiState.collectAsStateWithLifecycle()
    val presenceUiState by presenceViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    var showOverflowMenu by remember { mutableStateOf(false) }
    var showGroupInfo by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("Belästigung") }
    var reportDetails by remember { mutableStateOf("") }

    val chatType = chatUiState.chatType
    val chatTitle = chatUiState.conversationName

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            chatViewModel.onImageSelected(it.toString())
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val tempUri = saveBitmapToTempUri(context, it)
            tempUri?.let { uri ->
                chatViewModel.onImageSelected(uri.toString())
            }
        }
    }

    LaunchedEffect(chatId) {
        chatViewModel.loadConversation(chatId)
    }

    LaunchedEffect(chatId, chatType) {
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

    LaunchedEffect(chatUiState.actionSuccessMessage) {
        chatUiState.actionSuccessMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            chatViewModel.dismissMessage()
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
                            onHeaderClick = { showGroupInfo = true }
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    val targetId = chatUiState.targetUserId
                                    if (targetId.isNotBlank()) {
                                        onNavigateToUserProfile(targetId)
                                    }
                                }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PurplePrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = chatUiState.conversationInitial.ifBlank { chatTitle.take(1).uppercase() },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PurplePrimaryLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = chatTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (chatUiState.isBlocked) "Blockiert" else if (chatUiState.isOnline) "Online" else "Tippe für Profil",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (chatUiState.isBlocked) Color(0xFFEF4444) else if (chatUiState.isOnline) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
                        IconButton(onClick = { showGroupInfo = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Gruppeninfo",
                                tint = PurplePrimaryLight
                            )
                        }
                        IconButton(onClick = { presenceViewModel.toggleParticipantSheet() }) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "Teilnehmer anzeigen",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Box {
                            IconButton(onClick = { showOverflowMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Optionen",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            DropdownMenu(
                                expanded = showOverflowMenu,
                                onDismissRequest = { showOverflowMenu = false },
                                modifier = Modifier.background(DarkSurface)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Profil ansehen") },
                                    onClick = {
                                        showOverflowMenu = false
                                        val targetId = chatUiState.targetUserId
                                        if (targetId.isNotBlank()) onNavigateToUserProfile(targetId)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = PurplePrimaryLight)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (chatUiState.isBlocked) "Nutzer entblockieren" else "Nutzer blockieren") },
                                    onClick = {
                                        showOverflowMenu = false
                                        chatViewModel.toggleBlockUser()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444))
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Nutzer melden", color = Color(0xFFEF4444)) },
                                    onClick = {
                                        showOverflowMenu = false
                                        chatViewModel.openReportDialog()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Report, contentDescription = null, tint = Color(0xFFEF4444))
                                    }
                                )
                            }
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
                        onSeekVoice = { posMs -> chatViewModel.seekVoiceMessage(posMs) },
                        onSenderClick = { senderId ->
                            if (senderId.isNotBlank()) {
                                onNavigateToUserProfile(senderId)
                            }
                        }
                    )
                }
            }

            if (chatUiState.isAttachmentSheetVisible) {
                AttachmentOptionsSheet(
                    onOptionGallery = {
                        galleryLauncher.launch("image/*")
                    },
                    onOptionCamera = {
                        cameraLauncher.launch(null)
                    },
                    onDismiss = chatViewModel::closeAttachmentSheet
                )
            }

            chatUiState.selectedImageUri?.let { imageUri ->
                ImagePreviewSendDialog(
                    imageUri = imageUri,
                    caption = chatUiState.imageCaption,
                    onCaptionChange = chatViewModel::onImageCaptionChanged,
                    isCompressing = chatUiState.isCompressingImage,
                    onSend = { chatViewModel.sendSelectedImage(context) },
                    onDismiss = chatViewModel::clearSelectedImage
                )
            }

            if (showGroupInfo) {
                GroupInfoSheet(
                    title = chatTitle,
                    description = if (chatUiState.chatType == ChatType.PUBLIC_CITY) "Öffentlicher Stadt- und Gruppenchat" else "Gruppe",
                    avatarUrl = null,
                    memberCount = presenceUiState.totalMembersCount.coerceAtLeast(presenceUiState.totalOnlineCount).coerceAtLeast(1),
                    canAddMembers = chatUiState.chatType != ChatType.PUBLIC_CITY,
                    onAddMemberClick = {
                        showGroupInfo = false
                        presenceViewModel.toggleParticipantSheet()
                    },
                    onLeaveGroupClick = {
                        showGroupInfo = false
                        chatViewModel.deleteCurrentChat(onNavigateBack)
                    },
                    onDismiss = { showGroupInfo = false }
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

            if (chatUiState.isBlockConfirmationDialogVisible) {
                AlertDialog(
                    onDismissRequest = { chatViewModel.closeBlockConfirmationDialog() },
                    title = { Text("Nutzer blockieren") },
                    text = { Text("Möchtest du diesen Nutzer wirklich blockieren? Er kann dir dann keine weiteren Nachrichten mehr senden.") },
                    confirmButton = {
                        Button(
                            onClick = { chatViewModel.confirmBlockUser() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                        ) {
                            Text("Blockieren", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { chatViewModel.closeBlockConfirmationDialog() }) {
                            Text("Abbrechen")
                        }
                    },
                    containerColor = DarkSurface
                )
            }

            if (chatUiState.isReportDialogVisible) {
                val reportOptions = listOf("Belästigung", "Spam / Werbung", "Unangemessene Inhalte", "Fake-Profil", "Sonstiges")
                AlertDialog(
                    onDismissRequest = { chatViewModel.closeReportDialog() },
                    title = { Text("Nutzer melden") },
                    text = {
                        Column {
                            Text("Bitte wähle den Grund für die Meldung:", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(10.dp))
                            reportOptions.forEach { option ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { reportReason = option }
                                        .padding(vertical = 4.dp)
                                ) {
                                    RadioButton(
                                        selected = reportReason == option,
                                        onClick = { reportReason = option },
                                        colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = option, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = reportDetails,
                                onValueChange = { reportDetails = it },
                                label = { Text("Optionale Details") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                maxLines = 3
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { chatViewModel.reportUser(reportReason, reportDetails) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                        ) {
                            Text("Meldung senden", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { chatViewModel.closeReportDialog() }) {
                            Text("Abbrechen")
                        }
                    },
                    containerColor = DarkSurface
                )
            }
        }
    }
}

private fun saveBitmapToTempUri(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val file = File(context.cacheDir, "camera_capture_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        Uri.fromFile(file)
    } catch (e: Exception) {
        null
    }
}
