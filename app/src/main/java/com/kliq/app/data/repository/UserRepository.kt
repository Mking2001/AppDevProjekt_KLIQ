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
    suspend fun requestOtp(countryCode: String, phoneNumber: String): Result<Boolean>
    suspend fun verifyOtp(countryCode: String, phoneNumber: String, otpCode: String): Result<UserEntity>
}
