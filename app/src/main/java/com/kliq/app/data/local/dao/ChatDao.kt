package com.kliq.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.MessageEntity
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.MessageStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats ORDER BY lastMessageTimestampMs DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE chatType = 'PRIVATE' ORDER BY lastMessageTimestampMs DESC")
    fun getPrivateChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE chatType = 'PUBLIC_CITY' AND (:cityRegion IS NULL OR cityRegion = :cityRegion) ORDER BY lastMessageTimestampMs DESC")
    fun getPublicCityChats(cityRegion: String? = null): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :chatId")
    fun getChatById(chatId: String): Flow<ChatEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatEntity>)

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestampMs ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND (text LIKE '%' || :query || '%' OR senderName LIKE '%' || :query || '%') ORDER BY timestampMs ASC")
    fun searchMessagesInChat(chatId: String, query: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateMessageStatus(messageId: String, status: MessageStatus)

    @Query("UPDATE chats SET lastMessageText = :text, lastMessageTimestampMs = :timestampMs, lastMessageTimestampIso = :timestampIso, unreadCount = unreadCount + :unreadIncrement WHERE id = :chatId")
    suspend fun updateChatLastMessage(
        chatId: String,
        text: String,
        timestampMs: Long,
        timestampIso: String,
        unreadIncrement: Int = 0
    )

    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun markChatAsRead(chatId: String)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChatById(chatId: String)
}
