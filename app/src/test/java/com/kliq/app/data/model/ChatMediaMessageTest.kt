package com.kliq.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChatMediaMessageTest {

    @Test
    fun `ChatMessage defaults messageType to IMAGE when mediaUrl is provided`() {
        val message = ChatMessage(
            id = "msg_1",
            chatId = "chat_100",
            senderUserId = "usr_1",
            senderName = "Max",
            text = "Schau mal!",
            mediaUrl = "file:///storage/emulated/0/Pictures/party.jpg",
            thumbnailUrl = "file:///storage/emulated/0/Pictures/party_thumb.jpg",
            aspectRatio = 1.33f,
            mediaWidth = 1280,
            mediaHeight = 960,
            captionText = "Schau mal!",
            isMine = true
        )

        assertEquals(MessageType.IMAGE, message.messageType)
        assertEquals("file:///storage/emulated/0/Pictures/party.jpg", message.mediaUrl)
        assertEquals("file:///storage/emulated/0/Pictures/party_thumb.jpg", message.thumbnailUrl)
        assertEquals(1.33f, message.aspectRatio, 0.01f)
        assertEquals(1280, message.mediaWidth)
        assertEquals(960, message.mediaHeight)
        assertEquals("Schau mal!", message.captionText)
    }

    @Test
    fun `ChatMessage defaults messageType to TEXT when mediaUrl is null`() {
        val message = ChatMessage(
            id = "msg_2",
            chatId = "chat_100",
            senderUserId = "usr_2",
            senderName = "Lisa",
            text = "Hallo!",
            mediaUrl = null,
            isMine = false
        )

        assertEquals(MessageType.TEXT, message.messageType)
        assertNull(message.mediaUrl)
        assertNull(message.thumbnailUrl)
        assertEquals(1.0f, message.aspectRatio, 0.01f)
    }

    @Test
    fun `DirectMessage handles media attributes and aspectRatio correctly`() {
        val directMsg = DirectMessage(
            messageId = "dm_50",
            senderId = "usr_1",
            receiverId = "usr_2",
            text = "📷 Foto",
            mediaUrl = "/cache/images/compressed_123.jpg",
            thumbnailUrl = "/cache/images/thumb_123.jpg",
            aspectRatio = 0.75f,
            mediaWidth = 900,
            mediaHeight = 1200,
            captionText = "Location Heinsberg",
            isMine = true
        )

        assertEquals(MessageType.IMAGE, directMsg.messageType)
        assertEquals("/cache/images/compressed_123.jpg", directMsg.mediaUrl)
        assertEquals("/cache/images/thumb_123.jpg", directMsg.thumbnailUrl)
        assertEquals(0.75f, directMsg.aspectRatio, 0.01f)
        assertEquals("Location Heinsberg", directMsg.captionText)
    }
}
