package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.remote.KliqApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val apiService: KliqApiService,
    private val userDao: UserDao
) {
    fun getUser(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)

    suspend fun refreshUser(userId: String) {
        try {
            val user = apiService.getUserProfile(userId)
            userDao.insertUser(user)
        } catch (e: Exception) {
            // Handle error (e.g., logging)
        }
    }
}
