package com.kliq.app.data.repository

import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserById(userId: String): Flow<UserEntity?>
    fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?>
    suspend fun syncUserProfile(userId: String): Result<Unit>
    suspend fun saveUser(user: UserEntity)
    suspend fun saveUserPreferences(preferences: UserPreferencesEntity)
}
