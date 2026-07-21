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

data class ChatListUiState(
    val publicChats: List<ChatConversation> = emptyList(),
    val privateChats: List<ChatConversation> = emptyList(),
    val selectedTab: ChatType = ChatType.PUBLIC_CITY,
    val isLoading: Boolean = false
)

@HiltViewModel
class ChatListViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    fun onTabSelected(type: ChatType) {
        _uiState.update { it.copy(selectedTab = type) }
    }

    private fun loadMockData() {
        val now = System.currentTimeMillis()
        _uiState.update { state ->
            state.copy(
                publicChats = listOf(
                    ChatConversation(
                        id = "pub_1",
                        name = "Berlin - Tonight",
                        cityRegion = "Berlin",
                        lastMessageText = "Heute ab 23 Uhr im Watergate! 🎶",
                        lastMessageTimestampMs = now - 600000L,
                        avatarInitial = "B",
                        unreadCount = 5,
                        chatType = ChatType.PUBLIC_CITY
                    ),
                    ChatConversation(
                        id = "pub_2",
                        name = "München - Party Radar",
                        cityRegion = "München",
                        lastMessageText = "Hat jemand noch Tickets für Rote Sonne?",
                        lastMessageTimestampMs = now - 3600000L,
                        avatarInitial = "M",
                        unreadCount = 12,
                        chatType = ChatType.PUBLIC_CITY
                    ),
                    ChatConversation(
                        id = "pub_3",
                        name = "Hamburg - Reeperbahn",
                        cityRegion = "Hamburg",
                        lastMessageText = "Line-up steht! Schaut mal rein 👀",
                        lastMessageTimestampMs = now - 86400000L,
                        avatarInitial = "H",
                        unreadCount = 0,
                        chatType = ChatType.PUBLIC_CITY
                    )
                ),
                privateChats = listOf(
                    ChatConversation(
                        id = "priv_1",
                        name = "Lisa W.",
                        lastMessageText = "Treffen wir uns vor dem Eingang?",
                        lastMessageTimestampMs = now - 900000L,
                        avatarInitial = "L",
                        unreadCount = 2,
                        chatType = ChatType.PRIVATE,
                        isOnline = true
                    ),
                    ChatConversation(
                        id = "priv_2",
                        name = "Max K.",
                        lastMessageText = "War ein geiler Abend! 🔥",
                        lastMessageTimestampMs = now - 7200000L,
                        avatarInitial = "M",
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
        _uiState.update { state ->
            state.copy(
                publicChats = state.publicChats.filter { it.id != chatId },
                privateChats = state.privateChats.filter { it.id != chatId }
            )
        }
    }

    fun onUndoDelete(chat: ChatConversation) {
        _uiState.update { state ->
            if (chat.chatType == ChatType.PUBLIC_CITY) {
                state.copy(publicChats = listOf(chat) + state.publicChats)
            } else {
                state.copy(privateChats = listOf(chat) + state.privateChats)
            }
        }
    }
}
