package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.dao.DirectMessageDao
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.DirectMessageEntity
import com.kliq.app.data.local.entities.MessageEntity
import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.DirectMessage
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.model.formatMsToIso
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.generated.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    private val currentUserProvider: com.kliq.app.domain.CurrentUserProvider? = null,
    private val apiService: KliqApiService? = null,
    private val kliqConnector: com.kliq.app.data.generated.KliqConnectorConnector? = null,
    private val notificationHelper: com.kliq.app.service.notification.NotificationHelper? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ChatRepository {

    override fun getAllChats(): Flow<List<ChatConversation>> {
        return chatDao.getAllChats().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override fun getActiveChats(): Flow<List<ChatConversation>> {
        return chatDao.getActiveChats().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override fun getArchivedChats(): Flow<List<ChatConversation>> {
        return chatDao.getArchivedChats().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override fun getPrivateChats(): Flow<List<ChatConversation>> {
        return chatDao.getPrivateChats().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override fun getPublicCityChats(cityRegion: String?): Flow<List<ChatConversation>> {
        return chatDao.getPublicCityChats(cityRegion).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override fun getChatById(chatId: String): Flow<ChatConversation?> {
        return chatDao.getChatById(chatId).map { entity ->
            entity?.toDomain()
        }.flowOn(ioDispatcher)
    }

    override fun getTotalUnreadCount(): Flow<Int> {
        return chatDao.getTotalUnreadCount().flowOn(ioDispatcher)
    }

    override suspend fun createChatIfMissing(
        chatId: String,
        name: String,
        chatType: com.kliq.app.data.model.ChatType,
        cityRegion: String?,
        avatarInitial: String?
    ): Result<ChatConversation> = withContext(ioDispatcher) {
        try {
            val existing = chatDao.getChatById(chatId).first()
            if (existing != null) {
                if ((existing.name == "Direktnachricht" || existing.name == "Kliq Chat" || existing.name.isBlank()) && name.isNotBlank() && name != "Direktnachricht" && name != "Kliq Chat") {
                    val newInitial = avatarInitial?.takeIf { it.isNotBlank() } ?: name.trim().take(1).uppercase().ifBlank { "K" }
                    chatDao.updateChatName(chatId, name, newInitial)
                    return@withContext Result.success(existing.copy(name = name, avatarInitial = newInitial).toDomain())
                }
                return@withContext Result.success(existing.toDomain())
            }

            val nowMs = System.currentTimeMillis()
            val entity = ChatEntity(
                id = chatId,
                name = name,
                cityRegion = cityRegion,
                lastMessageText = "",
                lastMessageTimestampMs = nowMs,
                lastMessageTimestampIso = formatMsToIso(nowMs),
                avatarInitial = avatarInitial?.takeIf { it.isNotBlank() }
                    ?: name.trim().take(1).uppercase().ifBlank { "K" },
                chatType = chatType
            )
            chatDao.insertChat(entity)

            kliqConnector?.let { connector ->
                try {
                    connector.createChat.execute(
                        id = chatId,
                        name = name,
                        chatType = chatType.name,
                        lastMessageText = "",
                        lastMessageTimestampMs = nowMs,
                        avatarInitial = entity.avatarInitial
                    ) {
                        this.cityRegion = cityRegion
                    }
                } catch (ignored: Exception) { }
            }

            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMessagesForChat(chatId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForChat(chatId).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override fun searchMessagesInChat(chatId: String, query: String): Flow<List<ChatMessage>> {
        return chatDao.searchMessagesInChat(chatId, query).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(ioDispatcher)
    }

    override suspend fun syncAllChats(): Result<Unit> = withContext(ioDispatcher) {
        val currentUserId = currentUserProvider?.userId() ?: ""
        if (currentUserId.isNotBlank()) {
            syncAllChatsAndMessages(currentUserId)
        } else {
            try {
                kliqConnector?.let { connector ->
                    val response = connector.getAllChats.execute()
                    val remoteChats = response.data.chats.map { c ->
                        val type = try { ChatType.valueOf(c.chatType) } catch (e: Exception) { ChatType.PUBLIC_CITY }
                        ChatEntity(
                            id = c.id,
                            name = c.name,
                            cityRegion = c.cityRegion,
                            lastMessageText = c.lastMessageText,
                            lastMessageTimestampMs = c.lastMessageTimestampMs,
                            lastMessageTimestampIso = c.lastMessageTimestampIso,
                            avatarInitial = c.avatarInitial,
                            avatarUrl = c.avatarUrl,
                            unreadCount = c.unreadCount,
                            chatType = type,
                            isOnline = c.isOnline,
                            isPinned = c.isPinned,
                            isMuted = c.isMuted,
                            isArchived = c.isArchived
                        )
                    }
                    if (remoteChats.isNotEmpty()) {
                        chatDao.insertChats(remoteChats)
                    }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun syncAllChatsAndMessages(currentUserId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            kliqConnector?.let { connector ->
                val allChatsResponse = connector.getAllChats.execute()
                val remoteChats = allChatsResponse.data.chats

                for (cloudChat in remoteChats) {
                    val chatType = try { ChatType.valueOf(cloudChat.chatType) } catch (e: Exception) { ChatType.PUBLIC_CITY }

                    if (chatType == ChatType.PUBLIC_CITY) {
                        val localExisting = chatDao.getChatById(cloudChat.id).first()
                        val chatEntity = ChatEntity(
                            id = cloudChat.id,
                            name = cloudChat.name,
                            cityRegion = cloudChat.cityRegion,
                            lastMessageText = if (localExisting != null && localExisting.lastMessageText.isNotBlank()) localExisting.lastMessageText else cloudChat.lastMessageText,
                            lastMessageTimestampMs = if (localExisting != null && localExisting.lastMessageTimestampMs > 0) localExisting.lastMessageTimestampMs else cloudChat.lastMessageTimestampMs,
                            lastMessageTimestampIso = if (localExisting != null && localExisting.lastMessageTimestampIso.isNotBlank()) localExisting.lastMessageTimestampIso else cloudChat.lastMessageTimestampIso,
                            avatarInitial = cloudChat.avatarInitial,
                            avatarUrl = cloudChat.avatarUrl,
                            unreadCount = localExisting?.unreadCount ?: 0,
                            chatType = chatType,
                            isOnline = cloudChat.isOnline,
                            isPinned = localExisting?.isPinned ?: cloudChat.isPinned,
                            isMuted = localExisting?.isMuted ?: cloudChat.isMuted,
                            isArchived = localExisting?.isArchived ?: cloudChat.isArchived
                        )
                        chatDao.insertChat(chatEntity)

                        val msgsResponse = connector.getMessagesByChat.execute(chatId = cloudChat.id)
                        val msgs = msgsResponse.data.messages
                        for (msg in msgs) {
                            val localMsg = MessageEntity(
                                id = msg.id,
                                chatId = cloudChat.id,
                                senderUserId = msg.senderUserId,
                                senderName = msg.senderName,
                                text = msg.text,
                                timestampMs = msg.timestampMs,
                                timestampIso = msg.timestampIso,
                                mediaUrl = msg.mediaUrl,
                                messageType = try { com.kliq.app.data.model.MessageType.valueOf(msg.messageType ?: "TEXT") } catch (e: Exception) { com.kliq.app.data.model.MessageType.TEXT },
                                thumbnailUrl = msg.thumbnailUrl,
                                caption = msg.caption,
                                audioDurationMs = msg.audioDurationMs ?: 0L,
                                status = try { MessageStatus.valueOf(msg.status) } catch (e: Exception) { MessageStatus.DELIVERED },
                                isMine = msg.senderUserId == currentUserId
                            )
                            chatDao.insertMessage(localMsg)
                        }
                    } else {
                        // Private 1-to-1 chat
                        val msgsResponse = connector.getMessagesByChat.execute(chatId = cloudChat.id)
                        val msgs = msgsResponse.data.messages
                        if (msgs.isNotEmpty()) {
                            val participants = msgs.map { it.senderUserId }.filter { it.isNotBlank() }.distinct()
                            val isUserInvolved = participants.contains(currentUserId) || cloudChat.id.contains(currentUserId)

                            if (isUserInvolved) {
                                val otherParticipantId = participants.firstOrNull { it != currentUserId }
                                    ?: cloudChat.id.removePrefix("chat_").removePrefix("priv_")

                                val otherParticipantName = msgs.firstOrNull { it.senderUserId != currentUserId }?.senderName
                                    ?: if (cloudChat.name != "Chat" && cloudChat.name != "Direktnachricht" && cloudChat.name != "Kliq Chat") cloudChat.name else "Kliq Nutzer"

                                var unreadForMe = 0
                                val localExistingMsgs = chatDao.getMessagesForChat(cloudChat.id).first()

                                for (msg in msgs) {
                                    val isMine = msg.senderUserId == currentUserId
                                    val existingLocal = localExistingMsgs.find { it.id == msg.id }

                                    val isUnread = !isMine && (existingLocal == null || existingLocal.status != MessageStatus.READ)
                                    if (isUnread) {
                                        unreadForMe++
                                    }

                                    val localMsg = MessageEntity(
                                        id = msg.id,
                                        chatId = cloudChat.id,
                                        senderUserId = msg.senderUserId,
                                        senderName = msg.senderName,
                                        text = msg.text,
                                        timestampMs = msg.timestampMs,
                                        timestampIso = msg.timestampIso,
                                        mediaUrl = msg.mediaUrl,
                                        messageType = try { com.kliq.app.data.model.MessageType.valueOf(msg.messageType ?: "TEXT") } catch (e: Exception) { com.kliq.app.data.model.MessageType.TEXT },
                                        thumbnailUrl = msg.thumbnailUrl,
                                        caption = msg.caption,
                                        audioDurationMs = msg.audioDurationMs ?: 0L,
                                        status = if (existingLocal != null && existingLocal.status == MessageStatus.READ) MessageStatus.READ else try { MessageStatus.valueOf(msg.status) } catch (e: Exception) { MessageStatus.DELIVERED },
                                        isMine = isMine
                                    )
                                    chatDao.insertMessage(localMsg)
                                }

                                val latestMsg = msgs.last()
                                val localChat = chatDao.getChatById(cloudChat.id).first()
                                val prevUnread = localChat?.unreadCount ?: 0

                                val privateChatEntity = ChatEntity(
                                    id = cloudChat.id,
                                    name = otherParticipantName,
                                    cityRegion = null,
                                    lastMessageText = latestMsg.text,
                                    lastMessageTimestampMs = latestMsg.timestampMs,
                                    lastMessageTimestampIso = latestMsg.timestampIso,
                                    avatarInitial = otherParticipantName.take(1).uppercase().ifBlank { "U" },
                                    avatarUrl = cloudChat.avatarUrl,
                                    unreadCount = unreadForMe,
                                    chatType = ChatType.PRIVATE,
                                    isOnline = true,
                                    isPinned = localChat?.isPinned ?: false,
                                    isMuted = localChat?.isMuted ?: false,
                                    isArchived = localChat?.isArchived ?: false
                                )
                                chatDao.insertChat(privateChatEntity)

                                // Notification trigger for new unread messages
                                if (unreadForMe > prevUnread && latestMsg.senderUserId != currentUserId) {
                                    notificationHelper?.showChatNotification(
                                        com.kliq.app.data.model.ChatPushPayload(
                                            chatId = cloudChat.id,
                                            senderId = latestMsg.senderUserId,
                                            senderName = latestMsg.senderName,
                                            previewText = latestMsg.text,
                                            notificationType = com.kliq.app.data.model.PushNotificationType.DIRECT_MESSAGE
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncChatMessages(chatId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            kliqConnector?.let { connector ->
                val response = connector.getMessagesByChat.execute(chatId = chatId)
                val remoteMessages = response.data.messages.map { msg ->
                    MessageEntity(
                        id = msg.id,
                        chatId = chatId,
                        senderUserId = msg.senderUserId,
                        senderName = msg.senderName,
                        text = msg.text,
                        timestampMs = msg.timestampMs,
                        timestampIso = msg.timestampIso,
                        mediaUrl = msg.mediaUrl,
                        messageType = try { com.kliq.app.data.model.MessageType.valueOf(msg.messageType ?: "TEXT") } catch (e: Exception) { com.kliq.app.data.model.MessageType.TEXT },
                        thumbnailUrl = msg.thumbnailUrl,
                        caption = msg.caption,
                        audioDurationMs = msg.audioDurationMs ?: 0L,
                        status = try { MessageStatus.valueOf(msg.status) } catch (e: Exception) { MessageStatus.DELIVERED },
                        isMine = msg.isMine
                    )
                }
                if (remoteMessages.isNotEmpty()) {
                    remoteMessages.forEach { chatDao.insertMessage(it) }
                }
            }
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
    ): Result<ChatMessage> = withContext(ioDispatcher) {
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
    ): Result<ChatMessage> = withContext(ioDispatcher) {
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
    ): Result<ChatMessage> = withContext(ioDispatcher) {
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
    ): Result<ChatMessage> = withContext(ioDispatcher) {
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
            val existingChat = chatDao.getChatById(chatId).first()
            val formattedPreview = if (existingChat?.chatType == com.kliq.app.data.model.ChatType.PUBLIC_CITY) {
                val displayName = if (senderName.isNotBlank()) senderName else "Nutzer"
                "$displayName: $previewText"
            } else {
                previewText
            }
            chatDao.updateChatLastMessage(
                chatId = chatId,
                text = formattedPreview,
                timestampMs = nowMs,
                timestampIso = nowIso,
                unreadIncrement = 0
            )

            kliqConnector?.let { connector ->
                try {
                    connector.sendMessage.execute(
                        id = messageEntity.id,
                        chatId = chatId,
                        senderUserId = senderUserId,
                        senderName = senderName,
                        text = messageEntity.text,
                        timestampMs = nowMs,
                        isMine = true
                    ) {
                        this.messageType = messageType.name
                        this.mediaUrl = mediaUrl
                        this.thumbnailUrl = thumbnailUrl
                        this.caption = captionText
                        if (audioDurationMs > 0L) {
                            this.audioDurationMs = audioDurationMs
                        }
                    }
                } catch (ignored: Exception) {
                    // Graceful fallback for offline / mock mode
                }
            }

            Result.success(messageEntity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMessageStatus(messageId: String, status: MessageStatus) = withContext(ioDispatcher) {
        chatDao.updateMessageStatus(messageId, status)
    }

    override suspend fun markMessageAsDelivered(messageId: String) = withContext(ioDispatcher) {
        chatDao.markMessageAsDelivered(messageId = messageId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markMessageAsRead(messageId: String) = withContext(ioDispatcher) {
        chatDao.markMessageAsRead(messageId = messageId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markAllSentMessagesAsDelivered(chatId: String) = withContext(ioDispatcher) {
        chatDao.markAllSentMessagesAsDelivered(chatId = chatId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markChatAsRead(chatId: String) = withContext(ioDispatcher) {
        chatDao.markChatAsRead(chatId)
    }

    override fun getDirectMessages(currentUserId: String, targetUserId: String): Flow<List<DirectMessage>> {
        return directMessageDao.getDirectMessagesBetweenUsers(currentUserId, targetUserId).map { entities ->
            entities.map { entity ->
                entity.toDomain(isMine = entity.senderId == currentUserId)
            }
        }.flowOn(ioDispatcher)
    }

    override fun getUnreadDirectMessages(userId: String): Flow<List<DirectMessage>> {
        return directMessageDao.getUnreadDirectMessages(userId).map { entities ->
            entities.map { it.toDomain(isMine = false) }
        }.flowOn(ioDispatcher)
    }

    override fun getUnreadCountForUser(userId: String): Flow<Int> {
        return directMessageDao.getUnreadCountForUser(userId).flowOn(ioDispatcher)
    }

    override suspend fun sendDirectMessage(
        senderId: String,
        receiverId: String,
        text: String,
        isEncrypted: Boolean,
        mediaUrl: String?
    ): Result<DirectMessage> = withContext(ioDispatcher) {
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

            kliqConnector?.let { connector ->
                try {
                    connector.sendDirectMessage.execute(
                        id = msgId,
                        senderId = senderId,
                        receiverId = receiverId,
                        text = text,
                        timestamp = nowMs
                    ) {
                        this.messageType = com.kliq.app.data.model.MessageType.TEXT.name
                        this.mediaUrl = mediaUrl
                    }
                } catch (ignored: Exception) {
                    // Graceful fallback for offline mode
                }
            }

            Result.success(entity.toDomain(isMine = true))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun receiveDirectMessage(message: DirectMessage): Result<Unit> = withContext(ioDispatcher) {
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

    override suspend fun syncDirectMessages(userId: String, targetUserId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            kliqConnector?.let { connector ->
                val response = connector.getDirectMessages.execute(senderId = userId, receiverId = targetUserId)
                val remoteEntities = response.data.directMessages.map { msg ->
                    DirectMessageEntity(
                        messageId = msg.id,
                        senderId = msg.senderId,
                        receiverId = msg.receiverId,
                        text = msg.text,
                        timestamp = msg.timestamp,
                        timestampIso = formatMsToIso(msg.timestamp),
                        deliveryStatus = try { MessageStatus.valueOf(msg.deliveryStatus) } catch (e: Exception) { MessageStatus.DELIVERED },
                        isEncrypted = true,
                        encryptionAlgorithm = "AES-256-GCM",
                        mediaUrl = msg.mediaUrl,
                        messageType = try { com.kliq.app.data.model.MessageType.valueOf(msg.messageType ?: "TEXT") } catch (e: Exception) { com.kliq.app.data.model.MessageType.TEXT },
                        caption = msg.caption,
                        audioDurationMs = msg.audioDurationMs ?: 0L
                    )
                }
                if (remoteEntities.isNotEmpty()) {
                    directMessageDao.insertDirectMessages(remoteEntities)
                }
            }
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
        return kotlinx.coroutines.flow.flowOf(item).flowOn(ioDispatcher)
    }

    override suspend fun syncPublicCityMessages(chatId: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDirectMessageStatus(messageId: String, status: MessageStatus) = withContext(ioDispatcher) {
        directMessageDao.updateDeliveryStatus(messageId, status)
    }

    override suspend fun markDirectMessageAsDelivered(messageId: String) = withContext(ioDispatcher) {
        directMessageDao.markDirectMessageAsDelivered(messageId = messageId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markDirectMessageAsRead(messageId: String) = withContext(ioDispatcher) {
        directMessageDao.markDirectMessageAsRead(messageId = messageId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markDirectConversationAsDelivered(senderId: String, receiverId: String) = withContext(ioDispatcher) {
        directMessageDao.markConversationAsDelivered(senderId = senderId, receiverId = receiverId, timestampMs = System.currentTimeMillis())
    }

    override suspend fun markDirectConversationAsRead(senderId: String, receiverId: String) = withContext(ioDispatcher) {
        directMessageDao.markConversationAsRead(senderId = senderId, receiverId = receiverId)
    }

    override suspend fun joinPublicCityChat(chatId: String): Result<Unit> = withContext(ioDispatcher) {
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
    ): Result<DirectMessage> = withContext(ioDispatcher) {
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
    ): Result<DirectMessage> = withContext(ioDispatcher) {
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

    override suspend fun archiveChat(chatId: String, isArchived: Boolean) = withContext(ioDispatcher) {
        chatDao.updateArchiveStatus(chatId, isArchived)
    }

    override suspend fun deleteChat(chatId: String) = withContext(ioDispatcher) {
        chatDao.deleteChatById(chatId)
        chatDao.deleteMessagesForChat(chatId)
        val targetId = if (chatId.startsWith("chat_")) chatId.removePrefix("chat_") else chatId
        directMessageDao.deleteAllMessagesForUser(targetId)
    }

    override suspend fun updateChatName(chatId: String, name: String) = withContext(ioDispatcher) {
        val initial = name.trim().take(1).uppercase().ifBlank { "G" }
        chatDao.updateChatName(chatId, name, initial)
    }

    override suspend fun createGroupChat(
        name: String,
        description: String,
        imageUrl: String?,
        memberUserIds: List<String>
    ): Result<String> = withContext(ioDispatcher) {
        try {
            val groupId = "group_${System.currentTimeMillis()}"
            val nowMs = System.currentTimeMillis()
            val initial = name.trim().take(1).uppercase().ifBlank { "G" }
            val entity = ChatEntity(
                id = groupId,
                name = name,
                cityRegion = description.ifBlank { null },
                lastMessageText = "Gruppe erstellt",
                lastMessageTimestampMs = nowMs,
                lastMessageTimestampIso = formatMsToIso(nowMs),
                avatarInitial = initial,
                avatarUrl = imageUrl,
                chatType = ChatType.PUBLIC_CITY
            )
            chatDao.insertChat(entity)
            kliqConnector?.let { connector ->
                try {
                    connector.createChat.execute(
                        id = groupId,
                        name = name,
                        chatType = "GROUP",
                        avatarInitial = initial,
                        lastMessageText = "Gruppe erstellt",
                        lastMessageTimestampMs = nowMs
                    )
                } catch (ignored: Exception) {}
            }
            Result.success(groupId)
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
            isOnline = isOnline,
            isArchived = isArchived,
            isPinned = isPinned
        )
    }

    private fun MessageEntity.toDomain(): ChatMessage {
        val currentUserId = currentUserProvider?.userId()
        val isSenderMe = if (!currentUserId.isNullOrBlank() && senderUserId.isNotBlank()) {
            senderUserId == currentUserId
        } else {
            isMine
        }
        return ChatMessage(
            id = id,
            chatId = chatId,
            senderUserId = senderUserId,
            senderName = if (isSenderMe) "Du" else senderName,
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
            isMine = isSenderMe
        )
    }

    private fun DirectMessageEntity.toDomain(isMine: Boolean = false): DirectMessage {
        val currentUserId = currentUserProvider?.userId()
        val isSenderMe = if (!currentUserId.isNullOrBlank() && senderId.isNotBlank()) {
            senderId == currentUserId
        } else {
            isMine
        }
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
            isMine = isSenderMe
        )
    }
}
