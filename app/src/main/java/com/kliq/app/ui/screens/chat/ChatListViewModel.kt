package com.kliq.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatListItem
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.LastMessage
import com.kliq.app.data.model.UserStatus
import com.kliq.app.data.model.toChatConversation
import com.kliq.app.data.model.toChatListItem
import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatListUiState(
    val publicChats: List<ChatListItem> = emptyList(),
    val privateChats: List<ChatListItem> = emptyList(),
    val selectedTab: ChatType = ChatType.PUBLIC_CITY,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    private var rawPublicChats: List<ChatListItem> = emptyList()
    private var rawPrivateChats: List<ChatListItem> = emptyList()
    private var currentBlockedUserIds: Set<String> = emptySet()

    init {
        loadInitialData()
        observeBlockedUsers()
    }

    private fun loadInitialData() {
        val now = System.currentTimeMillis()
        val mockPublic = listOf(
            ChatListItem(
                id = "pub_1",
                title = "Berlin - Tonight",
                cityRegion = "Berlin",
                lastMessage = LastMessage(
                    text = "Heute ab 23 Uhr im Watergate! 🎶",
                    timestampMs = now - 600000L
                ),
                avatarInitial = "B",
                unreadCount = 5,
                chatType = ChatType.PUBLIC_CITY
            ),
            ChatListItem(
                id = "pub_2",
                title = "München - Party Radar",
                cityRegion = "München",
                lastMessage = LastMessage(
                    text = "Hat jemand noch Tickets für Rote Sonne?",
                    timestampMs = now - 3600000L
                ),
                avatarInitial = "M",
                unreadCount = 12,
                chatType = ChatType.PUBLIC_CITY
            ),
            ChatListItem(
                id = "pub_3",
                title = "Hamburg - Reeperbahn",
                cityRegion = "Hamburg",
                lastMessage = LastMessage(
                    text = "Line-up steht! Schaut mal rein 👀",
                    timestampMs = now - 86400000L
                ),
                avatarInitial = "H",
                unreadCount = 0,
                chatType = ChatType.PUBLIC_CITY
            )
        )

        val mockPrivate = listOf(
            ChatListItem(
                id = "priv_1",
                title = "Lisa W.",
                lastMessage = LastMessage(
                    text = "Treffen wir uns vor dem Eingang?",
                    timestampMs = now - 900000L
                ),
                avatarInitial = "L",
                unreadCount = 2,
                chatType = ChatType.PRIVATE,
                userStatus = UserStatus.ONLINE
            ),
            ChatListItem(
                id = "priv_2",
                title = "Max K.",
                lastMessage = LastMessage(
                    text = "War ein geiler Abend! 🔥",
                    timestampMs = now - 7200000L
                ),
                avatarInitial = "M",
                unreadCount = 0,
                chatType = ChatType.PRIVATE,
                userStatus = UserStatus.ONLINE
            )
        )

        rawPublicChats = mockPublic
        rawPrivateChats = mockPrivate

        applyFilters()

        chatRepository?.let { repo ->
            viewModelScope.launch {
                repo.getAllChats()
                    .catch { }
                    .collect { conversations ->
                        if (conversations.isNotEmpty()) {
                            val items = conversations.map { it.toChatListItem() }
                            rawPublicChats = items.filter { it.chatType == ChatType.PUBLIC_CITY }
                            rawPrivateChats = items.filter { it.chatType == ChatType.PRIVATE }
                            applyFilters()
                        }
                    }
            }
        }
    }

    private fun observeBlockedUsers() {
        viewModelScope.launch {
            userRepository.getBlockedUserIds("current_user")
                .catch { }
                .collect { blockedIds ->
                    currentBlockedUserIds = blockedIds.toSet()
                    applyFilters()
                }
        }
    }

    fun onTabSelected(type: ChatType) {
        _uiState.update { it.copy(selectedTab = type) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onToggleSearch(active: Boolean) {
        _uiState.update { 
            it.copy(
                isSearchActive = active,
                searchQuery = if (!active) "" else it.searchQuery
            )
        }
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.trim().lowercase()

        val filteredPrivate = rawPrivateChats.filter { item ->
            val notBlocked = !currentBlockedUserIds.contains(item.id) &&
                    !currentBlockedUserIds.contains("usr_${item.title}")
            val matchesQuery = query.isEmpty() ||
                    item.title.lowercase().contains(query) ||
                    item.lastMessage.text.lowercase().contains(query)
            notBlocked && matchesQuery
        }

        val filteredPublic = rawPublicChats.filter { item ->
            query.isEmpty() ||
                    item.title.lowercase().contains(query) ||
                    item.lastMessage.text.lowercase().contains(query)
        }

        _uiState.update { state ->
            state.copy(
                publicChats = filteredPublic,
                privateChats = filteredPrivate
            )
        }
    }

    fun onChatDeleted(chatId: String) {
        rawPublicChats = rawPublicChats.filter { it.id != chatId }
        rawPrivateChats = rawPrivateChats.filter { it.id != chatId }
        applyFilters()
    }

    fun onChatArchived(chatId: String) {
        rawPublicChats = rawPublicChats.filter { it.id != chatId }
        rawPrivateChats = rawPrivateChats.filter { it.id != chatId }
        applyFilters()
    }

    fun onUndoDelete(item: ChatListItem) {
        if (item.chatType == ChatType.PUBLIC_CITY) {
            rawPublicChats = listOf(item) + rawPublicChats.filter { it.id != item.id }
        } else {
            rawPrivateChats = listOf(item) + rawPrivateChats.filter { it.id != item.id }
        }
        applyFilters()
    }

    fun onUndoDelete(chat: ChatConversation) {
        onUndoDelete(chat.toChatListItem())
    }
}

