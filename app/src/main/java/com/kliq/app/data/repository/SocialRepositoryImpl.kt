package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.SocialDao
import com.kliq.app.data.local.entities.FriendEntity
import com.kliq.app.data.generated.*
import timber.log.Timber
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocialRepositoryImpl @Inject constructor(
    private val socialDao: SocialDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val kliqConnector: com.kliq.app.data.generated.KliqConnectorConnector
) : SocialRepository {

    override fun getFriendsForUser(userId: String): Flow<List<FriendEntity>> {
        kliqConnector?.let { connector ->
            CoroutineScope(ioDispatcher).launch {
                try {
                    val response = connector.getFriendsByUser.execute(userId = userId)
                    val remoteFriends = response.data.friends.map { f ->
                        FriendEntity(
                            userId = f.userId,
                            friendUserId = f.friendUserId,
                            status = f.status,
                            isQrVerified = f.isQrVerified
                        )
                    }
                    if (remoteFriends.isNotEmpty()) {
                        remoteFriends.forEach { socialDao.insertFriendship(it) }
                        Timber.i("Synced %d friends from Firebase SQL Connect for user %s", remoteFriends.size, userId)
                    }
                } catch (e: Exception) {
                    Timber.d(e, "Could not sync friends from SQL Connect for user %s (offline or empty)", userId)
                }
            }
        }
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
            Timber.i("Saved friendship locally in Room: %s <-> %s", userId, targetUserId)

            kliqConnector?.let { connector ->
                try {
                    connector.addFriend.execute(
                        userId = userId,
                        friendUserId = targetUserId,
                        createdAtTimestampMs = System.currentTimeMillis()
                    ) {
                        this.isQrVerified = isQrVerified
                    }
                    Timber.i("Successfully recorded friendship in Firebase SQL Connect (%s -> %s)", userId, targetUserId)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to record friendship in Firebase SQL Connect")
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error sending friend request in SocialRepositoryImpl")
            Result.failure(e)
        }
    }

    override suspend fun verifyAndAddFriend(userId: String, targetUserId: String): Result<Unit> {
        return sendFriendRequest(userId = userId, targetUserId = targetUserId, isQrVerified = true)
    }
}
