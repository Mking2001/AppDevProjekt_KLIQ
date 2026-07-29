package com.kliq.app.data.repository

import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.DirectMessage
import com.kliq.app.data.model.MessageStatus
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllChats(): Flow<List<ChatConversation>>
    fun getPrivateChats(): Flow<List<ChatConversation>>
    fun getPublicCityChats(cityRegion: String? = null): Flow<List<ChatConversation>>
    fun getMessagesForChat(chatId: String): Flow<List<ChatMessage>>
    fun searchMessagesInChat(chatId: String, query: String): Flow<List<ChatMessage>>
    suspend fun syncChatMessages(chatId: String): Result<Unit>
    suspend fun sendTextMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        text: String
    ): Result<ChatMessage>

    suspend fun sendMediaMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        text: String,
        mediaUrl: String
    ): Result<ChatMessage>

    suspend fun updateMessageStatus(messageId: String, status: MessageStatus)
    suspend fun markChatAsRead(chatId: String)
    fun getDirectMessages(currentUserId: String, targetUserId: String): Flow<List<DirectMessage>>
    fun getUnreadDirectMessages(userId: String): Flow<List<DirectMessage>>
    fun getUnreadCountForUser(userId: String): Flow<Int>
    suspend fun sendDirectMessage(
        senderId: String,
        receiverId: String,
        text: String,
        isEncrypted: Boolean = true,
        mediaUrl: String? = null
    ): Result<DirectMessage>

    suspend fun receiveDirectMessage(message: DirectMessage): Result<Unit>
    suspend fun syncDirectMessages(userId: String, targetUserId: String): Result<Unit>
    suspend fun updateDirectMessageStatus(messageId: String, status: MessageStatus)
    suspend fun markDirectConversationAsRead(senderId: String, receiverId: String)
    fun getCityChatForLocation(location: com.kliq.app.data.model.LocationData): Flow<com.kliq.app.data.model.ChatListItem>
    suspend fun syncPublicCityMessages(chatId: String): Result<Unit>
    suspend fun joinPublicCityChat(chatId: String): Result<Unit>
}
