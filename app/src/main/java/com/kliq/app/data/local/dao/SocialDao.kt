package com.kliq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kliq.app.data.local.entities.FriendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialDao {
    @Query("SELECT * FROM friends WHERE (userId = :userId AND friendUserId = :friendUserId) OR (userId = :friendUserId AND friendUserId = :userId) LIMIT 1")
    suspend fun getFriendship(userId: String, friendUserId: String): FriendEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM friends WHERE (userId = :userId AND friendUserId = :friendUserId) OR (userId = :friendUserId AND friendUserId = :userId))")
    fun isFriendFlow(userId: String, friendUserId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendship(friend: FriendEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendEntity>)

    @Query("SELECT * FROM friends WHERE userId = :userId OR friendUserId = :userId")
    fun getFriendsForUser(userId: String): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE friendUserId = :userId")
    fun getFollowers(userId: String): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE userId = :userId")
    fun getFollowing(userId: String): Flow<List<FriendEntity>>

    @Query("SELECT COUNT(*) FROM friends WHERE friendUserId = :userId")
    fun getFollowerCount(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM friends WHERE userId = :userId")
    fun getFollowingCount(userId: String): Flow<Int>

    @Query("DELETE FROM friends WHERE (userId = :userId AND friendUserId = :friendUserId) OR (userId = :friendUserId AND friendUserId = :userId)")
    suspend fun deleteFriendship(userId: String, friendUserId: String)
}
