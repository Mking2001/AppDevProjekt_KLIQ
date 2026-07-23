package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.remote.KliqApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: KliqApiService
) : UserRepository {

    override fun getUserById(userId: String): Flow<UserEntity?> {
        return userDao.getUserById(userId).flowOn(Dispatchers.IO)
    }

    override fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> {
        return userDao.getUserPreferences(userId).flowOn(Dispatchers.IO)
    }

    override suspend fun syncUserProfile(userId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val remoteUser = apiService.getUserProfile(userId)
            userDao.insertUser(remoteUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    override suspend fun saveProfile(
        userId: String,
        username: String,
        age: Int,
        hometown: String,
        bio: String,
        profilePictureUrl: String?
    ) = withContext(Dispatchers.IO) {
        val existingUser = userDao.getUserByIdOneShot(userId)
        val updatedUser = UserEntity(
            id = userId,
            username = username,
            email = existingUser?.email ?: "",
            age = age,
            hometown = hometown,
            profilePictureUrl = profilePictureUrl ?: existingUser?.profilePictureUrl,
            bio = bio.ifBlank { null },
            phoneNumber = existingUser?.phoneNumber,
            isVerified = existingUser?.isVerified ?: false,
            updatedAtTimestampMs = System.currentTimeMillis()
        )
        userDao.insertUser(updatedUser)
    }

    override suspend fun updateProfilePicture(userId: String, pictureUrl: String) = withContext(Dispatchers.IO) {
        val existingUser = userDao.getUserByIdOneShot(userId)
        val updatedUser = (existingUser ?: UserEntity(
            id = userId,
            username = "User",
            email = ""
        )).copy(
            profilePictureUrl = pictureUrl,
            updatedAtTimestampMs = System.currentTimeMillis()
        )
        userDao.insertUser(updatedUser)
    }

    override suspend fun saveUserPreferences(preferences: UserPreferencesEntity) = withContext(Dispatchers.IO) {
        userDao.insertUserPreferences(preferences)
    }

    override suspend fun saveSearchIntent(userId: String, intent: SearchIntent) = withContext(Dispatchers.IO) {
        val existingPref = userDao.getUserPreferencesOneShot(userId)
        val updatedPref = (existingPref ?: UserPreferencesEntity(userId = userId)).copy(searchIntent = intent)
        userDao.insertUserPreferences(updatedPref)
    }

    override suspend fun saveConsumptionHabits(
        userId: String,
        smokingHabit: SmokingHabit,
        drinkingHabit: DrinkingHabit
    ) = withContext(Dispatchers.IO) {
        val existingPref = userDao.getUserPreferencesOneShot(userId)
        val updatedPref = (existingPref ?: UserPreferencesEntity(userId = userId)).copy(
            smokingHabit = smokingHabit,
            drinkingHabit = drinkingHabit
        )
        userDao.insertUserPreferences(updatedPref)
    }

    override suspend fun requestOtp(countryCode: String, phoneNumber: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val digitsOnly = phoneNumber.filter { it.isDigit() }
            if (digitsOnly.length in 7..15) {
                Result.success(true)
            } else {
                Result.failure(IllegalArgumentException("Ungültiges Telefonnummer-Format."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(countryCode: String, phoneNumber: String, otpCode: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            val digitsOnlyOtp = otpCode.filter { it.isDigit() }
            if (digitsOnlyOtp.length == 6) {
                val fullNumber = "$countryCode$phoneNumber"
                val newUser = UserEntity(
                    id = "usr_${System.currentTimeMillis()}",
                    username = "kliq_user_${fullNumber.takeLast(4)}",
                    email = "user@kliq.app",
                    profilePictureUrl = null,
                    bio = "Mitglied bei Kliq",
                    phoneNumber = fullNumber,
                    isVerified = true,
                    updatedAtTimestampMs = System.currentTimeMillis()
                )
                userDao.insertUser(newUser)
                Result.success(newUser)
            } else {
                Result.failure(IllegalArgumentException("Der eingegebene Code muss genau 6 Ziffern enthalten."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
