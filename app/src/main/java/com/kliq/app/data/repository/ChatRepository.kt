package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatDao: ChatDao
) {
    fun getAllChats(): Flow<List<ChatEntity>> = chatDao.getAllChats()

    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> = chatDao.getMessagesForChat(chatId)

    suspend fun sendMessage(message: MessageEntity) {
        chatDao.insertMessage(message)
        chatDao.updateChatLastMessage(
            chatId = message.chatId,
            text = message.text,
            timestamp = message.timestamp,
            unreadIncrement = 0 // My messages are already read
        )
        // Implement API call to send message to backend
    }
    
    suspend fun markAsRead(chatId: String) {
        chatDao.markChatAsRead(chatId)
    }
}
