package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.dao.DirectMessageDao
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.DirectMessageEntity
import com.kliq.app.data.local.entities.MessageEntity
import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.DirectMessage
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.model.formatMsToIso
import com.kliq.app.data.remote.KliqApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val directMessageDao: DirectMessageDao,
    private val apiService: KliqApiService? = null
) : ChatRepository {

    override fun getAllChats(): Flow<List<ChatConversation>> {
        return chatDao.getAllChats().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getPrivateChats(): Flow<List<ChatConversation>> {
        return chatDao.getPrivateChats().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getPublicCityChats(cityRegion: String?): Flow<List<ChatConversation>> {
        return chatDao.getPublicCityChats(cityRegion).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getMessagesForChat(chatId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForChat(chatId).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override fun searchMessagesInChat(chatId: String, query: String): Flow<List<ChatMessage>> {
        return chatDao.searchMessagesInChat(chatId, query).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun syncChatMessages(chatId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
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
    ): Result<ChatMessage> = withContext(Dispatchers.IO) {
        sendInternalMessage(
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
        mediaUrl: String?,
        messageType: com.kliq.app.data.model.MessageType,
        thumbnailUrl: String?,
        aspectRatio: Float,
        mediaWidth: Int,
        mediaHeight: Int,
        captionText: String?
    ): Result<ChatMessage> = withContext(Dispatchers.IO) {
        sendInternalMessage(
            chatId = chatId,
            senderUserId = senderUserId,
            senderName = senderName,
            text = text,
            mediaUrl = mediaUrl,
            messageType = messageType,
            thumbnailUrl = thumbnailUrl,
            aspectRatio = aspectRatio,
            mediaWidth = mediaWidth,
            mediaHeight = mediaHeight,
            captionText = captionText
        )
    }

    override suspend fun sendVoiceMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        audioUrl: String,
        audioDurationMs: Long
    ): Result<ChatMessage> = withContext(Dispatchers.IO) {
        sendInternalMessage(
            chatId = chatId,
            senderUserId = senderUserId,
            senderName = senderName,
            text = "🎤 Sprachnachricht",
            mediaUrl = audioUrl,
            messageType = com.kliq.app.data.model.MessageType.VOICE,
            audioDurationMs = audioDurationMs
        )
    }

    private suspend fun sendInternalMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        text: String,
        mediaUrl: String?,
        messageType: com.kliq.app.data.model.MessageType = if (mediaUrl.isNullOrBlank()) com.kliq.app.data.model.MessageType.TEXT else com.kliq.app.data.model.MessageType.IMAGE,
        thumbnailUrl: String? = null,
        aspectRatio: Float = 1.0f,
        mediaWidth: Int = 0,
        mediaHeight: Int = 0,
        captionText: String? = null,
        audioDurationMs: Long = 0L
    ): Result<ChatMessage> = withContext(Dispatchers.IO) {
        try {
            val nowMs = System.currentTimeMillis()
            val nowIso = formatMsToIso(nowMs)

            val messageEntity = MessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                senderUserId = senderUserId,
                senderName = senderName,
                text = text.ifBlank { captionText ?: "" },
                timestampMs = nowMs,
                timestampIso = nowIso,
                mediaUrl = mediaUrl,
                messageType = messageType,
                thumbnailUrl = thumbnailUrl,
                aspectRatio = aspectRatio,
                mediaWidth = mediaWidth,
                mediaHeight = mediaHeight,
                caption = captionText,
                audioDurationMs = audioDurationMs,
                status = MessageStatus.SENT,
                isMine = true
            )

            chatDao.insertMessage(messageEntity)
            val previewText = when (messageType) {
                com.kliq.app.data.model.MessageType.IMAGE -> "📷 Foto"
                com.kliq.app.data.model.MessageType.VOICE -> "🎤 Sprachnachricht"
                else -> text
            }
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

    override suspend fun updateMessageStatus(messageId: String, status: MessageStatus) = withContext(Dispatchers.IO) {
        chatDao.updateMessageStatus(messageId, status)
    }

    override suspend fun markMessageAsDelivered(messageId: String) = withContext(Dispatchers.IO) {
        chatDao.markMessageAsDelivered(messageId = messageId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markMessageAsRead(messageId: String) = withContext(Dispatchers.IO) {
        chatDao.markMessageAsRead(messageId = messageId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markAllSentMessagesAsDelivered(chatId: String) = withContext(Dispatchers.IO) {
        chatDao.markAllSentMessagesAsDelivered(chatId = chatId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markChatAsRead(chatId: String) = withContext(Dispatchers.IO) {
        chatDao.markChatAsRead(chatId)
    }

    override fun getDirectMessages(currentUserId: String, targetUserId: String): Flow<List<DirectMessage>> {
        return directMessageDao.getDirectMessagesBetweenUsers(currentUserId, targetUserId).map { entities ->
            entities.map { entity ->
                entity.toDomain(isMine = entity.senderId == currentUserId)
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getUnreadDirectMessages(userId: String): Flow<List<DirectMessage>> {
        return directMessageDao.getUnreadDirectMessages(userId).map { entities ->
            entities.map { it.toDomain(isMine = false) }
        }.flowOn(Dispatchers.IO)
    }

    override fun getUnreadCountForUser(userId: String): Flow<Int> {
        return directMessageDao.getUnreadCountForUser(userId).flowOn(Dispatchers.IO)
    }

    override suspend fun sendDirectMessage(
        senderId: String,
        receiverId: String,
        text: String,
        isEncrypted: Boolean,
        mediaUrl: String?
    ): Result<DirectMessage> = withContext(Dispatchers.IO) {
        try {
            val nowMs = System.currentTimeMillis()
            val nowIso = formatMsToIso(nowMs)
            val msgId = UUID.randomUUID().toString()

            val entity = DirectMessageEntity(
                messageId = msgId,
                senderId = senderId,
                receiverId = receiverId,
                text = text,
                timestamp = nowMs,
                timestampIso = nowIso,
                deliveryStatus = MessageStatus.SENT,
                isEncrypted = isEncrypted,
                encryptionAlgorithm = if (isEncrypted) "AES-256-GCM" else "NONE",
                mediaUrl = mediaUrl
            )

            directMessageDao.insertDirectMessage(entity)
            Result.success(entity.toDomain(isMine = true))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun receiveDirectMessage(message: DirectMessage): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val entity = DirectMessageEntity(
                messageId = message.messageId,
                senderId = message.senderId,
                receiverId = message.receiverId,
                text = message.text,
                timestamp = message.timestamp,
                timestampIso = message.timestampIso,
                deliveryStatus = MessageStatus.DELIVERED,
                isEncrypted = message.isEncrypted,
                encryptionAlgorithm = message.encryptionAlgorithm,
                mediaUrl = message.mediaUrl
            )
            directMessageDao.insertDirectMessage(entity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncDirectMessages(userId: String, targetUserId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCityChatForLocation(location: com.kliq.app.data.model.LocationData): Flow<com.kliq.app.data.model.ChatListItem> {
        val resolvedConfig = com.kliq.app.data.util.CityChatLocationMapper.resolveCityForLocation(location)
        val distance = com.kliq.app.data.util.CityChatLocationMapper.calculateDistanceInKm(
            location.latitude, location.longitude,
            resolvedConfig.latitude, resolvedConfig.longitude
        )
        val item = com.kliq.app.data.util.CityChatLocationMapper.buildCityChatListItem(
            config = resolvedConfig,
            distanceKm = distance,
            isGpsAssigned = true
        )
        return kotlinx.coroutines.flow.flowOf(item).flowOn(Dispatchers.IO)
    }

    override suspend fun syncPublicCityMessages(chatId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDirectMessageStatus(messageId: String, status: MessageStatus) = withContext(Dispatchers.IO) {
        directMessageDao.updateDeliveryStatus(messageId, status)
    }

    override suspend fun markDirectMessageAsDelivered(messageId: String) = withContext(Dispatchers.IO) {
        directMessageDao.markDirectMessageAsDelivered(messageId = messageId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markDirectMessageAsRead(messageId: String) = withContext(Dispatchers.IO) {
        directMessageDao.markDirectMessageAsRead(messageId = messageId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markDirectConversationAsDelivered(senderId: String, receiverId: String) = withContext(Dispatchers.IO) {
        directMessageDao.markConversationAsDelivered(senderId = senderId, receiverId = receiverId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markDirectConversationAsRead(senderId: String, receiverId: String) = withContext(Dispatchers.IO) {
        directMessageDao.markConversationAsRead(senderId = senderId, receiverId = receiverId)
    }

    override suspend fun joinPublicCityChat(chatId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendDirectMediaMessage(
        senderId: String,
        receiverId: String,
        mediaUrl: String,
        messageType: com.kliq.app.data.model.MessageType,
        thumbnailUrl: String?,
        aspectRatio: Float,
        mediaWidth: Int,
        mediaHeight: Int,
        captionText: String?
    ): Result<DirectMessage> = withContext(Dispatchers.IO) {
        try {
            val nowMs = System.currentTimeMillis()
            val nowIso = formatMsToIso(nowMs)
            val msgId = UUID.randomUUID().toString()

            val entity = DirectMessageEntity(
                messageId = msgId,
                senderId = senderId,
                receiverId = receiverId,
                text = captionText ?: "📷 Foto",
                timestamp = nowMs,
                timestampIso = nowIso,
                deliveryStatus = MessageStatus.SENT,
                isEncrypted = true,
                encryptionAlgorithm = "AES-256-GCM",
                mediaUrl = mediaUrl,
                messageType = messageType,
                thumbnailUrl = thumbnailUrl,
                aspectRatio = aspectRatio,
                mediaWidth = mediaWidth,
                mediaHeight = mediaHeight,
                caption = captionText
            )

            directMessageDao.insertDirectMessage(entity)
            Result.success(entity.toDomain(isMine = true))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendDirectVoiceMessage(
        senderId: String,
        receiverId: String,
        audioUrl: String,
        audioDurationMs: Long
    ): Result<DirectMessage> = withContext(Dispatchers.IO) {
        try {
            val nowMs = System.currentTimeMillis()
            val nowIso = formatMsToIso(nowMs)
            val msgId = UUID.randomUUID().toString()

            val entity = DirectMessageEntity(
                messageId = msgId,
                senderId = senderId,
                receiverId = receiverId,
                text = "🎤 Sprachnachricht",
                timestamp = nowMs,
                timestampIso = nowIso,
                deliveryStatus = MessageStatus.SENT,
                isEncrypted = true,
                encryptionAlgorithm = "AES-256-GCM",
                mediaUrl = audioUrl,
                messageType = com.kliq.app.data.model.MessageType.VOICE,
                audioDurationMs = audioDurationMs
            )

            directMessageDao.insertDirectMessage(entity)
            Result.success(entity.toDomain(isMine = true))
        } catch (e: Exception) {
            Result.failure(e)
        }
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
            messageType = messageType,
            thumbnailUrl = thumbnailUrl,
            aspectRatio = aspectRatio,
            mediaWidth = mediaWidth,
            mediaHeight = mediaHeight,
            captionText = caption,
            audioDurationMs = audioDurationMs,
            status = status,
            deliveredAtMs = deliveredAtMs,
            readAtMs = readAtMs,
            isMine = isMine
        )
    }

    private fun DirectMessageEntity.toDomain(isMine: Boolean): DirectMessage {
        return DirectMessage(
            messageId = messageId,
            senderId = senderId,
            receiverId = receiverId,
            text = text,
            timestamp = timestamp,
            timestampIso = timestampIso.ifBlank { formatMsToIso(timestamp) },
            deliveryStatus = deliveryStatus,
            deliveredAtMs = deliveredAtMs,
            readAtMs = readAtMs,
            isEncrypted = isEncrypted,
            encryptionAlgorithm = encryptionAlgorithm,
            mediaUrl = mediaUrl,
            messageType = messageType,
            thumbnailUrl = thumbnailUrl,
            aspectRatio = aspectRatio,
            mediaWidth = mediaWidth,
            mediaHeight = mediaHeight,
            captionText = caption,
            audioDurationMs = audioDurationMs,
            isMine = isMine
        )
    }
}
