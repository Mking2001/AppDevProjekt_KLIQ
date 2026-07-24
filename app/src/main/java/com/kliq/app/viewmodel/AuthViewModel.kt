package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for authentication and persistent auto-login checks.
 */
sealed class AuthUiState {
    data object Loading : AuthUiState()
    data class Authenticated(val userId: String) : AuthUiState()
    data object Unauthenticated : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

/**
 * ViewModel managing authentication state and auto-login evaluation.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
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
            _uiState.value = AuthUiState.Authenticated(userId)
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            sessionRepository.clearSession()
            _uiState.value = AuthUiState.Unauthenticated
        }
    }
}
