package com.kliq.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.data.model.DirectMessage
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.viewmodel.PrivateChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// High-Contrast Dark-Mode Violet Colors
private val PurpleAccentPrimary = Color(0xFF8A2BE2) // BlueViolet (#8A2BE2)
private val PurpleAccentSecondary = Color(0xFF7F00FF) // Electric Purple (#7F00FF)
private val PurpleAccentLight = Color(0xFFBB86FC)
private val HighContrastBackground = Color(0xFF0F0B15)
private val SurfaceDark = Color(0xFF1A1523)
private val SurfaceVariantDark = Color(0xFF2D2640)
private val HighContrastText = Color(0xFFF0ECFA)
private val SubduedText = Color(0xFFAAA4C0)

/**
 * 1-zu-1 Private Chat Screen mit High-Contrast Lila Dark-Mode Design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateChatScreen(
    currentUserId: String,
    receiverId: String,
    receiverName: String,
    onNavigateBack: () -> Unit,
    viewModel: PrivateChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(currentUserId, receiverId) {
        viewModel.initConversation(
            currentUserId = currentUserId,
            receiverId = receiverId,
            receiverName = receiverName
        )
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = HighContrastBackground,
        topBar = {
            PrivateChatTopBar(
                receiverName = uiState.receiverName.ifBlank { receiverName },
                isOnline = uiState.isOnline,
                isEncrypted = uiState.isEncryptedSession,
                onToggleEncryption = viewModel::toggleEncryption,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            PrivateChatInputBar(
                value = uiState.currentInput,
                onValueChange = viewModel::onInputChanged,
                onSend = { viewModel.sendMessage(receiverId = receiverId) },
                modifier = Modifier.imePadding()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(HighContrastBackground)
        ) {
            if (uiState.isLoading && uiState.messages.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PurpleAccentPrimary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.messages, key = { it.messageId }) { message ->
                        DirectMessageBubble(
                            message = message,
                            isMine = message.isMine || message.senderId == currentUserId
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom High-Contrast Top-Bar mit E2E-Verschluesselungs-Badge und Online-Status.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrivateChatTopBar(
    receiverName: String,
    isOnline: Boolean,
    isEncrypted: Boolean,
    onToggleEncryption: (Boolean) -> Unit,
    onNavigateBack: () -> Unit
) {
    val initial = receiverName.take(1).uppercase(Locale.getDefault())

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PurpleAccentPrimary, PurpleAccentSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = receiverName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HighContrastText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) Color(0xFF22C55E) else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOnline) "Online" else "Offline",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOnline) Color(0xFF22C55E) else SubduedText
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Zurueck",
                    tint = HighContrastText
                )
            }
        },
        actions = {
            Surface(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onToggleEncryption(!isEncrypted) },
                color = if (isEncrypted) SurfaceVariantDark else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = if (isEncrypted) PurpleAccentPrimary else SubduedText,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Verschluesselung",
                        tint = if (isEncrypted) PurpleAccentLight else SubduedText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isEncrypted) "E2E Aktiv" else "Inaktiv",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isEncrypted) PurpleAccentLight else SubduedText,
                        fontSize = 11.sp
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SurfaceDark,
            titleContentColor = HighContrastText
        )
    )
}

/**
 * Sprechblase fuer 1-zu-1 Nachrichten mit Richtungs- und Statusanzeige.
 */
@Composable
private fun DirectMessageBubble(
    message: DirectMessage,
    isMine: Boolean
) {
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleShape = if (isMine) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
    }

    val bubbleBackground = if (isMine) {
        Brush.horizontalGradient(
            colors = listOf(PurpleAccentPrimary, PurpleAccentSecondary)
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(SurfaceVariantDark, SurfaceVariantDark)
        )
    }

    val formattedTime = formatTimestamp(message.timestamp)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(bubbleShape)
                .background(bubbleBackground)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (message.isEncrypted) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Verschluesselt",
                        tint = if (isMine) Color(0xFFE9D5FF) else PurpleAccentLight,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = message.encryptionAlgorithm,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isMine) Color(0xFFE9D5FF) else PurpleAccentLight,
                        fontSize = 10.sp
                    )
                }
            }

            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = HighContrastText,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isMine) Color(0xFFE9D5FF) else SubduedText,
                    fontSize = 10.sp
                )

                if (isMine) {
                    Spacer(modifier = Modifier.width(4.dp))
                    DeliveryStatusIcon(status = message.deliveryStatus)
                }
            }
        }
    }
}

/**
 * Status-Icon fuer Sende- & Gelesen-Status der Nachricht.
 */
@Composable
private fun DeliveryStatusIcon(status: MessageStatus) {
    val icon = when (status) {
        MessageStatus.SENT -> Icons.Filled.Check
        MessageStatus.DELIVERED, MessageStatus.READ -> Icons.Filled.DoneAll
    }

    val tint = when (status) {
        MessageStatus.READ -> PurpleAccentLight
        MessageStatus.DELIVERED -> Color(0xFFE9D5FF)
        MessageStatus.SENT -> Color(0xFFD8B4FE)
    }

    Icon(
        imageVector = icon,
        contentDescription = status.name,
        tint = tint,
        modifier = Modifier.size(14.dp)
    )
}

/**
 * High-Contrast Eingabeleiste mit Lila Akzent-Sendebutton.
 */
@Composable
private fun PrivateChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceDark,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = "Nachricht schreiben...",
                        color = SubduedText
                    )
                },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    focusedBorderColor = PurpleAccentPrimary,
                    unfocusedBorderColor = Color(0xFF3B3354),
                    focusedTextColor = HighContrastText,
                    unfocusedTextColor = HighContrastText
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() })
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                enabled = value.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (value.isNotBlank()) {
                            Brush.linearGradient(
                                colors = listOf(PurpleAccentPrimary, PurpleAccentSecondary)
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(SurfaceVariantDark, SurfaceVariantDark)
                            )
                        }
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Senden",
                    tint = if (value.isNotBlank()) Color.White else SubduedText
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
