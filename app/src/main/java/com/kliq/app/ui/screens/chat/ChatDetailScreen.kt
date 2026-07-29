package com.kliq.app.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.components.BlockConfirmationDialog
import com.kliq.app.ui.components.ChatBubble
import com.kliq.app.ui.components.ChatDateDivider
import com.kliq.app.ui.components.ChatInputBar
import com.kliq.app.ui.components.UserReportBottomSheet
import com.kliq.app.ui.theme.DarkOnSurface
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.ErrorRed
import com.kliq.app.ui.theme.PurplePrimaryLight
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.kliq.app.ui.components.AttachmentOptionsSheet
import com.kliq.app.ui.components.ImageAttachmentPreviewDialog

/**
 * Chat-Detail-Screen mit Nachrichtenverlauf, Kontextmenü ("Nutzer melden", "Nutzer blockieren"),
 * Reporting Modal Bottom Sheet & Blockier-Bestätigungsdialog.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatId: String,
    onNavigateBack: () -> Unit,
    viewModel: ChatDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.onImageSelected(it.toString()) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val tempFile = java.io.File(context.cacheDir, "camera_cap_${System.currentTimeMillis()}.jpg")
            val fos = java.io.FileOutputStream(tempFile)
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
            viewModel.onImageSelected(tempFile.toURI().toString())
        }
    }

    LaunchedEffect(chatId) {
        viewModel.loadConversation(chatId)
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    LaunchedEffect(uiState.errorMessage, uiState.actionSuccessMessage) {
        val err = uiState.errorMessage
        val succ = uiState.actionSuccessMessage
        if (err != null) {
            snackbarHostState.showSnackbar(err)
            viewModel.dismissMessage()
        } else if (succ != null) {
            snackbarHostState.showSnackbar(succ)
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            ChatDetailTopBar(
                name = uiState.conversationName,
                initial = uiState.conversationInitial,
                isOnline = uiState.isOnline,
                isBlocked = uiState.isBlocked,
                onNavigateBack = onNavigateBack,
                onReportClick = { viewModel.openReportDialog() },
                onBlockToggleClick = { viewModel.toggleBlockUser() }
            )
        },
        bottomBar = {
            if (uiState.isBlocked) {
                BlockedChatNoticeBar(onUnblock = { viewModel.unblockUser() })
            } else {
                ChatInputBar(
                    value = uiState.currentInput,
                    onValueChange = viewModel::onInputChanged,
                    onSend = viewModel::onSendMessage,
                    onAttachClick = { viewModel.openAttachmentSheet() },
                    modifier = Modifier.imePadding()
                )
            }
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
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    if (message.dateHeader != null) {
                        ChatDateDivider(dateText = message.dateHeader)
                    }
                    ChatBubble(message = message)
                }
            }

            if (uiState.isAttachmentSheetVisible) {
                AttachmentOptionsSheet(
                    onOptionGallery = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onOptionCamera = {
                        cameraLauncher.launch(null)
                    },
                    onDismiss = { viewModel.closeAttachmentSheet() }
                )
            }

            if (uiState.selectedImageUri != null) {
                ImageAttachmentPreviewDialog(
                    imageUri = uiState.selectedImageUri!!,
                    caption = uiState.imageCaption,
                    onCaptionChange = viewModel::onImageCaptionChanged,
                    isUploading = uiState.isCompressingImage,
                    onSend = { viewModel.sendSelectedImage(context) },
                    onDismiss = { viewModel.clearSelectedImage() }
                )
            }

            if (uiState.isReportDialogVisible) {
                UserReportBottomSheet(
                    targetUsername = uiState.conversationName,
                    onDismiss = { viewModel.closeReportDialog() },
                    onSubmitReport = { reason, details ->
                        viewModel.reportUser(reason, details)
                    }
                )
            }

            if (uiState.isBlockConfirmationDialogVisible) {
                BlockConfirmationDialog(
                    username = uiState.conversationName,
                    onDismiss = { viewModel.closeBlockConfirmationDialog() },
                    onConfirmBlock = { viewModel.confirmBlockUser() }
                )
            }
        }
    }
}

@Composable
private fun BlockedChatNoticeBar(onUnblock: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Block,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nutzer blockiert",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            TextButton(onClick = onUnblock) {
                Text("Entblocken", color = PurplePrimaryLight)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatDetailTopBar(
    name: String,
    initial: String,
    isOnline: Boolean,
    isBlocked: Boolean,
    onNavigateBack: () -> Unit,
    onReportClick: () -> Unit,
    onBlockToggleClick: () -> Unit
) {
    var showOverflowMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isOnline && !isBlocked) {
                        Text(
                            text = "Online",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF22C55E)
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
            Box {
                IconButton(onClick = { showOverflowMenu = true }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = "Optionen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = showOverflowMenu,
                    onDismissRequest = { showOverflowMenu = false },
                    modifier = Modifier.background(DarkSurface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Report,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Nutzer melden", color = DarkOnSurface)
                            }
                        },
                        onClick = {
                            showOverflowMenu = false
                            onReportClick()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBlocked) "Entblocken" else "Nutzer blockieren",
                                    color = DarkOnSurface
                                )
                            }
                        },
                        onClick = {
                            showOverflowMenu = false
                            onBlockToggleClick()
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
