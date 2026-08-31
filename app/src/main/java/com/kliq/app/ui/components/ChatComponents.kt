package com.kliq.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.kliq.app.util.ensureMinTouchTarget
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.model.MessageType
import com.kliq.app.data.model.toChatListItem
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryDark
import com.kliq.app.ui.theme.PurplePrimaryLight

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatListItem(
    item: com.kliq.app.data.model.ChatListItem,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .then(
                    if (item.chatType == ChatType.PUBLIC_CITY) {
                        Modifier.border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(PurplePrimaryLight, PurplePrimary)
                            ),
                            shape = CircleShape
                        )
                    } else Modifier
                )
                .padding(if (item.chatType == ChatType.PUBLIC_CITY) 3.dp else 0.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.avatarInitial,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            if (item.chatType == ChatType.PRIVATE && item.userStatus == com.kliq.app.data.model.UserStatus.ONLINE) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (item.unreadCount > 0) FontWeight.Bold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            val previewDisplay = if (item.chatType == ChatType.PUBLIC_CITY && !item.lastMessage.senderName.isNullOrBlank() && !item.lastMessage.text.startsWith("${item.lastMessage.senderName}:")) {
                "${item.lastMessage.senderName}: ${item.lastMessage.text}"
            } else {
                item.lastMessage.text
            }
            Text(
                text = previewDisplay,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.lastMessage.timestampIso.take(16).replace("T", " "),
                style = MaterialTheme.typography.labelSmall,
                color = if (item.unreadCount > 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            if (item.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (item.unreadCount > 99) "99+"
                        else item.unreadCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    conversation: ChatConversation,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ChatListItem(
        item = conversation.toChatListItem(),
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier
    )
}


@Composable
fun VoiceMessageBubble(
    message: ChatMessage,
    isPlaying: Boolean = false,
    playbackPositionMs: Long = 0L,
    playbackDurationMs: Long = 0L,
    onPlayPauseClick: () -> Unit = {},
    onSeek: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val durationMs = if (message.audioDurationMs > 0) message.audioDurationMs else if (playbackDurationMs > 0) playbackDurationMs else 1000L
    val currentPositionMs = if (isPlaying) playbackPositionMs else 0L
    val progress = (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)

    val senderPrefix = if (message.isMine) "Deine Sprachnachricht" else "Sprachnachricht von ${message.senderName}"
    val talkBackVoiceDesc = "$senderPrefix, Dauer ${formatDurationMs(durationMs)}${if (isPlaying) ", wird abgespielt" else ", pausiert"}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = talkBackVoiceDesc
                stateDescription = if (isPlaying) "Wird abgespielt" else "Pausiert"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (message.isMine) Color.White.copy(alpha = 0.25f) else PurplePrimary.copy(alpha = 0.2f))
        ) {
            KliqIcon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Sprachnachricht pausieren" else "Sprachnachricht abspielen",
                size = KliqIconSize.MEDIUM,
                category = KliqIconCategory.ACTION,
                tint = if (message.isMine) Color.White else PurplePrimaryLight
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎤 Sprachnachricht",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (message.isMine) Color.White else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatDurationMs(if (isPlaying) currentPositionMs else durationMs),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (message.isMine) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = progress,
                onValueChange = { newProgress ->
                    val targetMs = (newProgress * durationMs).toLong()
                    onSeek?.invoke(targetMs)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                colors = SliderDefaults.colors(
                    thumbColor = if (message.isMine) Color.White else PurplePrimaryLight,
                    activeTrackColor = if (message.isMine) Color.White else PurplePrimary,
                    inactiveTrackColor = if (message.isMine) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )
        }
    }
}

fun formatDurationMs(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onImageClick: ((String) -> Unit)? = null,
    isPlayingVoice: Boolean = false,
    voicePlaybackPositionMs: Long = 0L,
    voicePlaybackDurationMs: Long = 0L,
    onPlayPauseVoice: (() -> Unit)? = null,
    onSeekVoice: ((Long) -> Unit)? = null,
    onSenderClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isFullscreenVisible by remember { mutableStateOf(false) }

    val isVoiceMessage = message.messageType == MessageType.VOICE
    val isImageMessage = message.messageType == MessageType.IMAGE || (!isVoiceMessage && !message.mediaUrl.isNullOrBlank())

    val talkBackBubbleText = when {
        isVoiceMessage -> "Sprachnachricht von ${if (message.isMine) "dir" else message.senderName}"
        isImageMessage -> "Fotonachricht von ${if (message.isMine) "dir" else message.senderName}${if (message.text.isNotBlank() && message.text != "📷 Foto") ": ${message.text}" else ""}"
        else -> "Nachricht von ${if (message.isMine) "dir" else message.senderName}: ${message.text}"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = talkBackBubbleText
            },
        horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start
    ) {

        if (!message.isMine) {
            Text(
                text = message.senderName,
                style = MaterialTheme.typography.labelSmall,
                color = PurplePrimaryLight,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        bottom = 4.dp
                    )
                    .clickable(enabled = onSenderClick != null && message.senderUserId.isNotBlank()) {
                        onSenderClick?.invoke(message.senderUserId)
                    }
            )
        }

        Surface(
            modifier = Modifier.widthIn(max = 280.dp),

            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isMine) 16.dp else 4.dp,
                bottomEnd = if (message.isMine) 4.dp else 16.dp
            ),
            color = if (message.isMine) {
                PurplePrimary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            tonalElevation = if (message.isMine) 0.dp else 2.dp
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = if (isImageMessage) 6.dp else 14.dp,
                    vertical = if (isImageMessage) 6.dp else 10.dp
                )
            ) {
                if (isVoiceMessage) {
                    VoiceMessageBubble(
                        message = message,
                        isPlaying = isPlayingVoice,
                        playbackPositionMs = voicePlaybackPositionMs,
                        playbackDurationMs = voicePlaybackDurationMs,
                        onPlayPauseClick = { onPlayPauseVoice?.invoke() },
                        onSeek = onSeekVoice
                    )
                } else if (isImageMessage && !message.mediaUrl.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(message.aspectRatio.coerceIn(0.5f, 2.0f))
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                onImageClick?.invoke(message.mediaUrl)
                                isFullscreenVisible = true
                            }
                    ) {
                        AsyncImage(
                            model = message.thumbnailUrl ?: message.mediaUrl,
                            contentDescription = "Foto-Nachricht",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (message.status == MessageStatus.SENT && message.isMine) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(28.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                            }
                        }
                    }

                    if (message.text.isNotBlank() && message.text != "📷 Foto") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (message.isMine) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                } else {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (message.isMine) {
                            Color.White
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = if (isImageMessage) 4.dp else 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val timeText = if (message.timestampIso.length >= 16) {
                        message.timestampIso.substring(11, 16)
                    } else {
                        message.timestampIso
                    }
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (message.isMine) {
                            Color.White.copy(alpha = 0.75f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        }
                    )
                    if (message.isMine) {
                        val iconVector = when (message.status) {
                            MessageStatus.SENT -> Icons.Default.Done
                            MessageStatus.DELIVERED, MessageStatus.READ -> Icons.Default.DoneAll
                        }
                        val targetTint = when (message.status) {
                            MessageStatus.READ -> PurplePrimaryLight
                            MessageStatus.DELIVERED -> Color.White.copy(alpha = 0.95f)
                            MessageStatus.SENT -> Color.White.copy(alpha = 0.6f)
                        }
                        val animatedTint by animateColorAsState(
                            targetValue = targetTint,
                            animationSpec = tween(durationMillis = 300),
                            label = "StatusTintAnimation"
                        )
                        val statusDescription = when (message.status) {
                            MessageStatus.READ -> "Gelesen"
                            MessageStatus.DELIVERED -> "Zugestellt"
                            MessageStatus.SENT -> "Gesendet"
                        }
                        Icon(
                            imageVector = iconVector,
                            contentDescription = statusDescription,
                            tint = animatedTint,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }

    if (isFullscreenVisible && !message.mediaUrl.isNullOrBlank()) {
        FullscreenImageViewerDialog(
            imageUrl = message.mediaUrl,
            onDismiss = { isFullscreenVisible = false }
        )
    }
}

@Composable
fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttachClick: (() -> Unit)? = null,
    isRecordingVoice: Boolean = false,
    recordingDurationMs: Long = 0L,
    recordingAmplitudes: List<Float> = emptyList(),
    onStartRecordVoice: (() -> Unit)? = null,
    onStopAndSendRecordVoice: (() -> Unit)? = null,
    onCancelRecordVoice: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val hasText = value.isNotBlank()

    val sendButtonColor by animateColorAsState(
        targetValue = if (hasText || isRecordingVoice) PurplePrimary else PurplePrimary.copy(alpha = 0.3f),
        label = "sendButtonColor"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                PurplePrimary.copy(alpha = 0.0f),
                                PurplePrimaryLight.copy(alpha = 0.5f),
                                PurplePrimary.copy(alpha = 0.0f)
                            )
                        )
                    )
            )

            if (isRecordingVoice) {
                val infiniteTransition = rememberInfiniteTransition(label = "PulseTransition")
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "PulseAlpha"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = { onCancelRecordVoice?.invoke() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Aufnahme verwerfen",
                                tint = Color(0xFFEF4444)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = pulseAlpha))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = formatDurationMs(recordingDurationMs),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            val amplitudes = if (recordingAmplitudes.isNotEmpty()) recordingAmplitudes.takeLast(16) else listOf(0.2f, 0.4f, 0.7f, 0.3f, 0.6f, 0.8f, 0.4f, 0.2f)
                            amplitudes.forEach { amp ->
                                val h = (amp * 20.dp.value).coerceIn(4f, 24f)
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(h.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(PurplePrimaryLight)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { onStopAndSendRecordVoice?.invoke() },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = PurplePrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Sprachnachricht senden",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (onAttachClick != null) {
                        IconButton(
                            onClick = onAttachClick,
                            modifier = Modifier
                                .ensureMinTouchTarget(48.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Foto oder Medien anhaengen",
                                tint = PurplePrimaryLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                text = "Nachricht schreiben…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            cursorColor = PurplePrimary,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = { if (hasText) onSend() }
                        )
                    )

                    if (hasText || onStartRecordVoice == null) {
                        IconButton(
                            onClick = onSend,
                            enabled = hasText,
                            modifier = Modifier
                                .ensureMinTouchTarget(48.dp)
                                .clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = sendButtonColor,
                                contentColor = Color.White,
                                disabledContainerColor = PurplePrimary.copy(alpha = 0.3f),
                                disabledContentColor = Color.White.copy(alpha = 0.5f)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Nachricht senden",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { onStartRecordVoice() },
                            modifier = Modifier
                                .ensureMinTouchTarget(48.dp)
                                .clip(CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = PurplePrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Sprachnachricht aufnehmen",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentOptionsSheet(
    onOptionGallery: () -> Unit,
    onOptionCamera: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Medien teilen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onDismiss()
                        onOptionGallery()
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = PurplePrimaryLight
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Galerie",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Foto aus Mediathek auswählen",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onDismiss()
                        onOptionCamera()
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = PurplePrimaryLight
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Kamera",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Neues Foto aufnehmen",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ImageAttachmentPreviewDialog(
    imageUri: String,
    caption: String,
    onCaptionChange: (String) -> Unit,
    isUploading: Boolean = false,
    onSend: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Foto-Vorschau",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss, enabled = !isUploading) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Abbrechen",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Ausgewähltes Foto",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isUploading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = PurplePrimaryLight)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Bild wird komprimiert…",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = caption,
                    onValueChange = onCaptionChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Bildunterschrift (optional)…",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss, enabled = !isUploading) {
                        Text("Abbrechen", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onSend,
                        enabled = !isUploading,
                        modifier = Modifier
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PurplePrimary)
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Senden",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Senden",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FullscreenImageViewerDialog(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Vollbild Foto",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Schließen",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ChatDateDivider(
    dateText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
        )
        Text(
            text = dateText,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
        )
    }
}

@Composable
fun CityChatHeaderBanner(
    suggestedCity: com.kliq.app.data.util.CityChatConfig?,
    onSwitchCityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestedCity == null) return

    androidx.compose.material3.Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(PurplePrimaryLight.copy(alpha = 0.7f), PurplePrimary.copy(alpha = 0.3f))
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📍",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Du bist in ${suggestedCity.cityRegion}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Zu „${suggestedCity.cityRegion}“ wechseln?",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            androidx.compose.material3.Button(
                onClick = onSwitchCityClick,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = PurplePrimary
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Wechseln",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CityChatSwitcherSheet(
    supportedCities: List<com.kliq.app.data.util.CityChatConfig>,
    onCitySelected: (com.kliq.app.data.util.CityChatConfig) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, top = 8.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "Öffentlichen Stadt-Chat wählen",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tritt dem Chat deiner aktuellen Stadt oder Partymetropole bei:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            supportedCities.forEach { city ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                        .clickable { onCitySelected(city) }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = city.avatarInitial,
                                style = MaterialTheme.typography.titleMedium,
                                color = PurplePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = city.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${city.defaultOnlineCount} Feiernde aktiv",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "Beitreten",
                        style = MaterialTheme.typography.labelSmall,
                        color = PurplePrimaryLight,
                        fontWeight = FontWeight.Bold
                    )
                }
                androidx.compose.material3.Divider(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
fun ImagePreviewSendDialog(
    imageUri: String,
    caption: String,
    onCaptionChange: (String) -> Unit,
    isCompressing: Boolean,
    onSend: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { if (!isCompressing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isCompressing
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Abbrechen",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Vorschau",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // Image Preview
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Ausgewähltes Bild",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )

                    if (isCompressing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PurplePrimaryLight)
                        }
                    }
                }

                // Caption and Send Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = caption,
                        onValueChange = onCaptionChange,
                        placeholder = { Text("Bildunterschrift hinzufügen...", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        ),
                        shape = RoundedCornerShape(24.dp),
                        enabled = !isCompressing
                    )

                    IconButton(
                        onClick = onSend,
                        enabled = !isCompressing,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(PurplePrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Senden",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog zum Erstellen einer neuen Gruppe im WhatsApp-Stil.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupDialog(
    availableUsers: List<com.kliq.app.data.local.entities.UserEntity>,
    onPickImageGallery: () -> Unit,
    onPickImageCamera: () -> Unit,
    groupImageUri: String?,
    onCreateGroup: (name: String, description: String, selectedUserIds: List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var groupDescription by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedUserIds by remember { mutableStateOf(setOf<String>()) }

    val filteredUsers = remember(availableUsers, searchQuery) {
        if (searchQuery.isBlank()) availableUsers
        else availableUsers.filter {
            it.username.contains(searchQuery, ignoreCase = true) ||
            it.hometown?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Neue Gruppe erstellen",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Schließen", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Group Avatar Picker & Name Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .clickable { onPickImageGallery() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!groupImageUri.isNullOrBlank()) {
                            AsyncImage(
                                model = groupImageUri,
                                contentDescription = "Gruppenbild",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Bild wählen",
                                tint = PurplePrimaryLight,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = groupName,
                            onValueChange = { if (it.length <= 35) groupName = it },
                            label = { Text("Gruppenname *") },
                            placeholder = { Text("z.B. VIP Night Crew") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Group Description
                OutlinedTextField(
                    value = groupDescription,
                    onValueChange = { if (it.length <= 120) groupDescription = it },
                    label = { Text("Gruppenbeschreibung / Thema") },
                    placeholder = { Text("z.B. Treffen vor dem Clubbing") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Member Selection Section
                Text(
                    text = "Mitglieder hinzufügen (${selectedUserIds.size} ausgewählt)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimaryLight
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Kontakte / Personen suchen…") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Member List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (filteredUsers.isEmpty()) {
                        item {
                            Text(
                                text = "Keine weiteren Kontakte gefunden",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(items = filteredUsers, key = { it.id }) { user ->
                            val isSelected = selectedUserIds.contains(user.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedUserIds = if (isSelected) selectedUserIds - user.id else selectedUserIds + user.id
                                    }
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(PurplePrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.username.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = PurplePrimaryLight
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = user.username,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    if (!user.hometown.isNullOrBlank()) {
                                        Text(
                                            text = user.hometown,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        selectedUserIds = if (checked) selectedUserIds + user.id else selectedUserIds - user.id
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = PurplePrimary,
                                        checkmarkColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Abbrechen", color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (groupName.isNotBlank()) {
                                onCreateGroup(groupName.trim(), groupDescription.trim(), selectedUserIds.toList())
                            }
                        },
                        enabled = groupName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Gruppe erstellen", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * WhatsApp-ähnliche Gruppen-Info-Ansicht.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupInfoSheet(
    title: String,
    description: String?,
    avatarUrl: String?,
    memberCount: Int,
    members: List<com.kliq.app.data.local.entities.UserEntity> = emptyList(),
    canAddMembers: Boolean = true,
    onAddMemberClick: () -> Unit = {},
    onLeaveGroupClick: () -> Unit = {},
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Large Group Avatar
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(PurplePrimary.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                if (!avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = title.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = PurplePrimaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            if (!description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "$memberCount Mitglieder",
                style = MaterialTheme.typography.labelMedium,
                color = PurplePrimaryLight,
                modifier = Modifier.padding(top = 4.dp)
            )

            if (canAddMembers) {
                Spacer(modifier = Modifier.height(16.dp))

                // Action: Add members
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                        .clickable { onAddMemberClick() }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = PurplePrimaryLight)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Mitglieder hinzufügen", fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Member list
            if (members.isNotEmpty()) {
                Text(
                    text = "Teilnehmer",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(items = members, key = { it.id }) { user ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PurplePrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.username.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = PurplePrimaryLight
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(user.username, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Leave / Delete Group Button
            Button(
                onClick = {
                    onDismiss()
                    onLeaveGroupClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gruppe verlassen & löschen", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
