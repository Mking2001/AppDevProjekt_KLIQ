package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.MessageEntity
import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.model.formatMsToIso
import com.kliq.app.data.remote.KliqApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val apiService: KliqApiService? = null
) : ChatRepository {

    override fun getAllChats(): Flow<List<ChatConversation>> {
        return chatDao.getAllChats().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPrivateChats(): Flow<List<ChatConversation>> {
        return chatDao.getPrivateChats().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getPublicCityChats(cityRegion: String?): Flow<List<ChatConversation>> {
        return chatDao.getPublicCityChats(cityRegion).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getMessagesForChat(chatId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForChat(chatId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchMessagesInChat(chatId: String, query: String): Flow<List<ChatMessage>> {
        return chatDao.searchMessagesInChat(chatId, query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncChatMessages(chatId: String): Result<Unit> {
        return try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendTextMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        text: String
    ): Result<ChatMessage> {
        return sendInternalMessage(
            chatId = chatId,
            senderUserId = senderUserId,
            senderName = senderName,
            text = text,
            mediaUrl = null
        )
    }

    override suspend fun sendMediaMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        text: String,
        mediaUrl: String
    ): Result<ChatMessage> {
        return sendInternalMessage(
            chatId = chatId,
            senderUserId = senderUserId,
            senderName = senderName,
            text = text,
            mediaUrl = mediaUrl
        )
    }

    private suspend fun sendInternalMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        text: String,
        mediaUrl: String?
    ): Result<ChatMessage> {
        return try {
            val nowMs = System.currentTimeMillis()
            val nowIso = formatMsToIso(nowMs)

            val messageEntity = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                senderUserId = senderUserId,
                senderName = senderName,
                text = text,
                timestampMs = nowMs,
                timestampIso = nowIso,
                mediaUrl = mediaUrl,
                status = MessageStatus.SENT,
                isMine = true
            )

            chatDao.insertMessage(messageEntity)
            val previewText = if (!mediaUrl.isNullOrBlank()) "📷 Foto" else text
            chatDao.updateChatLastMessage(
                chatId = chatId,
                text = previewText,
                timestampMs = nowMs,
                timestampIso = nowIso,
                unreadIncrement = 0
            )

            Result.success(messageEntity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMessageStatus(messageId: String, status: MessageStatus) {
        chatDao.updateMessageStatus(messageId, status)
    }

    override suspend fun markChatAsRead(chatId: String) {
        chatDao.markChatAsRead(chatId)
    }

    private fun ChatEntity.toDomain(): ChatConversation {
        return ChatConversation(
            id = id,
            name = name,
            cityRegion = cityRegion,
            lastMessageText = lastMessageText,
            lastMessageTimestampMs = lastMessageTimestampMs,
            lastMessageTimestampIso = lastMessageTimestampIso.ifBlank { formatMsToIso(lastMessageTimestampMs) },
            avatarInitial = avatarInitial,
            avatarUrl = avatarUrl,
            unreadCount = unreadCount,
            chatType = chatType,
            isOnline = isOnline
        )
    }

    private fun MessageEntity.toDomain(): ChatMessage {
        return ChatMessage(
            id = id,
            chatId = chatId,
            senderUserId = senderUserId,
            senderName = senderName,
            text = text,
            timestampMs = timestampMs,
            timestampIso = timestampIso.ifBlank { formatMsToIso(timestampMs) },
            mediaUrl = mediaUrl,
            status = status,
            isMine = isMine
        )
    }
}
