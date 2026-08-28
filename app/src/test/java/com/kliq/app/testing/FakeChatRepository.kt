This branch has conflicts that must be resolved
Use the web editor or the command line to resolve conflicts before continuing.

app/build.gradle.kts
app/src/main/java/com/kliq/app/KliqApplication.kt
app/src/main/java/com/kliq/app/ui/screens/map/MapViewModel.kt
build.gradle.ktspackage com.kliq.app.testing

import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatListItem
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.DirectMessage
import com.kliq.app.data.model.LastMessage
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.model.MessageType
import com.kliq.app.data.model.formatMsToIso
import com.kliq.app.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * In-Memory-Test-Double des [ChatRepository].
 *
 * Ersetzt Room in Unit-Tests und verhält sich wie die echte Implementierung:
 * Schreibvorgänge verändern den Zustand, Leseflüsse geben die Änderungen aus.
 * Dadurch prüfen die Tests das tatsächliche Verhalten und nicht nur
 * die Aufrufreihenfolge einzelner Methoden.
 */
class FakeChatRepository(
    initialChats: List<ChatConversation> = emptyList(),
    initialMessages: List<ChatMessage> = emptyList()
) : ChatRepository {

    private val chats = MutableStateFlow(initialChats)
    private val messages = MutableStateFlow(initialMessages)

    /** Zählt Aufrufe von [markChatAsRead] je Chat-ID. */
    val markedAsReadChatIds = mutableListOf<String>()

    /** Protokolliert Archivierungsaufrufe als Paar aus Chat-ID und Zielzustand. */
    val archiveCalls = mutableListOf<Pair<String, Boolean>>()

    /** Protokolliert gelöschte Chat-IDs. */
    val deletedChatIds = mutableListOf<String>()

    private var messageCounter = 0

    override fun getAllChats(): Flow<List<ChatConversation>> = chats

    override fun getActiveChats(): Flow<List<ChatConversation>> =
        chats.map { list -> list.filterNot { it.isArchived } }

    override fun getArchivedChats(): Flow<List<ChatConversation>> =
        chats.map { list -> list.filter { it.isArchived } }

    override fun getPrivateChats(): Flow<List<ChatConversation>> =
        chats.map { list -> list.filter { it.chatType == ChatType.PRIVATE } }

    override fun getPublicCityChats(cityRegion: String?): Flow<List<ChatConversation>> =
        chats.map { list ->
            list.filter { it.chatType == ChatType.PUBLIC_CITY && (cityRegion == null || it.cityRegion == cityRegion) }
        }

    override fun getChatById(chatId: String): Flow<ChatConversation?> =
        chats.map { list -> list.find { it.id == chatId } }

    override suspend fun createChatIfMissing(
        chatId: String,
        name: String,
        chatType: ChatType,
        cityRegion: String?,
        avatarInitial: String?
    ): Result<ChatConversation> {
        chats.value.find { it.id == chatId }?.let { return Result.success(it) }

        val nowMs = System.currentTimeMillis()
        val created = ChatConversation(
            id = chatId,
            name = name,
            cityRegion = cityRegion,
            lastMessageText = "",
            lastMessageTimestampMs = nowMs,
            avatarInitial = avatarInitial ?: name.take(1).uppercase(),
            chatType = chatType
        )
        chats.value = chats.value + created
        return Result.success(created)
    }

    override fun getMessagesForChat(chatId: String): Flow<List<ChatMessage>> =
        messages.map { list -> list.filter { it.chatId == chatId }.sortedBy { it.timestampMs } }

    override fun searchMessagesInChat(chatId: String, query: String): Flow<List<ChatMessage>> =
        messages.map { list ->
            list.filter { it.chatId == chatId && it.text.contains(query, ignoreCase = true) }
        }

    override suspend fun syncChatMessages(chatId: String): Result<Unit> = Result.success(Unit)

    override suspend fun sendTextMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        text: String
    ): Result<ChatMessage> = appendMessage(
        chatId = chatId,
        senderUserId = senderUserId,
        senderName = senderName,
        text = text
    )

    override suspend fun sendMediaMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        text: String,
        mediaUrl: String?,
        messageType: MessageType,
        thumbnailUrl: String?,
        aspectRatio: Float,
        mediaWidth: Int,
        mediaHeight: Int,
        captionText: String?
    ): Result<ChatMessage> = appendMessage(
        chatId = chatId,
        senderUserId = senderUserId,
        senderName = senderName,
        text = text,
        mediaUrl = mediaUrl,
        messageType = messageType,
        captionText = captionText
    )

    override suspend fun sendVoiceMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        audioUrl: String,
        audioDurationMs: Long
    ): Result<ChatMessage> = appendMessage(
        chatId = chatId,
        senderUserId = senderUserId,
        senderName = senderName,
        text = "Sprachnachricht",
        mediaUrl = audioUrl,
        messageType = MessageType.VOICE,
        audioDurationMs = audioDurationMs
    )

    private fun appendMessage(
        chatId: String,
        senderUserId: String,
        senderName: String,
        text: String,
        mediaUrl: String? = null,
        messageType: MessageType = MessageType.TEXT,
        captionText: String? = null,
        audioDurationMs: Long = 0L
    ): Result<ChatMessage> {
        val nowMs = System.currentTimeMillis() + messageCounter
        val message = ChatMessage(
            id = "fake_msg_${messageCounter++}",
            chatId = chatId,
            senderUserId = senderUserId,
            senderName = senderName,
            text = text,
            timestampMs = nowMs,
            timestampIso = formatMsToIso(nowMs),
            mediaUrl = mediaUrl,
            messageType = messageType,
            captionText = captionText,
            audioDurationMs = audioDurationMs,
            status = MessageStatus.SENT,
            isMine = true
        )
        messages.value = messages.value + message

        chats.value = chats.value.map { chat ->
            if (chat.id == chatId) {
                chat.copy(lastMessageText = text, lastMessageTimestampMs = nowMs)
            } else {
                chat
            }
        }
        return Result.success(message)
    }

    override suspend fun updateMessageStatus(messageId: String, status: MessageStatus) {
        messages.value = messages.value.map { if (it.id == messageId) it.copy(status = status) else it }
    }

    override suspend fun markMessageAsDelivered(messageId: String) =
        updateMessageStatus(messageId, MessageStatus.DELIVERED)

    override suspend fun markMessageAsRead(messageId: String) =
        updateMessageStatus(messageId, MessageStatus.READ)

    override suspend fun markAllSentMessagesAsDelivered(chatId: String) {
        messages.value = messages.value.map { message ->
            if (message.chatId == chatId && message.isMine && message.status == MessageStatus.SENT) {
                message.copy(status = MessageStatus.DELIVERED)
            } else {
                message
            }
        }
    }

    override suspend fun markChatAsRead(chatId: String) {
        markedAsReadChatIds += chatId
        chats.value = chats.value.map { if (it.id == chatId) it.copy(unreadCount = 0) else it }
    }

    override suspend fun archiveChat(chatId: String, isArchived: Boolean) {
        archiveCalls += chatId to isArchived
        chats.value = chats.value.map { if (it.id == chatId) it.copy(isArchived = isArchived) else it }
    }

    override suspend fun deleteChat(chatId: String) {
        deletedChatIds += chatId
        chats.value = chats.value.filterNot { it.id == chatId }
        messages.value = messages.value.filterNot { it.chatId == chatId }
    }

    // -----------------------------------------------------------------
    // Direktnachrichten-Pfad: in diesen Tests nicht verwendet
    // -----------------------------------------------------------------

    override fun getDirectMessages(currentUserId: String, targetUserId: String): Flow<List<DirectMessage>> =
        flowOf(emptyList())

    override fun getUnreadDirectMessages(userId: String): Flow<List<DirectMessage>> = flowOf(emptyList())

    override fun getUnreadCountForUser(userId: String): Flow<Int> = flowOf(0)

    override suspend fun sendDirectMessage(
        senderId: String,
        receiverId: String,
        text: String,
        isEncrypted: Boolean,
        mediaUrl: String?
    ): Result<DirectMessage> = Result.success(
        DirectMessage(messageId = "fake_dm", senderId = senderId, receiverId = receiverId, text = text)
    )

    override suspend fun sendDirectMediaMessage(
        senderId: String,
        receiverId: String,
        mediaUrl: String,
        messageType: MessageType,
        thumbnailUrl: String?,
        aspectRatio: Float,
        mediaWidth: Int,
        mediaHeight: Int,
        captionText: String?
    ): Result<DirectMessage> = Result.success(
        DirectMessage(messageId = "fake_dm_media", senderId = senderId, receiverId = receiverId, text = "")
    )

    override suspend fun sendDirectVoiceMessage(
        senderId: String,
        receiverId: String,
        audioUrl: String,
        audioDurationMs: Long
    ): Result<DirectMessage> = Result.success(
        DirectMessage(messageId = "fake_dm_voice", senderId = senderId, receiverId = receiverId, text = "")
    )

    override suspend fun receiveDirectMessage(message: DirectMessage): Result<Unit> = Result.success(Unit)

    override suspend fun syncDirectMessages(userId: String, targetUserId: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun updateDirectMessageStatus(messageId: String, status: MessageStatus) = Unit

    override suspend fun markDirectMessageAsDelivered(messageId: String) = Unit

    override suspend fun markDirectMessageAsRead(messageId: String) = Unit

    override suspend fun markDirectConversationAsDelivered(senderId: String, receiverId: String) = Unit

    override suspend fun markDirectConversationAsRead(senderId: String, receiverId: String) = Unit

    override fun getCityChatForLocation(location: LocationData): Flow<ChatListItem> = flowOf(
        ChatListItem(
            id = "pub_klagenfurt",
            title = "Klagenfurt - Tonight",
            lastMessage = LastMessage(text = ""),
            avatarInitial = "K",
            chatType = ChatType.PUBLIC_CITY
        )
    )

    override suspend fun syncPublicCityMessages(chatId: String): Result<Unit> = Result.success(Unit)

    override suspend fun joinPublicCityChat(chatId: String): Result<Unit> = Result.success(Unit)
}
