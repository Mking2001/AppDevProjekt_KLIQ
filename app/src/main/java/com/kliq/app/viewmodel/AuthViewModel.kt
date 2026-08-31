package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.data.repository.SessionRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.repository.UserRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    data object Loading : AuthUiState()
    data class Authenticated(val userId: String) : AuthUiState()
    data object Unauthenticated : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository? = null,
    private val chatRepository: ChatRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAutoLogin()
    }

    fun checkAutoLogin() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val isValid = sessionRepository.checkAndValidateSession()
            if (isValid) {
                val userId = sessionRepository.getUserId() ?: "user_default"
                syncFromCloud(userId)
                _uiState.value = AuthUiState.Authenticated(userId)
            } else {
                _uiState.value = AuthUiState.Unauthenticated
            }
        }
    }

    fun login(token: String, userId: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            sessionRepository.saveSession(token, userId)
            syncFromCloud(userId)
            _uiState.value = AuthUiState.Authenticated(userId)
        }
    }

    private suspend fun syncFromCloud(userId: String) {
        try {

            (userRepository as? UserRepositoryImpl)?.clearLocalCache()
        } catch (ignored: Exception) { }
        try {
            userRepository?.syncUserProfile(userId)
        } catch (ignored: Exception) { }
        try {
            chatRepository?.syncAllChats()
        } catch (ignored: Exception) { }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            sessionRepository.clearSession()
            _uiState.value = AuthUiState.Unauthenticated
        }
    }

    fun deleteAccount(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val currentUserId = sessionRepository.getUserId() ?: ""
            if (currentUserId.isNotBlank()) {
                userRepository?.deleteAccount(currentUserId)
            }
            sessionRepository.clearSession()
            _uiState.value = AuthUiState.Unauthenticated
            onComplete()
        }
    }
}
