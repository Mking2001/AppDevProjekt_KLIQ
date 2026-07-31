package com.kliq.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ChatVoiceMessageTest {

    @Test
    fun `ChatMessage handles VOICE messageType and audioDurationMs correctly`() {
        val voiceMsg = ChatMessage(
            id = "voice_msg_1",
            chatId = "chat_1",
            senderUserId = "usr_1",
            senderName = "Max",
            text = "🎤 Sprachnachricht",
            mediaUrl = "file:///storage/emulated/0/Android/data/com.kliq.app/cache/chat_voice/voice_123.m4a",
            messageType = MessageType.VOICE,
            audioDurationMs = 7500L,
            isMine = true
        )

        assertEquals(MessageType.VOICE, voiceMsg.messageType)
        assertEquals("file:///storage/emulated/0/Android/data/com.kliq.app/cache/chat_voice/voice_123.m4a", voiceMsg.mediaUrl)
        assertEquals(7500L, voiceMsg.audioDurationMs)
        assertEquals("🎤 Sprachnachricht", voiceMsg.text)
    }

    @Test
    fun `DirectMessage handles VOICE messageType and audioDurationMs correctly`() {
        val directVoiceMsg = DirectMessage(
            messageId = "dm_voice_1",
            senderId = "usr_1",
            receiverId = "usr_2",
            text = "🎤 Sprachnachricht",
            mediaUrl = "/cache/voice/recorded_456.m4a",
            messageType = MessageType.VOICE,
            audioDurationMs = 14200L,
            isMine = false
        )

        assertEquals(MessageType.VOICE, directVoiceMsg.messageType)
        assertEquals("/cache/voice/recorded_456.m4a", directVoiceMsg.mediaUrl)
        assertEquals(14200L, directVoiceMsg.audioDurationMs)
        assertNotNull(directVoiceMsg.timestampIso)
    }
}
