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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.components.ChatBubble
import com.kliq.app.ui.components.ChatDateDivider
import com.kliq.app.ui.components.ChatInputBar

/**
 * Chat-Detail-Screen mit Nachrichtenverlauf und Eingabeleiste.
 *
 * Zeigt den vollständigen Chatverlauf mit richtungsabhängigen
 * Sprechblasen im Lila-Design an. Eigene Nachrichten erscheinen
 * rechts-ausgerichtet in PurplePrimary, fremde links in SurfaceVariant.
 *
 * @param chatId ID der anzuzeigenden Konversation.
 * @param onNavigateBack Callback für die Zurück-Navigation.
 * @param viewModel Hilt-injiziertes [ChatDetailViewModel].
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

    // Konversation laden und bei neuen Nachrichten ans Ende scrollen
    LaunchedEffect(chatId) {
        viewModel.loadConversation(chatId)
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ChatDetailTopBar(
                name = uiState.conversationName,
                initial = uiState.conversationInitial,
                isOnline = uiState.isOnline,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            ChatInputBar(
                value = uiState.currentInput,
                onValueChange = viewModel::onInputChanged,
                onSend = viewModel::onSendMessage,
                modifier = Modifier.imePadding()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = listState,
            contentPadding = PaddingValues(
                horizontal = 12.dp,
                vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.messages, key = { it.id }) { message ->
                // Optionaler Datums-Header vor der Nachricht
                if (message.dateHeader != null) {
                    ChatDateDivider(dateText = message.dateHeader)
                }
                ChatBubble(message = message)
            }
        }
    }
}

/**
 * Custom Top-App-Bar für den Chat-Detail-Screen.
 * Zeigt den Avatar mit Initiale, Chat-Namen, Online-Status
 * und einen Zurück-Pfeil für die Navigation.
 *
 * @param name Anzeigename des Chat-Partners/Gruppe.
 * @param initial Avatar-Initiale.
 * @param isOnline Online-Status des Gesprächspartners.
 * @param onNavigateBack Callback für die Zurück-Navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatDetailTopBar(
    name: String,
    initial: String,
    isOnline: Boolean,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mini-Avatar in der TopBar
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
                    if (isOnline) {
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
            IconButton(onClick = { /* Stub: Mehr-Optionen */ }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = "Mehr",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
