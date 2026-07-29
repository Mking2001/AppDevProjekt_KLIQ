package com.kliq.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class MessageStatusTransitionTest {

    @Test
    fun testInitialMessageStateIsSent() {
        val message = ChatMessage(
            id = "msg_1",
            chatId = "chat_1",
            senderUserId = "usr_mine",
            senderName = "Du",
            text = "Hallo Kliq!",
            isMine = true
        )

        assertEquals(MessageStatus.SENT, message.status)
        assertNull(message.deliveredAtMs)
        assertNull(message.readAtMs)
    }

    @Test
    fun testTransitionFromSentToDeliveredSetsTimestamp() {
        val now = System.currentTimeMillis()
        val initialMessage = ChatMessage(
            id = "msg_1",
            chatId = "chat_1",
            senderUserId = "usr_mine",
            senderName = "Du",
            text = "Hallo Kliq!",
            isMine = true
        )

        val deliveredMessage = initialMessage.copy(
            status = MessageStatus.DELIVERED,
            deliveredAtMs = now
        )

        assertEquals(MessageStatus.DELIVERED, deliveredMessage.status)
        assertNotNull(deliveredMessage.deliveredAtMs)
        assertEquals(now, deliveredMessage.deliveredAtMs)
        assertNull(deliveredMessage.readAtMs)
    }

    @Test
    fun testTransitionFromDeliveredToReadPreservesDeliveredTimestamp() {
        val deliveredTime = System.currentTimeMillis() - 2000
        val readTime = System.currentTimeMillis()

        val deliveredMessage = ChatMessage(
            id = "msg_1",
            chatId = "chat_1",
            senderUserId = "usr_mine",
            senderName = "Du",
            text = "Hallo Kliq!",
            status = MessageStatus.DELIVERED,
            deliveredAtMs = deliveredTime,
            isMine = true
        )

        val readMessage = deliveredMessage.copy(
            status = MessageStatus.READ,
            readAtMs = readTime
        )

        assertEquals(MessageStatus.READ, readMessage.status)
        assertEquals(deliveredTime, readMessage.deliveredAtMs)
        assertEquals(readTime, readMessage.readAtMs)
    }

    @Test
    fun testDirectMessageStatusTransitions() {
        val sentDm = DirectMessage(
            messageId = "dm_1",
            senderId = "usr_1",
            receiverId = "usr_2",
            text = "Hi via DM",
            deliveryStatus = MessageStatus.SENT,
            isMine = true
        )

        assertEquals(MessageStatus.SENT, sentDm.deliveryStatus)

        val deliveredDm = sentDm.copy(
            deliveryStatus = MessageStatus.DELIVERED,
            deliveredAtMs = System.currentTimeMillis()
        )
        assertEquals(MessageStatus.DELIVERED, deliveredDm.deliveryStatus)
        assertNotNull(deliveredDm.deliveredAtMs)

        val readDm = deliveredDm.copy(
            deliveryStatus = MessageStatus.READ,
            readAtMs = System.currentTimeMillis()
        )
        assertEquals(MessageStatus.READ, readDm.deliveryStatus)
        assertNotNull(readDm.readAtMs)
    }
}
