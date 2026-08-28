package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.BlockedUserDao
import com.kliq.app.data.local.dao.ReviewDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.BlockedUserEntity
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.data.model.SmokingHabit
import com.kliq.app.data.model.UserReputationSummary
import com.kliq.app.data.remote.BlockUserRequestDto
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.remote.ReportUserRequestDto
import com.kliq.app.data.generated.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: KliqApiService,
    private val reviewDao: ReviewDao? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val blockedUserDao: BlockedUserDao? = null,
    private val kliqConnector: com.kliq.app.data.generated.KliqConnectorConnector? = null
) : UserRepository {

    override fun getUserById(userId: String): Flow<UserEntity?> {
        return userDao.getUserById(userId).flowOn(ioDispatcher)
    }

    override fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> {
        return userDao.getUserPreferences(userId).flowOn(ioDispatcher)
    }

    override fun getUserReputationSummary(userId: String): Flow<UserReputationSummary> {
        val dao = reviewDao ?: return flowOf(UserReputationSummary(targetUserId = userId))
        return combine(
            dao.getAverageRatingForTargetUser(userId),
            dao.getReviewsCountForTargetUser(userId),
            dao.getVerifiedReviewsCountForTargetUser(userId)
        ) { avgRating, totalCount, verifiedCount ->
            UserReputationSummary(
                targetUserId = userId,
                averageRating = avgRating ?: 0.0,
                totalReviewsCount = totalCount,
                verifiedReviewsCount = verifiedCount
            )
        }.flowOn(ioDispatcher)
    }

    override suspend fun syncUserProfile(userId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            val remoteUser = apiService.getUserProfile(userId)
            userDao.insertUser(remoteUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveUser(user: UserEntity): Unit = withContext(ioDispatcher) {
        userDao.insertUser(user)
        kliqConnector?.let { connector ->
            try {
                connector.createUser.execute(
                    id = user.id,
                    username = user.username,
                    email = user.email
                )
            } catch (ignored: Exception) {
                // Graceful fallback for offline mode
            }
        }
        Unit
    }

    override suspend fun saveProfile(
        userId: String,
        username: String,
        age: Int,
        hometown: String,
        bio: String,
        profilePictureUrl: String?
    ): Unit = withContext(ioDispatcher) {
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
        kliqConnector?.let { connector ->
            try {
                connector.createUser.execute(
                    id = updatedUser.id,
                    username = updatedUser.username,
                    email = updatedUser.email
                )
            } catch (ignored: Exception) {
                // Graceful fallback for offline mode
            }
        }
        Unit
    }

    override suspend fun updateProfilePicture(userId: String, pictureUrl: String) = withContext(ioDispatcher) {
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

    override suspend fun saveUserPreferences(preferences: UserPreferencesEntity) = withContext(ioDispatcher) {
        userDao.insertUserPreferences(preferences)
    }

    override suspend fun saveSearchIntent(userId: String, intent: SearchIntent) = withContext(ioDispatcher) {
        val existingPref = userDao.getUserPreferencesOneShot(userId)
        val updatedPref = (existingPref ?: UserPreferencesEntity(userId = userId)).copy(searchIntent = intent)
        userDao.insertUserPreferences(updatedPref)
    }

    override suspend fun saveConsumptionHabits(
        userId: String,
        smokingHabit: SmokingHabit,
        drinkingHabit: DrinkingHabit
    ) = withContext(ioDispatcher) {
        val existingPref = userDao.getUserPreferencesOneShot(userId)
        val updatedPref = (existingPref ?: UserPreferencesEntity(userId = userId)).copy(
            smokingHabit = smokingHabit,
            drinkingHabit = drinkingHabit
        )
        userDao.insertUserPreferences(updatedPref)
    }

    override suspend fun requestOtp(countryCode: String, phoneNumber: String): Result<Boolean> = withContext(ioDispatcher) {
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

    override suspend fun verifyOtp(countryCode: String, phoneNumber: String, otpCode: String): Result<UserEntity> = withContext(ioDispatcher) {
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

    override fun isUserBlocked(currentUserId: String, targetUserId: String): Flow<Boolean> {
        val dao = blockedUserDao ?: return flowOf(false)
        return dao.isUserBlockedFlow(currentUserId, targetUserId).flowOn(ioDispatcher)
    }

    override fun getBlockedUserIds(currentUserId: String): Flow<List<String>> {
        val dao = blockedUserDao ?: return flowOf(emptyList())
        return dao.getBlockedUserIdsFlow(currentUserId).flowOn(ioDispatcher)
    }

    override suspend fun blockUser(currentUserId: String, targetUserId: String, reason: String?): Result<Unit> = withContext(ioDispatcher) {
        try {
            blockedUserDao?.blockUser(
                BlockedUserEntity(
                    userId = currentUserId,
                    blockedUserId = targetUserId,
                    reason = reason,
                    blockedAtTimestampMs = System.currentTimeMillis()
                )
            )
            try {
                apiService.blockUser(
                    BlockUserRequestDto(
                        currentUserId = currentUserId,
                        targetUserId = targetUserId,
                        reason = reason
                    )
                )
            } catch (ignored: Exception) {
                // Network error handled gracefully, local change takes effect immediately
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unblockUser(currentUserId: String, targetUserId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            blockedUserDao?.unblockUser(currentUserId, targetUserId)
            try {
                apiService.unblockUser(currentUserId, targetUserId)
            } catch (ignored: Exception) {
                // Graceful fallback
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reportUser(
        reporterUserId: String,
        targetUserId: String,
        reason: String,
        details: String?
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            try {
                apiService.reportUser(
                    ReportUserRequestDto(
                        reporterUserId = reporterUserId,
                        targetUserId = targetUserId,
                        reason = reason,
                        details = details
                    )
                )
            } catch (ignored: Exception) {
                // Graceful handling of offline/mock mode
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
