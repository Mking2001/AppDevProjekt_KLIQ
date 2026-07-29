package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.SocialDao
import com.kliq.app.data.local.entities.FriendEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val socialDao: SocialDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SocialRepository {

    override fun getFriendsForUser(userId: String): Flow<List<FriendEntity>> {
        return socialDao.getFriendsForUser(userId)
    }

    override fun isFriend(userId: String, friendUserId: String): Flow<Boolean> {
        return socialDao.isFriendFlow(userId, friendUserId)
    }

    override suspend fun isFriendOneShot(userId: String, friendUserId: String): Boolean = withContext(ioDispatcher) {
        val existing = socialDao.getFriendship(userId, friendUserId)
        existing != null && existing.status == "ACCEPTED"
    }

    override suspend fun sendFriendRequest(
        userId: String,
        targetUserId: String,
        isQrVerified: Boolean
    ): Result<Unit> = withContext(ioDispatcher) {
        try {
            if (userId == targetUserId) {
                return@withContext Result.failure(IllegalArgumentException("Du kannst dir nicht selbst eine Freundesanfrage senden."))
            }
            val existing = socialDao.getFriendship(userId, targetUserId)
            if (existing != null) {
                return@withContext Result.failure(IllegalStateException("Ihr seid bereits befreundet."))
            }
            val newFriend = FriendEntity(
                userId = userId,
                friendUserId = targetUserId,
                status = "ACCEPTED",
                isQrVerified = isQrVerified
            )
            socialDao.insertFriendship(newFriend)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyAndAddFriend(userId: String, targetUserId: String): Result<Unit> {
        return sendFriendRequest(userId = userId, targetUserId = targetUserId, isQrVerified = true)
    }
}
