package com.kliq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kliq.app.data.local.entities.BlockedUserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object für den Zugriff auf blockierte Nutzer in der Room-Datenbank.
 */
@Dao
interface BlockedUserDao {

    @Query("SELECT * FROM blocked_users WHERE userId = :currentUserId AND blockedUserId = :targetUserId LIMIT 1")
    suspend fun getBlockedUser(currentUserId: String, targetUserId: String): BlockedUserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM blocked_users WHERE userId = :currentUserId AND blockedUserId = :targetUserId)")
    fun isUserBlockedFlow(currentUserId: String, targetUserId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM blocked_users WHERE userId = :currentUserId AND blockedUserId = :targetUserId)")
    suspend fun isUserBlockedOneShot(currentUserId: String, targetUserId: String): Boolean

    @Query("SELECT blockedUserId FROM blocked_users WHERE userId = :currentUserId")
    fun getBlockedUserIdsFlow(currentUserId: String): Flow<List<String>>

    @Query("SELECT blockedUserId FROM blocked_users WHERE userId = :currentUserId")
    suspend fun getBlockedUserIds(currentUserId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun blockUser(entity: BlockedUserEntity)

    @Query("DELETE FROM blocked_users WHERE userId = :currentUserId AND blockedUserId = :targetUserId")
    suspend fun unblockUser(currentUserId: String, targetUserId: String)

    @Query("SELECT * FROM blocked_users WHERE userId = :currentUserId ORDER BY blockedAtTimestampMs DESC")
    fun getAllBlockedUsers(currentUserId: String): Flow<List<BlockedUserEntity>>
}
