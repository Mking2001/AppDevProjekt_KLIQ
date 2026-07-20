package com.kliq.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Immutable UI-State für die Chat-Listen-Übersicht.
 * Unterscheidet zwischen öffentlichen Gruppen-Chats und
 * privaten Direktnachrichten über den [selectedTab]-Zustand.
 *
 * @param publicChats Liste aller öffentlichen Gruppen-Chats.
 * @param privateChats Liste aller privaten Einzelgespräche.
 * @param selectedTab Aktuell ausgewählter Tab (PUBLIC oder PRIVATE).
 * @param isLoading Ladezustand für Skeleton-/Shimmer-Anzeige.
 */
data class ChatListUiState(
    val publicChats: List<ChatConversation> = emptyList(),
    val privateChats: List<ChatConversation> = emptyList(),
    val selectedTab: ChatType = ChatType.PUBLIC,
    val isLoading: Boolean = false
)

/**
 * ViewModel für die Chat-Listen-Übersicht.
 * Verwaltet den Tab-Zustand und stellt Platzhalter-Daten
 * für die Scaffolding-Phase bereit.
 *
 * Folgt strikt dem MVVM-Pattern:
 * - Kein Compose/Android-Import
 * - Immutable State via [StateFlow]
 * - Intent-basierte Actions
 */
@HiltViewModel
class ChatListViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    /**
     * Wechselt den aktiven Tab zwischen öffentlich und privat.
     * @param type Der Ziel-Chat-Typ.
     */
    fun onTabSelected(type: ChatType) {
        _uiState.update { it.copy(selectedTab = type) }
    }

    /**
     * Lädt Platzhalter-Daten für beide Chat-Kategorien.
     * Wird in der finalen Implementierung durch Repository-Aufrufe
     * mit echten API-/Datenbankdaten ersetzt.
     */
    private fun loadMockData() {
        _uiState.update { state ->
            state.copy(
                publicChats = listOf(
                    ChatConversation(
                        id = "pub_1",
                        name = "Afterwork Köln",
                        lastMessage = "Heute ab 20 Uhr im Bootshaus! 🎶",
                        timestamp = "14:32",
                        avatarInitial = "A",
                        unreadCount = 5,
                        chatType = ChatType.PUBLIC
                    ),
                    ChatConversation(
                        id = "pub_2",
                        name = "Festival Crew 2026",
                        lastMessage = "Hat jemand noch ein Zelt übrig?",
                        timestamp = "12:15",
                        avatarInitial = "F",
                        unreadCount = 12,
                        chatType = ChatType.PUBLIC
                    ),
                    ChatConversation(
                        id = "pub_3",
                        name = "Techno Düsseldorf",
                        lastMessage = "Line-up steht! Schaut mal rein 👀",
                        timestamp = "Gestern",
                        avatarInitial = "T",
                        unreadCount = 0,
                        chatType = ChatType.PUBLIC
                    ),
                    ChatConversation(
                        id = "pub_4",
                        name = "Rooftop Sessions",
                        lastMessage = "Nächste Session am Samstag",
                        timestamp = "Gestern",
                        avatarInitial = "R",
                        unreadCount = 3,
                        chatType = ChatType.PUBLIC
                    ),
                    ChatConversation(
                        id = "pub_5",
                        name = "Konzertgänger NRW",
                        lastMessage = "Tickets für Rammstein sind raus!",
                        timestamp = "Mo",
                        avatarInitial = "K",
                        unreadCount = 0,
                        chatType = ChatType.PUBLIC
                    )
                ),
                privateChats = listOf(
                    ChatConversation(
                        id = "priv_1",
                        name = "Lisa W.",
                        lastMessage = "Treffen wir uns vor dem Eingang?",
                        timestamp = "15:08",
                        avatarInitial = "L",
                        unreadCount = 2,
                        chatType = ChatType.PRIVATE,
                        isOnline = true
                    ),
                    ChatConversation(
                        id = "priv_2",
                        name = "Max K.",
                        lastMessage = "War ein geiler Abend! 🔥",
                        timestamp = "13:45",
                        avatarInitial = "M",
                        unreadCount = 0,
                        chatType = ChatType.PRIVATE,
                        isOnline = true
                    ),
                    ChatConversation(
                        id = "priv_3",
                        name = "Anna M.",
                        lastMessage = "Danke für die Einladung!",
                        timestamp = "11:20",
                        avatarInitial = "A",
                        unreadCount = 1,
                        chatType = ChatType.PRIVATE,
                        isOnline = false
                    ),
                    ChatConversation(
                        id = "priv_4",
                        name = "Tom S.",
                        lastMessage = "Klar, bin dabei 👍",
                        timestamp = "Gestern",
                        avatarInitial = "T",
                        unreadCount = 0,
                        chatType = ChatType.PRIVATE,
                        isOnline = false
                    ),
                    ChatConversation(
                        id = "priv_5",
                        name = "Sarah B.",
                        lastMessage = "Schick mir mal den Link",
                        timestamp = "Gestern",
                        avatarInitial = "S",
                        unreadCount = 0,
                        chatType = ChatType.PRIVATE,
                        isOnline = true
                    )
                )
            )
        }
    }

    fun onChatDeleted(chatId: String) {
        _uiState.update { state ->
            state.copy(
                publicChats = state.publicChats.filter { it.id != chatId },
                privateChats = state.privateChats.filter { it.id != chatId }
            )
        }
    }

    fun onChatArchived(chatId: String) {
        // Für dieses Beispiel verhält sich Archivieren visuell wie Löschen
        _uiState.update { state ->
            state.copy(
                publicChats = state.publicChats.filter { it.id != chatId },
                privateChats = state.privateChats.filter { it.id != chatId }
            )
        }
    }

    fun onUndoDelete(chat: ChatConversation) {
        _uiState.update { state ->
            if (chat.chatType == ChatType.PUBLIC) {
                state.copy(publicChats = listOf(chat) + state.publicChats)
            } else {
                state.copy(privateChats = listOf(chat) + state.privateChats)
            }
        }
    }
}
