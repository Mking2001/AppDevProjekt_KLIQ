package com.kliq.app.data.repository

import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatMessage
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
}
