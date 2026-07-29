package com.kliq.app.data.model

/**
 * Represents the current authentication state of the user session.
 */
sealed class SessionState {
    data object Loading : SessionState()
    data class Authenticated(val token: String, val userId: String) : SessionState()
    data object Unauthenticated : SessionState()
}
