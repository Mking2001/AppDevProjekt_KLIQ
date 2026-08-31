package com.kliq.app.data.repository

import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.DirectMessage
import com.kliq.app.data.model.MessageStatus
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllChats(): Flow<List<ChatConversation>>

    /** Liefert alle nicht archivierten Chats, angepinnte zuerst. */
    fun getActiveChats(): Flow<List<ChatConversation>>

    /** Liefert alle archivierten Chats. */
    fun getArchivedChats(): Flow<List<ChatConversation>>
    fun getPrivateChats(): Flow<List<ChatConversation>>
    fun getPublicCityChats(cityRegion: String? = null): Flow<List<ChatConversation>>

    /** Liefert die Metadaten eines einzelnen Chats oder null, wenn er nicht existiert. */
    fun getChatById(chatId: String): Flow<ChatConversation?>

    /** Liefert die Gesamtzahl aller ungelesenen Nachrichten über alle aktiven Chats. */
    fun getTotalUnreadCount(): Flow<Int>

    /**
     * Legt einen Chat an, falls unter der ID noch keiner existiert.
     * Wird beim Einstieg in einen Chat aus einem Nutzerprofil oder Deep Link benoetigt.
     *
     * @return Der bestehende oder neu angelegte Chat.
     */
    suspend fun createChatIfMissing(
        chatId: String,
        name: String,
        chatType: com.kliq.app.data.model.ChatType,
        cityRegion: String? = null,
        avatarInitial: String? = null
    ): Result<ChatConversation>

    fun getMessagesForChat(chatId: String): Flow<List<ChatMessage>>
    fun searchMessagesInChat(chatId: String, query: String): Flow<List<ChatMessage>>
    suspend fun syncAllChats(): Result<Unit>
    suspend fun syncAllChatsAndMessages(currentUserId: String): Result<Unit>
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
        mediaUrl: String?,
        messageType: com.kliq.app.data.model.MessageType = com.kliq.app.data.model.MessageType.IMAGE,
        thumbnailUrl: String? = null,
        aspectRatio: Float = 1.0f,
        mediaWidth: Int = 0,
        mediaHeight: Int = 0,
        captionText: String? = null
    ): Result<ChatMessage>

    suspend fun sendVoiceMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        audioUrl: String,
        audioDurationMs: Long
    ): Result<ChatMessage>

    suspend fun updateMessageStatus(messageId: String, status: MessageStatus)
    suspend fun markMessageAsDelivered(messageId: String)
    suspend fun markMessageAsRead(messageId: String)
    suspend fun markAllSentMessagesAsDelivered(chatId: String)
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

    suspend fun sendDirectMediaMessage(
        senderId: String,
        receiverId: String,
        mediaUrl: String,
        messageType: com.kliq.app.data.model.MessageType = com.kliq.app.data.model.MessageType.IMAGE,
        thumbnailUrl: String? = null,
        aspectRatio: Float = 1.0f,
        mediaWidth: Int = 0,
        mediaHeight: Int = 0,
        captionText: String? = null
    ): Result<DirectMessage>

    suspend fun sendDirectVoiceMessage(
        senderId: String,
        receiverId: String,
        audioUrl: String,
        audioDurationMs: Long
    ): Result<DirectMessage>

    suspend fun receiveDirectMessage(message: DirectMessage): Result<Unit>
    suspend fun syncDirectMessages(userId: String, targetUserId: String): Result<Unit>
    suspend fun updateDirectMessageStatus(messageId: String, status: MessageStatus)
    suspend fun markDirectMessageAsDelivered(messageId: String)
    suspend fun markDirectMessageAsRead(messageId: String)
    suspend fun markDirectConversationAsDelivered(senderId: String, receiverId: String)
    suspend fun markDirectConversationAsRead(senderId: String, receiverId: String)
    fun getCityChatForLocation(location: com.kliq.app.data.model.LocationData): Flow<com.kliq.app.data.model.ChatListItem>
    suspend fun syncPublicCityMessages(chatId: String): Result<Unit>
    suspend fun joinPublicCityChat(chatId: String): Result<Unit>
    suspend fun archiveChat(chatId: String, isArchived: Boolean = true)
    suspend fun deleteChat(chatId: String)
    suspend fun updateChatName(chatId: String, name: String)
    suspend fun createGroupChat(name: String, description: String, imageUrl: String?, memberUserIds: List<String>): Result<String>
}
