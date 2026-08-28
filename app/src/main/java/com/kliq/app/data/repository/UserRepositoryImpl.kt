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
    private val kliqConnector: com.kliq.app.data.generated.KliqConnectorConnector? = null,
    private val sessionRepository: SessionRepository? = null
) : UserRepository {

    override suspend fun checkUsernameAvailability(username: String): Boolean = withContext(ioDispatcher) {
        val trimmed = username.trim()
        if (trimmed.isBlank()) return@withContext false
        if (userDao.getUserByUsername(trimmed) != null) return@withContext false

        kliqConnector?.let { connector ->
            try {
                val res = connector.checkUsername.execute(username = trimmed)
                if (res.data.users.isNotEmpty()) {
                    return@withContext false
                }
            } catch (e: Exception) {
                timber.log.Timber.d("DataConnect: checkUsername: %s", e.message)
            }
        }
        true
    }

    override suspend fun checkEmailAvailability(email: String): Boolean = withContext(ioDispatcher) {
        val trimmed = email.trim()
        if (trimmed.isBlank()) return@withContext false
        if (userDao.getUserByEmail(trimmed) != null) return@withContext false
        true
    }

    override suspend fun loginUser(identifier: String, password: String): Result<UserEntity> = withContext(ioDispatcher) {
        val trimmed = identifier.trim()
        if (trimmed.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Bitte gib deinen Benutzernamen, deine E-Mail oder Telefonnummer ein."))
        }
        if (password.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Das Passwort muss mindestens 6 Zeichen lang sein."))
        }

        // 1. Suche in lokaler Room-Datenbank
        var foundUser = userDao.getUserByUsername(trimmed)
            ?: userDao.getUserByEmail(trimmed)
            ?: userDao.getUserByPhone(trimmed)

        // 2. Falls lokal nicht gefunden, in Cloud SQL suchen
        if (foundUser == null && kliqConnector != null) {
            try {
                val usernameResult = kliqConnector.checkUsername.execute(username = trimmed)
                val cloudUserSummary = usernameResult.data.users.firstOrNull()

                val cloudUserId = cloudUserSummary?.id ?: run {
                    val allUsers = kliqConnector.listUsers.execute().data.users
                    allUsers.find {
                        it.username.equals(trimmed, ignoreCase = true)
                    }?.id
                }

                if (cloudUserId != null) {
                    val fullCloudUser = kliqConnector.getUserById.execute(id = cloudUserId).data.user
                    if (fullCloudUser != null) {
                        val importedUser = UserEntity(
                            id = fullCloudUser.id,
                            username = fullCloudUser.username,
                            email = fullCloudUser.email,
                            age = fullCloudUser.age,
                            hometown = fullCloudUser.hometown,
                            profilePictureUrl = fullCloudUser.profilePictureUrl,
                            bio = fullCloudUser.bio,
                            phoneNumber = fullCloudUser.phoneNumber,
                            isVerified = true,
                            gender = fullCloudUser.gender ?: "UNSPECIFIED",
                            updatedAtTimestampMs = System.currentTimeMillis()
                        )
                        userDao.insertUser(importedUser)
                        foundUser = importedUser
                    }
                }
            } catch (e: Exception) {
                timber.log.Timber.e(e, "DataConnect: Cloud SQL login lookup failed")
            }
        }

        if (foundUser != null) {
            sessionRepository?.saveSession(token = "jwt_${foundUser.id}", userId = foundUser.id)
            Result.success(foundUser)
        } else {
            Result.failure(IllegalArgumentException("Kein Konto mit diesen Anmeldedaten gefunden. Bitte registriere dich zuerst."))
        }
    }

    override suspend fun registerUser(
        username: String,
        email: String,
        firstName: String,
        lastName: String,
        birthDateMs: Long,
        gender: String,
        hometown: String,
        countryCode: String,
        phoneNumber: String,
        profilePictureUrl: String,
        photos: List<String>,
        searchIntent: SearchIntent,
        smokingHabit: SmokingHabit,
        drinkingHabit: DrinkingHabit,
        bio: String,
        password: String
    ): Result<UserEntity> = withContext(ioDispatcher) {
        try {
            val trimmedUsername = username.trim()
            val userId = "usr_${System.currentTimeMillis()}"
            val finalHometown = hometown.trim().ifBlank { "${firstName.trim()} ${lastName.trim()}".trim() }
            val finalEmail = email.trim().ifBlank { "${trimmedUsername.lowercase()}@kliq.app" }
            val primaryPhotoUrl = photos.firstOrNull { it.isNotBlank() } ?: profilePictureUrl.ifBlank { null }

            // Calculate approximate age from birthDateMs
            val nowMs = System.currentTimeMillis()
            val ageYears = ((nowMs - birthDateMs) / (365.25 * 24 * 60 * 60 * 1000L)).toInt().coerceAtLeast(18)

            val newUser = UserEntity(
                id = userId,
                username = trimmedUsername,
                email = finalEmail,
                age = ageYears,
                hometown = finalHometown,
                profilePictureUrl = primaryPhotoUrl,
                bio = bio.trim().ifBlank { "Hey, ich bin neu bei KLIQ!" },
                phoneNumber = phoneNumber.trim().ifBlank { null },
                isVerified = true,
                gender = gender,
                updatedAtTimestampMs = System.currentTimeMillis()
            )

            // 1. Save User in Room
            userDao.insertUser(newUser)

            // 2. Save Preferences in Room
            val preferences = UserPreferencesEntity(
                userId = userId,
                searchIntent = searchIntent,
                smokingHabit = smokingHabit,
                drinkingHabit = drinkingHabit
            )
            userDao.insertUserPreferences(preferences)

            // 3. Save Session so user is authenticated
            sessionRepository?.saveSession(token = "jwt_$userId", userId = userId)

            // 4. Sync to Firebase Data Connect Cloud SQL
            kliqConnector?.let { connector ->
                try {
                    timber.log.Timber.d("DataConnect: Creating user %s (%s, %s)...", newUser.id, newUser.username, newUser.email)
                    connector.createUser.execute(
                        id = newUser.id,
                        username = newUser.username,
                        email = newUser.email
                    ) {
                        this.firstName = firstName.trim().ifBlank { null }
                        this.lastName = lastName.trim().ifBlank { null }
                        this.birthDateMs = birthDateMs
                        this.age = ageYears
                        this.gender = gender
                        this.hometown = finalHometown
                        this.countryCode = countryCode.ifBlank { "+43" }
                        this.phoneNumber = phoneNumber.trim().ifBlank { null }
                        this.profilePictureUrl = primaryPhotoUrl
                        this.bio = newUser.bio
                    }
                    timber.log.Timber.d("DataConnect: User created successfully on Cloud SQL!")
                } catch (e: Exception) {
                    timber.log.Timber.e(e, "DataConnect: Failed to create user on Cloud SQL")
                }

                try {
                    connector.upsertUserPreference.execute(
                        userId = newUser.id,
                        isDarkMode = false,
                        searchRadiusKm = 10,
                        pushNotificationsEnabled = true,
                        searchIntent = searchIntent.name,
                        smokingHabit = smokingHabit.name,
                        drinkingHabit = drinkingHabit.name
                    )
                    timber.log.Timber.d("DataConnect: User preferences upserted successfully on Cloud SQL!")
                } catch (e: Exception) {
                    timber.log.Timber.e(e, "DataConnect: Failed to upsert user preferences on Cloud SQL")
                }

                // Sync all gallery photos
                photos.forEachIndexed { index, photoUrl ->
                    if (photoUrl.isNotBlank()) {
                        try {
                            val photoId = "photo_${userId}_${System.currentTimeMillis()}_$index"
                            connector.addUserPhoto.execute(
                                id = photoId,
                                userId = userId,
                                imageUrl = photoUrl
                            ) {
                                this.displayOrder = index
                            }
                            timber.log.Timber.d("DataConnect: User photo %d synced to Cloud SQL", index)
                        } catch (e: Exception) {
                            timber.log.Timber.e(e, "DataConnect: Failed to sync photo %d to Cloud SQL", index)
                        }
                    }
                }
            }

            Result.success(newUser)
        } catch (e: Exception) {
            timber.log.Timber.e(e, "UserRepository.registerUser failed")
            Result.failure(e)
        }
    }

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
                // Graceful fallback if user already exists
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
            } catch (ignored: Exception) {
                // Graceful fallback for offline mode
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
            } catch (ignored: Exception) { }
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
                    } catch (ignored: Exception) { }
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
