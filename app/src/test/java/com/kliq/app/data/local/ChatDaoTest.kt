package com.kliq.app.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.MessageEntity
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.MessageStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ChatDaoTest {

    private lateinit var db: KliqDatabase
    private lateinit var chatDao: ChatDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        chatDao = db.chatDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testPrivateAndPublicCityChatFiltering() = runTest {
        val privateChat = ChatEntity(
            id = "chat_priv_1",
            name = "Sarah Connor",
            lastMessageText = "See you tonight!",
            lastMessageTimestampMs = 1700000000000L,
            lastMessageTimestampIso = "2026-07-21T20:00:00Z",
            avatarInitial = "S",
            unreadCount = 1,
            chatType = ChatType.PRIVATE,
            isOnline = true
        )
        val cityGroupChat = ChatEntity(
            id = "chat_city_berlin",
            name = "Berlin - Tonight",
            cityRegion = "Berlin",
            lastMessageText = "Meet at Watergate at 23:00!",
            lastMessageTimestampMs = 1700001000000L,
            lastMessageTimestampIso = "2026-07-21T20:15:00Z",
            avatarInitial = "B",
            unreadCount = 4,
            chatType = ChatType.PUBLIC_CITY,
            isOnline = false
        )

        chatDao.insertChats(listOf(privateChat, cityGroupChat))

        val privateChats = chatDao.getPrivateChats().first()
        assertEquals(1, privateChats.size)
        assertEquals("Sarah Connor", privateChats[0].name)
        assertEquals(ChatType.PRIVATE, privateChats[0].chatType)

        val cityChats = chatDao.getPublicCityChats("Berlin").first()
        assertEquals(1, cityChats.size)
        assertEquals("Berlin - Tonight", cityChats[0].name)
        assertEquals("Berlin", cityChats[0].cityRegion)
        assertEquals(ChatType.PUBLIC_CITY, cityChats[0].chatType)
    }

    @Test
    fun testMessagePersistenceIsoTimestampAndMediaUrl() = runTest {
        val cityGroupChat = ChatEntity(
            id = "chat_city_berlin",
            name = "Berlin - Tonight",
            cityRegion = "Berlin",
            lastMessageText = "Photo uploaded",
            lastMessageTimestampMs = 1700000000000L,
            avatarInitial = "B",
            chatType = ChatType.PUBLIC_CITY
        )
        chatDao.insertChat(cityGroupChat)

        val msg1 = MessageEntity(
            id = "msg_101",
            chatId = "chat_city_berlin",
            senderUserId = "usr_max",
            senderName = "Max",
            text = "Check out the stage setup!",
            timestampMs = 1700000000000L,
            timestampIso = "2026-07-21T21:00:00Z",
            mediaUrl = "https://kliq-app.de/uploads/stage.jpg",
            status = MessageStatus.SENT,
            isMine = true
        )
        chatDao.insertMessage(msg1)

        val messages = chatDao.getMessagesForChat("chat_city_berlin").first()
        assertEquals(1, messages.size)

        val retrievedMsg = messages[0]
        assertEquals("msg_101", retrievedMsg.id)
        assertEquals("usr_max", retrievedMsg.senderUserId)
        assertEquals("2026-07-21T21:00:00Z", retrievedMsg.timestampIso)
        assertEquals("https://kliq-app.de/uploads/stage.jpg", retrievedMsg.mediaUrl)
        assertEquals(MessageStatus.SENT, retrievedMsg.status)

        // Status-Update (SENT -> READ)
        chatDao.updateMessageStatus("msg_101", MessageStatus.READ)
        val updatedMessages = chatDao.getMessagesForChat("chat_city_berlin").first()
        assertEquals(MessageStatus.READ, updatedMessages[0].status)
    }
}
