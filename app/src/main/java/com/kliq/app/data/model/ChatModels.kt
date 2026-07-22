package com.kliq.app.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class ChatType {
    PRIVATE,
    PUBLIC_CITY
}

enum class MessageStatus {
    SENT,
    DELIVERED,
    READ
}

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
    val status: MessageStatus = MessageStatus.SENT,
    val isMine: Boolean,
    val dateHeader: String? = null
)

fun formatMsToIso(timestampMs: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(timestampMs))
}
