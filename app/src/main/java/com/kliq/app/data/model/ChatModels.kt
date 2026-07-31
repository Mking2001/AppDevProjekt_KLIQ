package com.kliq.app.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class ChatType {
    PRIVATE,
    PUBLIC_CITY
}

enum class MessageType {
    TEXT,
    IMAGE,
    VOICE
}

enum class MessageStatus {
    SENT,
    DELIVERED,
    READ
}

enum class UserStatus {
    ONLINE,
    OFFLINE,
    AWAY
}

data class LastMessage(
    val text: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val timestampIso: String = formatMsToIso(timestampMs),
    val senderName: String? = null,
    val isRead: Boolean = false
)

data class ChatListItem(
    val id: String,
    val title: String,
    val cityRegion: String? = null,
    val lastMessage: LastMessage,
    val avatarInitial: String,
    val avatarUrl: String? = null,
    val unreadCount: Int = 0,
    val chatType: ChatType,
    val userStatus: UserStatus = UserStatus.OFFLINE,
    val distanceKm: Double? = null,
    val onlineMembersCount: Int = 0,
    val isGpsAssigned: Boolean = false
)

data class ChatConversation(
    val id: String,
    val name: String,
    val cityRegion: String? = null,
    val lastMessageText: String,
    val lastMessageTimestampMs: Long,
    val lastMessageTimestampIso: String = formatMsToIso(lastMessageTimestampMs),
    val avatarInitial: String,
    val avatarUrl: String? = null,
    val unreadCount: Int = 0,
    val chatType: ChatType,
    val isOnline: Boolean = false
)

fun ChatConversation.toChatListItem(): ChatListItem {
    return ChatListItem(
        id = id,
        title = name,
        cityRegion = cityRegion,
        lastMessage = LastMessage(
            text = lastMessageText,
            timestampMs = lastMessageTimestampMs,
            timestampIso = lastMessageTimestampIso,
            isRead = unreadCount == 0
        ),
        avatarInitial = avatarInitial,
        avatarUrl = avatarUrl,
        unreadCount = unreadCount,
        chatType = chatType,
        userStatus = if (isOnline) UserStatus.ONLINE else UserStatus.OFFLINE
    )
}

fun ChatListItem.toChatConversation(): ChatConversation {
    return ChatConversation(
        id = id,
        name = title,
        cityRegion = cityRegion,
        lastMessageText = lastMessage.text,
        lastMessageTimestampMs = lastMessage.timestampMs,
        lastMessageTimestampIso = lastMessage.timestampIso,
        avatarInitial = avatarInitial,
        avatarUrl = avatarUrl,
        unreadCount = unreadCount,
        chatType = chatType,
        isOnline = userStatus == UserStatus.ONLINE
    )
}


data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderUserId: String,
    val senderName: String,
    val senderAvatarUrl: String? = null,
    val text: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val timestampIso: String = formatMsToIso(timestampMs),
    val mediaUrl: String? = null,
    val messageType: MessageType = if (mediaUrl.isNullOrBlank()) MessageType.TEXT else MessageType.IMAGE,
    val thumbnailUrl: String? = null,
    val aspectRatio: Float = 1.0f,
    val mediaWidth: Int = 0,
    val mediaHeight: Int = 0,
    val captionText: String? = null,
    val audioDurationMs: Long = 0L,
    val status: MessageStatus = MessageStatus.SENT,
    val deliveredAtMs: Long? = null,
    val readAtMs: Long? = null,
    val isMine: Boolean,
    val dateHeader: String? = null
)

data class DirectMessage(
    val messageId: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val timestampIso: String = formatMsToIso(timestamp),
    val deliveryStatus: MessageStatus = MessageStatus.SENT,
    val deliveredAtMs: Long? = null,
    val readAtMs: Long? = null,
    val isEncrypted: Boolean = true,
    val encryptionAlgorithm: String = "AES-256-GCM",
    val mediaUrl: String? = null,
    val messageType: MessageType = if (mediaUrl.isNullOrBlank()) MessageType.TEXT else MessageType.IMAGE,
    val thumbnailUrl: String? = null,
    val aspectRatio: Float = 1.0f,
    val mediaWidth: Int = 0,
    val mediaHeight: Int = 0,
    val captionText: String? = null,
    val audioDurationMs: Long = 0L,
    val isMine: Boolean = false
)

fun formatMsToIso(timestampMs: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(timestampMs))
}
