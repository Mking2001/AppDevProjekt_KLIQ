package com.kliq.app.data.model

import java.io.Serializable

enum class PushNotificationType(val rawType: String) {
    DIRECT_MESSAGE("direct_message"),
    CITY_CHAT_MENTION("city_chat_mention");

    companion object {
        fun fromRaw(typeStr: String?): PushNotificationType {
            return when (typeStr?.lowercase()) {
                "city_chat_mention", "city_chat", "group_chat" -> CITY_CHAT_MENTION
                else -> DIRECT_MESSAGE
            }
        }
    }
}

data class ChatPushPayload(
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val previewText: String,
    val notificationType: PushNotificationType = PushNotificationType.DIRECT_MESSAGE,
    val timestampMs: Long = System.currentTimeMillis()
) : Serializable {
    companion object {
        const val KEY_CHAT_ID = "chat_id"
        const val KEY_SENDER_ID = "sender_id"
        const val KEY_SENDER_NAME = "sender_name"
        const val KEY_PREVIEW_TEXT = "preview_text"
        const val KEY_NOTIFICATION_TYPE = "notification_type"

        fun fromMap(data: Map<String, String>): ChatPushPayload {
            val chatId = data[KEY_CHAT_ID] ?: data["chatId"] ?: ""
            val senderId = data[KEY_SENDER_ID] ?: data["senderId"] ?: ""
            val senderName = data[KEY_SENDER_NAME] ?: data["senderName"] ?: data["title"] ?: "Kliq Chat"
            val previewText = data[KEY_PREVIEW_TEXT] ?: data["previewText"] ?: data["message"] ?: data["body"] ?: ""
            val typeStr = data[KEY_NOTIFICATION_TYPE] ?: data["type"]
            val notificationType = PushNotificationType.fromRaw(typeStr)

            return ChatPushPayload(
                chatId = chatId,
                senderId = senderId,
                senderName = senderName,
                previewText = previewText,
                notificationType = notificationType
            )
        }
    }
}
