package com.kliq.app.data.repository

import com.kliq.app.data.local.security.SessionStorage
import com.kliq.app.data.model.SessionState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [SessionRepository] providing persistent encrypted session
 * storage operations backed by [SessionStorage].
 */
@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SessionRepository {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    override val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    override suspend fun checkAndValidateSession(): Boolean = withContext(ioDispatcher) {
        val isActive = sessionStorage.isSessionActive()
        val token = sessionStorage.getAuthToken()
        val userId = sessionStorage.getUserId()

        if (isActive && !token.isNullOrBlank() && !userId.isNullOrBlank()) {
            _sessionState.value = SessionState.Authenticated(token, userId)
            true
        } else {
            _sessionState.value = SessionState.Unauthenticated
            false
        }
    }

    override suspend fun saveSession(token: String, userId: String) = withContext(ioDispatcher) {
        sessionStorage.saveSession(token, userId)
        _sessionState.value = SessionState.Authenticated(token, userId)
    }

    override suspend fun clearSession() = withContext(ioDispatcher) {
        sessionStorage.clearSession()
        _sessionState.value = SessionState.Unauthenticated
    }

    override fun getAuthToken(): String? = sessionStorage.getAuthToken()

    override fun getUserId(): String? = sessionStorage.getUserId()

    override fun isSessionActive(): Boolean = sessionStorage.isSessionActive()
}
