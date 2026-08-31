package com.kliq.app.data.model

sealed class SessionState {
    data object Loading : SessionState()
    data class Authenticated(val token: String, val userId: String) : SessionState()
    data object Unauthenticated : SessionState()
}
