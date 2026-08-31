package com.kliq.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatListItem
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.toChatListItem
import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.data.repository.LocationRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.util.CityChatConfig
import com.kliq.app.data.util.CityChatLocationMapper
import com.kliq.app.domain.CurrentUserProvider
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
    val archivedChats: List<ChatListItem> = emptyList(),
    val pendingDeleteChat: ChatListItem? = null,
    val showArchivedSection: Boolean = false,
    val selectedTab: ChatType = ChatType.PUBLIC_CITY,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val activeCity: CityChatConfig = CityChatLocationMapper.SUPPORTED_CITIES.first(),
    val suggestedForeignCity: CityChatConfig? = null,
    val showCitySwitchSuggestion: Boolean = false,
    val isCitySwitcherOpen: Boolean = false,
    val isCreateGroupDialogOpen: Boolean = false,
    val groupImageUri: String? = null,
    val availableUsers: List<com.kliq.app.data.local.entities.UserEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    private var activeChats: List<ChatListItem> = emptyList()
    private var archivedChats: List<ChatListItem> = emptyList()
    private var blockedUserIds: Set<String> = emptySet()
    private var currentGpsCity: CityChatConfig? = null
    private var manualSelectedCity: CityChatConfig? = null

    init {
        observeUserProfile()
        observeChats()
        observeLocationUpdates()
        syncChatsFromCloud()
    }

    private fun syncChatsFromCloud() {
        viewModelScope.launch {
            try {
                chatRepository.syncAllChats()
            } catch (ignored: Exception) { }
        }
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            val currentUserId = currentUserProvider.userId()
            userRepository.getUserById(currentUserId).collect { user ->
                val hometown = user?.hometown?.ifBlank { null } ?: "Klagenfurt"
                val homeCityConfig = CityChatLocationMapper.resolveCityByName(hometown)

                val targetCity = manualSelectedCity ?: homeCityConfig
                val isDifferent = currentGpsCity != null &&
                        !currentGpsCity!!.cityRegion.equals(targetCity.cityRegion, ignoreCase = true)

                _uiState.update { state ->
                    state.copy(
                        activeCity = targetCity,
                        suggestedForeignCity = if (isDifferent) currentGpsCity else null,
                        showCitySwitchSuggestion = isDifferent
                    )
                }

                chatRepository.createChatIfMissing(
                    chatId = targetCity.id,
                    name = targetCity.title,
                    chatType = ChatType.PUBLIC_CITY,
                    cityRegion = targetCity.cityRegion,
                    avatarInitial = targetCity.avatarInitial
                )
            }
        }
    }

    private fun observeChats() {
        viewModelScope.launch {
            val currentUserId = currentUserProvider.userId()

            combine(
                chatRepository.getActiveChats(),
                chatRepository.getArchivedChats(),
                userRepository.getBlockedUserIds(currentUserId)
            ) { active, archived, blocked ->
                Triple(active, archived, blocked)
            }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Chats konnten nicht geladen werden: ${error.localizedMessage}"
                        )
                    }
                }
                .collect { (active, archived, blocked) ->
                    activeChats = active.map { it.toChatListItem() }
                    archivedChats = archived.map { it.toChatListItem() }
                    blockedUserIds = blocked.toSet()
                    _uiState.update { it.copy(isLoading = false, error = null) }
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

    fun onChatOpened(chatId: String) {
        viewModelScope.launch {
            chatRepository.markChatAsRead(chatId)
        }
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.trim().lowercase()

        fun matchesQuery(item: ChatListItem): Boolean {
            if (query.isEmpty()) return true
            return item.title.lowercase().contains(query) ||
                    item.lastMessage.text.lowercase().contains(query)
        }

        fun isFromBlockedUser(item: ChatListItem): Boolean {
            if (item.chatType != ChatType.PRIVATE) return false
            return blockedUserIds.any { blockedId ->
                item.id == blockedId || item.id == "chat_$blockedId" || item.id == "priv_$blockedId"
            }
        }

        val activeCityRegion = _uiState.value.activeCity.cityRegion
        val filteredPublic = activeChats.filter {
            it.chatType == ChatType.PUBLIC_CITY &&
            it.cityRegion.equals(activeCityRegion, ignoreCase = true) &&
            matchesQuery(it)
        }
        val filteredPrivate = activeChats.filter {
            it.chatType == ChatType.PRIVATE && !isFromBlockedUser(it) && matchesQuery(it)
        }
        val filteredArchived = archivedChats.filter { matchesQuery(it) }

        _uiState.update { state ->
            state.copy(
                publicChats = filteredPublic,
                privateChats = filteredPrivate,
                archivedChats = filteredArchived
            )
        }
    }

    fun onRequestDeleteChat(chat: ChatListItem) {
        _uiState.update { it.copy(pendingDeleteChat = chat) }
    }

    fun onConfirmDeleteChat() {
        val target = _uiState.value.pendingDeleteChat ?: return
        _uiState.update { it.copy(pendingDeleteChat = null) }

        viewModelScope.launch {
            chatRepository.deleteChat(target.id)
        }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(pendingDeleteChat = null) }
    }

    fun onChatDeleted(chatId: String) {
        val target = (activeChats + archivedChats).find { it.id == chatId }
        if (target != null) {
            onRequestDeleteChat(target)
        } else {
            viewModelScope.launch { chatRepository.deleteChat(chatId) }
        }
    }

    fun onArchiveChat(item: ChatListItem) {
        viewModelScope.launch {
            chatRepository.archiveChat(item.id, isArchived = true)
        }
    }

    fun onUnarchiveChat(item: ChatListItem) {
        viewModelScope.launch {
            chatRepository.archiveChat(item.id, isArchived = false)
        }
    }

    fun onToggleArchivedSection(show: Boolean) {
        _uiState.update { it.copy(showArchivedSection = show) }
    }

    fun onChatArchived(chatId: String) {
        viewModelScope.launch {
            chatRepository.archiveChat(chatId, isArchived = true)
        }
    }

    fun onUndoDelete(item: ChatListItem) {
        viewModelScope.launch {
            chatRepository.createChatIfMissing(
                chatId = item.id,
                name = item.title,
                chatType = item.chatType,
                cityRegion = item.cityRegion,
                avatarInitial = item.avatarInitial
            )
            chatRepository.archiveChat(item.id, isArchived = false)
        }
    }

    fun onUndoDelete(chat: ChatConversation) {
        onUndoDelete(chat.toChatListItem())
    }

    private fun observeLocationUpdates() {
        viewModelScope.launch {
            locationRepository.locationUpdates.collect { location ->
                if (location == null) return@collect
                val resolvedConfig = CityChatLocationMapper.resolveCityForLocation(location)
                currentGpsCity = resolvedConfig

                _uiState.update { state ->
                    val isDifferent = !resolvedConfig.cityRegion.equals(state.activeCity.cityRegion, ignoreCase = true)
                    state.copy(
                        suggestedForeignCity = if (isDifferent) resolvedConfig else null,
                        showCitySwitchSuggestion = isDifferent
                    )
                }
            }
        }
    }

    fun openCitySwitcher() {
        _uiState.update { it.copy(isCitySwitcherOpen = true) }
    }

    fun closeCitySwitcher() {
        _uiState.update { it.copy(isCitySwitcherOpen = false) }
    }

    fun selectCityChat(config: CityChatConfig) {
        manualSelectedCity = config

        viewModelScope.launch {
            chatRepository.createChatIfMissing(
                chatId = config.id,
                name = config.title,
                chatType = ChatType.PUBLIC_CITY,
                cityRegion = config.cityRegion,
                avatarInitial = config.avatarInitial
            )
        }

        _uiState.update { state ->
            val isDifferentFromGps = currentGpsCity != null &&
                    !currentGpsCity!!.cityRegion.equals(config.cityRegion, ignoreCase = true)
            state.copy(
                activeCity = config,
                suggestedForeignCity = if (isDifferentFromGps) currentGpsCity else null,
                showCitySwitchSuggestion = isDifferentFromGps,
                isCitySwitcherOpen = false
            )
        }
    }

    fun openCreateGroupDialog() {
        viewModelScope.launch {
            val users = try {
                userRepository.getAllUsers()
            } catch (e: Exception) {
                emptyList()
            }
            val currentUserId = currentUserProvider.userId()
            _uiState.update {
                it.copy(
                    isCreateGroupDialogOpen = true,
                    groupImageUri = null,
                    availableUsers = users.filter { u -> u.id != currentUserId }
                )
            }
        }
    }

    fun closeCreateGroupDialog() {
        _uiState.update { it.copy(isCreateGroupDialogOpen = false, groupImageUri = null) }
    }

    fun setGroupImageUri(uri: String?) {
        _uiState.update { it.copy(groupImageUri = uri) }
    }

    fun createGroup(name: String, description: String, selectedUserIds: List<String>, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val imageUri = _uiState.value.groupImageUri
            val res = chatRepository.createGroupChat(
                name = name,
                description = description,
                imageUrl = imageUri,
                memberUserIds = selectedUserIds
            )
            closeCreateGroupDialog()
            res.onSuccess { newGroupId ->
                onCreated(newGroupId)
            }
        }
    }
}
