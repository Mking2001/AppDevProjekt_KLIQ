package com.kliq.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ChatPushPayloadTest {

    @Test
    fun `fromMap correctly parses direct message payload`() {
        val data = mapOf(
            "chat_id" to "chat_123",
            "sender_id" to "user_456",
            "sender_name" to "Alice",
            "preview_text" to "Hey, are you coming tonight?",
            "notification_type" to "direct_message"
        )

        val payload = ChatPushPayload.fromMap(data)

        assertEquals("chat_123", payload.chatId)
        assertEquals("user_456", payload.senderId)
        assertEquals("Alice", payload.senderName)
        assertEquals("Hey, are you coming tonight?", payload.previewText)
        assertEquals(PushNotificationType.DIRECT_MESSAGE, payload.notificationType)
    }

    @Test
    fun `fromMap correctly parses city chat mention payload`() {
        val data = mapOf(
            "chatId" to "city_berlin",
            "senderId" to "user_789",
            "title" to "Berlin Party Radar",
            "message" to "@everyone Meetup at 10 PM!",
            "type" to "city_chat_mention"
        )

        val payload = ChatPushPayload.fromMap(data)

        assertEquals("city_berlin", payload.chatId)
        assertEquals("user_789", payload.senderId)
        assertEquals("Berlin Party Radar", payload.senderName)
        assertEquals("@everyone Meetup at 10 PM!", payload.previewText)
        assertEquals(PushNotificationType.CITY_CHAT_MENTION, payload.notificationType)
    }

    @Test
    fun `fromMap uses defaults when keys are missing`() {
        val data = emptyMap<String, String>()

        val payload = ChatPushPayload.fromMap(data)

        assertEquals("", payload.chatId)
        assertEquals("", payload.senderId)
        assertEquals("Kliq Chat", payload.senderName)
        assertEquals("", payload.previewText)
        assertEquals(PushNotificationType.DIRECT_MESSAGE, payload.notificationType)
    }
}
