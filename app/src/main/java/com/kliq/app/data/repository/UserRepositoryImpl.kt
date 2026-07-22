package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
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

    override suspend fun saveUserPreferences(preferences: UserPreferencesEntity) = withContext(Dispatchers.IO) {
        userDao.insertUserPreferences(preferences)
    }
}
