package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.GroupMemberPresence
import com.kliq.app.data.model.UserStatus
import com.kliq.app.data.repository.GroupPresenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupPresenceUiState(
    val chatId: String = "",
    val chatTitle: String = "",
    val totalOnlineCount: Int = 0,
    val totalMembersCount: Int = 0,
    val onlineMembers: List<GroupMemberPresence> = emptyList(),
    val filteredMembers: List<GroupMemberPresence> = emptyList(),
    val searchQuery: String = "",
    val isParticipantSheetExpanded: Boolean = false,
    val myPresenceStatus: UserStatus = UserStatus.ONLINE,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class GroupPresenceViewModel @Inject constructor(
    private val repository: GroupPresenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupPresenceUiState())
    val uiState: StateFlow<GroupPresenceUiState> = _uiState.asStateFlow()

    fun loadGroupPresence(chatId: String) {
        if (chatId.isBlank()) return
        _uiState.update { it.copy(chatId = chatId, isLoading = true) }

        viewModelScope.launch {
            repository.observeGroupPresence(chatId)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Fehler beim Laden der Präsenzdaten"
                        )
                    }
                }
                .collect { summary ->
                    _uiState.update { state ->
                        val query = state.searchQuery
                        val filtered = if (query.isBlank()) {
                            summary.members
                        } else {
                            summary.members.filter { member ->
                                member.displayName.contains(query, ignoreCase = true) ||
                                        (member.statusMessage?.contains(query, ignoreCase = true) == true)
                            }
                        }
                        state.copy(
                            chatTitle = summary.chatTitle,
                            totalOnlineCount = summary.totalOnlineCount,
                            totalMembersCount = summary.totalMembersCount,
                            onlineMembers = summary.members,
                            filteredMembers = filtered,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) {
                state.onlineMembers
            } else {
                state.onlineMembers.filter { member ->
                    member.displayName.contains(query, ignoreCase = true) ||
                            (member.statusMessage?.contains(query, ignoreCase = true) == true)
                }
            }
            state.copy(
                searchQuery = query,
                filteredMembers = filtered
            )
        }
    }

    fun toggleParticipantSheet() {
        _uiState.update { it.copy(isParticipantSheetExpanded = !it.isParticipantSheetExpanded) }
    }

    fun setParticipantSheetExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isParticipantSheetExpanded = expanded) }
    }

    fun updateMyPresenceStatus(status: UserStatus) {
        val currentChat = _uiState.value.chatId
        _uiState.update { it.copy(myPresenceStatus = status) }
        viewModelScope.launch {
            if (currentChat.isNotBlank()) {
                repository.updatePresenceStatus(currentChat, "current_user", status)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
