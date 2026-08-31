package com.kliq.app.data.repository

import com.kliq.app.data.local.entities.FriendEntity
import kotlinx.coroutines.flow.Flow

interface SocialRepository {
    fun getFriendsForUser(userId: String): Flow<List<FriendEntity>>
    fun getFollowers(userId: String): Flow<List<FriendEntity>>
    fun getFollowing(userId: String): Flow<List<FriendEntity>>
    fun isFriend(userId: String, friendUserId: String): Flow<Boolean>
    suspend fun isFriendOneShot(userId: String, friendUserId: String): Boolean
    suspend fun sendFriendRequest(userId: String, targetUserId: String, isQrVerified: Boolean = true): Result<Unit>
    suspend fun verifyAndAddFriend(userId: String, targetUserId: String): Result<Unit>
    suspend fun removeFriend(userId: String, targetUserId: String): Result<Unit>
    suspend fun syncSocialConnections(userId: String): Result<Unit>
}
