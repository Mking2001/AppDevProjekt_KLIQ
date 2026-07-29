package com.kliq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kliq.app.data.local.entities.DirectMessageEntity
import com.kliq.app.data.model.MessageStatus
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object fuer 1-zu-1 Direktnachrichten.
 */
@Dao
interface DirectMessageDao {

    @Query("""
        SELECT * FROM direct_messages 
        WHERE (senderId = :userA AND receiverId = :userB) 
           OR (senderId = :userB AND receiverId = :userA) 
        ORDER BY timestamp ASC
    """)
    fun getDirectMessagesBetweenUsers(userA: String, userB: String): Flow<List<DirectMessageEntity>>

    @Query("SELECT * FROM direct_messages WHERE receiverId = :receiverId AND deliveryStatus != 'READ'")
    fun getUnreadDirectMessages(receiverId: String): Flow<List<DirectMessageEntity>>

    @Query("""
        SELECT COUNT(*) FROM direct_messages 
        WHERE receiverId = :receiverId 
          AND senderId = :senderId 
          AND deliveryStatus != 'READ'
    """)
    fun getUnreadCountForConversation(senderId: String, receiverId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM direct_messages WHERE receiverId = :receiverId AND deliveryStatus != 'READ'")
    fun getUnreadCountForUser(receiverId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirectMessage(message: DirectMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirectMessages(messages: List<DirectMessageEntity>)

    @Query("UPDATE direct_messages SET deliveryStatus = :status WHERE messageId = :messageId")
    suspend fun updateDeliveryStatus(messageId: String, status: MessageStatus)

    @Query("""
        UPDATE direct_messages 
        SET deliveryStatus = 'READ' 
        WHERE receiverId = :receiverId 
          AND senderId = :senderId 
          AND deliveryStatus != 'READ'
    """)
    suspend fun markConversationAsRead(senderId: String, receiverId: String)

    @Query("""
        DELETE FROM direct_messages 
        WHERE (senderId = :userA AND receiverId = :userB) 
           OR (senderId = :userB AND receiverId = :userA)
    """)
    suspend fun deleteConversationBetweenUsers(userA: String, userB: String)

    @Query("""
        SELECT * FROM direct_messages 
        WHERE (senderId = :userA AND receiverId = :userB) 
           OR (senderId = :userB AND receiverId = :userA) 
        ORDER BY timestamp DESC LIMIT 1
    """)
    suspend fun getLatestMessageBetweenUsers(userA: String, userB: String): DirectMessageEntity?
}
