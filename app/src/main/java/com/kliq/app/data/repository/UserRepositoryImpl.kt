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
import timber.log.Timber
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
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
    private val kliqConnector: com.kliq.app.data.generated.KliqConnectorConnector,
    private val sessionRepository: SessionRepository? = null
) : UserRepository {

    override suspend fun checkUsernameAvailability(username: String): Boolean = withContext(ioDispatcher) {
        val trimmed = username.trim()
        if (trimmed.length < 3) return@withContext false

        // 1. Check local Room DB
        val localUser = userDao.getUserByUsername(trimmed)
        if (localUser != null) {
            Timber.d("Username '%s' exists locally in Room DB", trimmed)
            return@withContext false
        }

        // 2. Check Firebase Data Connect Cloud SQL (if available)
        kliqConnector?.let { connector ->
            try {
                val cloudUsers = connector.listUsers.execute().data.users
                val existsInCloud = cloudUsers.any { it.username.equals(trimmed, ignoreCase = true) }
                if (existsInCloud) {
                    Timber.i("Username '%s' already taken in Firebase SQL Connect", trimmed)
                    return@withContext false
                }
            } catch (e: Exception) {
                Timber.w(e, "Could not check username in Firebase SQL Connect, relying on local DB")
            }
        }

        true
    }

    override suspend fun registerUser(
        username: String,
        firstName: String,
        lastName: String,
        birthDateMs: Long,
        gender: String,
        hometown: String,
        phoneNumber: String,
        profilePictureUrl: String,
        searchIntent: SearchIntent,
        bio: String,
        password: String
    ): Result<UserEntity> = withContext(ioDispatcher) {
        try {
            val trimmedUsername = username.trim()
            val userId = "usr_${System.currentTimeMillis()}"
            val finalHometown = hometown.trim().ifBlank { "${firstName.trim()} ${lastName.trim()}".trim() }

            // Calculate approximate age from birthDateMs
            val nowMs = System.currentTimeMillis()
            val ageYears = ((nowMs - birthDateMs) / (365.25 * 24 * 60 * 60 * 1000L)).toInt().coerceAtLeast(18)

            val newUser = UserEntity(
                id = userId,
                username = trimmedUsername,
                email = "${trimmedUsername.lowercase()}@kliq.app",
                age = ageYears,
                hometown = finalHometown,
                profilePictureUrl = profilePictureUrl.ifBlank { null },
                bio = bio.trim().ifBlank { "Hey, ich bin neu bei KLIQ!" },
                phoneNumber = phoneNumber.trim().ifBlank { null },
                isVerified = true,
                gender = gender,
                updatedAtTimestampMs = System.currentTimeMillis()
            )

            // 1. Save User in Room
            userDao.insertUser(newUser)
            Timber.i("Saved user locally in Room DB: %s (%s)", newUser.id, newUser.username)

            // 2. Save Preferences in Room
            val preferences = UserPreferencesEntity(
                userId = userId,
                searchIntent = searchIntent
            )
            userDao.insertUserPreferences(preferences)

            // 3. Save Session so user is authenticated
            sessionRepository?.saveSession(token = "jwt_$userId", userId = userId)

            // 4. Sync to Firebase Data Connect Cloud SQL
            kliqConnector?.let { connector ->
                try {
                    connector.createUser.execute(
                        id = newUser.id,
                        username = newUser.username,
                        email = newUser.email
                    )
                    Timber.i("Successfully created user '%s' in Firebase SQL Connect", newUser.id)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to create user in Firebase SQL Connect (createUser)")
                }

                try {
                    connector.updateUserProfile.execute(id = newUser.id) {
                        this.username = newUser.username
                        this.bio = newUser.bio
                        this.profilePictureUrl = newUser.profilePictureUrl
                        this.age = newUser.age
                        this.hometown = newUser.hometown
                        this.gender = newUser.gender
                        this.phoneNumber = newUser.phoneNumber
                    }
                    Timber.i("Successfully updated profile for '%s' in Firebase SQL Connect", newUser.id)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to update user profile in Firebase SQL Connect")
                }

                try {
                    connector.upsertUserPreference.execute(
                        userId = newUser.id,
                        isDarkMode = false,
                        searchRadiusKm = 10,
                        pushNotificationsEnabled = true,
                        searchIntent = searchIntent.name,
                        smokingHabit = SmokingHabit.NEVER.name,
                        drinkingHabit = DrinkingHabit.NEVER.name
                    )
                    Timber.i("Successfully saved user preferences for '%s' in Firebase SQL Connect", newUser.id)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to upsert user preference in Firebase SQL Connect")
                }
            } ?: Timber.w("KliqConnectorConnector is null during registerUser - skipping Cloud SQL sync")

            Result.success(newUser)
        } catch (e: Exception) {
            Timber.e(e, "Error registering user in UserRepositoryImpl")
            Result.failure(e)
        }
    }

    override fun getUserById(userId: String): Flow<UserEntity?> {
        kliqConnector?.let { connector ->
            kotlinx.coroutines.CoroutineScope(ioDispatcher).launch {
                try {
                    val local = userDao.getUserByIdOneShot(userId)
                    if (local == null) {
                        val response = connector.getUserById.execute(id = userId)
                        val remote = response.data.user
                        if (remote != null) {
                            val entity = UserEntity(
                                id = remote.id,
                                username = remote.username,
                                email = remote.email,
                                age = remote.age,
                                hometown = remote.hometown,
                                profilePictureUrl = remote.profilePictureUrl,
                                bio = remote.bio,
                                phoneNumber = remote.phoneNumber,
                                isVerified = remote.isVerified,
                                gender = remote.gender,
                                updatedAtTimestampMs = System.currentTimeMillis()
                            )
                            userDao.insertUser(entity)
                            Timber.i("Fetched remote user '%s' from SQL Connect and cached locally", userId)
                        }
                    }
                } catch (e: Exception) {
                    Timber.d(e, "Could not fetch user '%s' from Firebase SQL Connect (offline or not found)", userId)
                }
            }
        }
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
            kliqConnector?.let { connector ->
                try {
                    val response = connector.getUserById.execute(id = userId)
                    val remote = response.data.user
                    if (remote != null) {
                        val entity = UserEntity(
                            id = remote.id,
                            username = remote.username,
                            email = remote.email,
                            age = remote.age,
                            hometown = remote.hometown,
                            profilePictureUrl = remote.profilePictureUrl,
                            bio = remote.bio,
                            phoneNumber = remote.phoneNumber,
                            isVerified = remote.isVerified,
                            gender = remote.gender,
                            updatedAtTimestampMs = System.currentTimeMillis()
                        )
                        userDao.insertUser(entity)
                        Timber.i("Successfully synced user profile for '%s' from SQL Connect", userId)
                        return@withContext Result.success(Unit)
                    }
                } catch (e: Exception) {
                    Timber.w(e, "Failed to sync user from SQL Connect, trying REST API fallback")
                }
            }

            val remoteUser = apiService.getUserProfile(userId)
            userDao.insertUser(remoteUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error syncing user profile for '%s'", userId)
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
                Timber.i("Saved user '%s' in Firebase SQL Connect", user.id)
            } catch (e: Exception) {
                Timber.w(e, "Failed to save user in Firebase SQL Connect (createUser)")
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
                // User may already exist in Cloud SQL
            }
            try {
                connector.updateUserProfile.execute(id = updatedUser.id) {
                    this.username = updatedUser.username
                    this.bio = updatedUser.bio
                    this.profilePictureUrl = updatedUser.profilePictureUrl
                    this.age = updatedUser.age
                    this.hometown = updatedUser.hometown
                    this.phoneNumber = updatedUser.phoneNumber
                }
                Timber.i("Successfully updated profile for '%s' in Firebase SQL Connect", userId)
            } catch (e: Exception) {
                Timber.e(e, "Failed to update profile in Firebase SQL Connect for '%s'", userId)
            }
        }
        Unit
    }

    override suspend fun updateProfilePicture(userId: String, pictureUrl: String): Unit = withContext(ioDispatcher) {
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
        kliqConnector?.let { connector ->
            try {
                connector.updateUserProfile.execute(id = userId) {
                    this.profilePictureUrl = pictureUrl
                }
                Timber.i("Updated profile picture in Firebase SQL Connect for user '%s'", userId)
            } catch (e: Exception) {
                Timber.e(e, "Failed to update profile picture in Firebase SQL Connect for user '%s'", userId)
            }
        }
        Unit
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
                kliqConnector?.let { connector ->
                    try {
                        connector.createUser.execute(
                            id = newUser.id,
                            username = newUser.username,
                            email = newUser.email
                        )
                        Timber.i("OTP User created in Firebase SQL Connect: %s", newUser.id)
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to create OTP user in Firebase SQL Connect: %s", newUser.id)
                    }
                }
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
