package com.kliq.app.service.notification

import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationChannelManagerTest {

    @Test
    fun `channel constants match required specifications`() {
        assertEquals("kliq_direct_messages_channel", NotificationChannelManager.CHANNEL_DIRECT_MESSAGES)
        assertEquals("kliq_city_chats_channel", NotificationChannelManager.CHANNEL_CITY_CHATS)
        assertEquals("Kliq Direct Messages", NotificationChannelManager.CHANNEL_DIRECT_MESSAGES_NAME)
        assertEquals("Kliq City Chats", NotificationChannelManager.CHANNEL_CITY_CHATS_NAME)
    }
}
