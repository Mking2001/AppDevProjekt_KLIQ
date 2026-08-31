package com.kliq.app.data.repository

import com.kliq.app.data.model.SessionState
import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    val sessionState: StateFlow<SessionState>
    suspend fun checkAndValidateSession(): Boolean
    suspend fun saveSession(token: String, userId: String)
    suspend fun clearSession()
    fun getAuthToken(): String?
    fun getUserId(): String?
    fun isSessionActive(): Boolean
}
