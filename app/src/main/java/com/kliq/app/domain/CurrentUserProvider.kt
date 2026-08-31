package com.kliq.app.domain

import com.kliq.app.data.repository.SessionRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.seed.KlagenfurtSeedData
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentUserProvider @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) {

    fun userId(): String {
        return sessionRepository.getUserId()?.takeIf { it.isNotBlank() }
            ?: KlagenfurtSeedData.CURRENT_USER_ID
    }

    suspend fun displayName(): String {
        val id = userId()
        return runCatching { userRepository.getUserById(id).first()?.username }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }
            ?: KlagenfurtSeedData.CURRENT_USER_NAME
    }
}
