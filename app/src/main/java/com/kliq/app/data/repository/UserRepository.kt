package com.kliq.app.data.repository

import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.model.UserReputationSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface UserRepository {
    fun getUserById(userId: String): Flow<UserEntity?>
    fun getUser(userId: String): Flow<UserEntity?> = getUserById(userId)
    fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?>
    fun getUserReputationSummary(userId: String): Flow<UserReputationSummary> =
        flowOf(UserReputationSummary(targetUserId = userId))
    suspend fun syncUserProfile(userId: String): Result<Unit>
    suspend fun saveUser(user: UserEntity)
    suspend fun saveUserPreferences(preferences: UserPreferencesEntity)
    suspend fun saveSearchIntent(userId: String, intent: SearchIntent)
    suspend fun saveConsumptionHabits(
        userId: String,
        smokingHabit: SmokingHabit,
        drinkingHabit: DrinkingHabit
    ) {}
    suspend fun saveProfile(
        userId: String,
        username: String,
        age: Int,
        hometown: String,
        bio: String,
        profilePictureUrl: String? = null
    ) {}
    suspend fun updateProfilePicture(userId: String, pictureUrl: String) {}
    suspend fun requestOtp(countryCode: String, phoneNumber: String): Result<Boolean>
    suspend fun verifyOtp(countryCode: String, phoneNumber: String, otpCode: String): Result<UserEntity>
    suspend fun checkUsernameAvailability(username: String): Boolean = true
    suspend fun checkEmailAvailability(email: String): Boolean = true
    suspend fun checkPhoneAvailability(phoneNumber: String): Boolean = true
    suspend fun loginUser(identifier: String, password: String): Result<UserEntity> = Result.failure(NotImplementedError())
    suspend fun registerUser(
        username: String,
        email: String,
        firstName: String,
        lastName: String,
        birthDateMs: Long,
        gender: String = "MALE",
        hometown: String = "",
        countryCode: String = "+43",
        phoneNumber: String = "",
        profilePictureUrl: String,
        photos: List<String> = emptyList(),
        searchIntent: SearchIntent,
        smokingHabit: SmokingHabit = SmokingHabit.NEVER,
        drinkingHabit: DrinkingHabit = DrinkingHabit.NEVER,
        bio: String,
        password: String
    ): Result<UserEntity> = Result.failure(NotImplementedError())
    fun isUserBlocked(currentUserId: String, targetUserId: String): Flow<Boolean> = flowOf(false)
    fun getBlockedUserIds(currentUserId: String): Flow<List<String>> = flowOf(emptyList())
    suspend fun blockUser(currentUserId: String, targetUserId: String, reason: String? = null): Result<Unit> = Result.success(Unit)
    suspend fun unblockUser(currentUserId: String, targetUserId: String): Result<Unit> = Result.success(Unit)
    suspend fun reportUser(reporterUserId: String, targetUserId: String, reason: String, details: String? = null): Result<Unit> = Result.success(Unit)
    suspend fun deleteAccount(userId: String): Result<Unit> = Result.success(Unit)
    suspend fun searchUsers(query: String): List<UserEntity> = emptyList()
    suspend fun getAllUsers(): List<UserEntity> = emptyList()
}
