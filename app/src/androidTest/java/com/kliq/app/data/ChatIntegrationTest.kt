package com.kliq.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.entities.ChatEntity
import com.kliq.app.data.local.entities.MessageEntity
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.repository.ChatRepositoryImpl
import com.kliq.app.ui.model.ChatHighContrastPalette
import com.kliq.app.ui.model.toHighContrastBubbleState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ChatIntegrationTest {

    private lateinit var db: KliqDatabase
    private lateinit var chatDao: ChatDao
    private lateinit var chatRepository: ChatRepositoryImpl

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        chatDao = db.chatDao()
        chatRepository = ChatRepositoryImpl(chatDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testPrivateOneOnOneChatPersistenceAndStatusFlow() = runBlocking {
        // 1. Erstellen eines 1-zu-1 Einzel-Chats
        val chatId = "chat_1on1_alex_sarah"
        val privateChat = ChatEntity(
            id = chatId,
            name = "Sarah Connor",
            lastMessageText = "Treffen wir uns vor dem Club?",
            lastMessageTimestampMs = 1784600000000L,
            lastMessageTimestampIso = "2026-07-21T21:00:00Z",
            avatarInitial = "S",
            unreadCount = 0,
            chatType = ChatType.PRIVATE,
            isOnline = true
        )
        chatDao.insertChat(privateChat)

        // 2. Textnachricht senden (Status: SENT -> DELIVERED -> READ)
        val result = chatRepository.sendTextMessage(
            chatId = chatId,
            senderUserId = "usr_alex_1",
            senderName = "Alex",
            text = "Bin in 10 Minuten da!"
        )
        assertTrue(result.isSuccess)
        val sentMsg = result.getOrNull()
        assertNotNull(sentMsg)
        assertEquals("usr_alex_1", sentMsg!!.senderUserId)
        assertEquals(MessageStatus.SENT, sentMsg.status)
        assertTrue(sentMsg.timestampIso.contains("Z"))

        // Status-Update auf DELIVERED und READ
        chatRepository.updateMessageStatus(sentMsg.id, MessageStatus.READ)

        // 3. Verifizieren des Datenbank-Zustands
        val messages = chatDao.getMessagesForChat(chatId).first()
        assertEquals(1, messages.size)
        val dbMsg = messages[0]
        assertEquals("Bin in 10 Minuten da!", dbMsg.text)
        assertEquals(MessageStatus.READ, dbMsg.status)

        // 4. Verifizieren der High-Contrast Sprechblasen UI-Struktur (Eigene Nachricht -> Deep Violet Background)
        val bubbleState = sentMsg.copy(status = MessageStatus.READ, isMine = true)
            .toHighContrastBubbleState(isGroupChat = false)
        assertTrue(bubbleState.alignmentIsRight)
        assertEquals(ChatHighContrastPalette.DeepVioletOwnBubble, bubbleState.bubbleBackgroundColorHex)
        assertTrue(bubbleState.statusIconText.contains("Gelesen"))
        assertEquals(ChatHighContrastPalette.StatusReadViolet, bubbleState.statusIconColorHex)
    }

    @Test
    fun testPublicCityGroupChatWithPhotoMediaUrl() = runBlocking {
        // 1. Erstellen eines öffentlichen Stadt-basierten Gruppenchats ("Berlin - Tonight")
        val chatId = "chat_group_berlin_tonight"
        val cityGroupChat = ChatEntity(
            id = chatId,
            name = "Berlin - Tonight",
            cityRegion = "Berlin",
            lastMessageText = "Gegen Night im KitKat ist voll!",
            lastMessageTimestampMs = 1784601000000L,
            lastMessageTimestampIso = "2026-07-21T21:15:00Z",
            avatarInitial = "B",
            unreadCount = 3,
            chatType = ChatType.PUBLIC_CITY,
            isOnline = false
        )
        chatDao.insertChat(cityGroupChat)

        // 2. Fotonachricht senden (mit Medien-URL)
        val photoResult = chatRepository.sendMediaMessage(
            chatId = chatId,
            senderUserId = "usr_lisa_2",
            senderName = "Lisa",
            text = "Schlange vorm Club:",
            mediaUrl = "https://kliq-app.de/uploads/queue_kitkat.jpg"
        )
        assertTrue(photoResult.isSuccess)
        val photoMsg = photoResult.getOrNull()
        assertNotNull(photoMsg)
        assertEquals("https://kliq-app.de/uploads/queue_kitkat.jpg", photoMsg!!.mediaUrl)

        // 3. Verifizieren des Gruppen-Chats und der Nachrichten im Speicher
        val cityChats = chatDao.getPublicCityChats("Berlin").first()
        assertEquals(1, cityChats.size)
        assertEquals("Berlin - Tonight", cityChats[0].name)
        assertEquals(ChatType.PUBLIC_CITY, cityChats[0].chatType)

        // 4. Verifizieren der Sprechblasen-Formatierung für empfangene Gruppennachrichten (Left Align + Header + Dark Surface)
        val bubbleStateReceived = photoMsg.copy(isMine = false)
            .toHighContrastBubbleState(isGroupChat = true)
        assertTrue("Fremde Gruppennachricht soll Absender-Header zeigen", bubbleStateReceived.showSenderHeader)
        assertEquals("Lisa", bubbleStateReceived.senderName)
        assertTrue("Soll Medien-Flag besitzen", bubbleStateReceived.hasMedia)
        assertEquals(ChatHighContrastPalette.DarkSurfaceReceivedBubble, bubbleStateReceived.bubbleBackgroundColorHex)
    }
}
