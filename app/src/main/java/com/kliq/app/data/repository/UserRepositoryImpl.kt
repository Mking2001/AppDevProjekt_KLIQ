package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.remote.KliqApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val apiService: KliqApiService
) : UserRepository {

    override fun getUserById(userId: String): Flow<UserEntity?> {
        return userDao.getUserById(userId)
    }

    override fun getUserPreferences(userId: String): Flow<UserPreferencesEntity?> {
        return userDao.getUserPreferences(userId)
    }

    override suspend fun syncUserProfile(userId: String): Result<Unit> {
        return try {
            val remoteUser = apiService.getUserProfile(userId)
            userDao.insertUser(remoteUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun saveUserPreferences(preferences: UserPreferencesEntity) {
        userDao.insertUserPreferences(preferences)
    }
}
