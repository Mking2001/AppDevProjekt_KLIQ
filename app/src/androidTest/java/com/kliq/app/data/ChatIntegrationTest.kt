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
import org.junit.Assert.assertFalse
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
    fun testMessageInstantiationAndLocalDatabasePersistence() = runBlocking {
        // 1. Chat-Verbindung in der Room-Datenbank erstellen
        val chatId = "chat_persist_101"
        chatDao.insertChat(
            ChatEntity(
                id = chatId,
                name = "Max Mustermann",
                lastMessageText = "Initiale Nachricht",
                lastMessageTimestampMs = System.currentTimeMillis(),
                avatarInitial = "M",
                chatType = ChatType.PRIVATE
            )
        )

        // 2. Instanziieren & Speichern einer Nachricht mit ISO-Zeitstempel und Foto-Medien-URL
        val messageEntity = MessageEntity(
            id = "msg_persist_88",
            chatId = chatId,
            senderUserId = "user_sender_42",
            senderName = "Max",
            text = "Schau dir diesen Club an!",
            timestampMs = 1784600000000L,
            timestampIso = "2026-07-21T21:00:00Z",
            mediaUrl = "https://kliq-app.de/uploads/club_stage.jpg",
            status = MessageStatus.SENT,
            isMine = true
        )
        chatDao.insertMessage(messageEntity)

        // 3. Auslesen aus der Datenbank & Verifikation aller Attribute
        val persistedMessages = chatDao.getMessagesForChat(chatId).first()
        assertEquals(1, persistedMessages.size)

        val retrievedMsg = persistedMessages[0]
        assertEquals("msg_persist_88", retrievedMsg.id)
        assertEquals(chatId, retrievedMsg.chatId)
        assertEquals("user_sender_42", retrievedMsg.senderUserId)
        assertEquals("Max", retrievedMsg.senderName)
        assertEquals("Schau dir diesen Club an!", retrievedMsg.text)
        assertEquals(1784600000000L, retrievedMsg.timestampMs)
        assertEquals("2026-07-21T21:00:00Z", retrievedMsg.timestampIso)
        assertEquals("https://kliq-app.de/uploads/club_stage.jpg", retrievedMsg.mediaUrl)
        assertEquals(MessageStatus.SENT, retrievedMsg.status)
        assertTrue(retrievedMsg.isMine)
    }

    @Test
    fun testMessageChronologicalSortingByTimestamp() = runBlocking {
        val chatId = "chat_sorting_test"
        chatDao.insertChat(
            ChatEntity(
                id = chatId,
                name = "Berlin - Tonight",
                cityRegion = "Berlin",
                lastMessageText = "Letzte Nachricht",
                lastMessageTimestampMs = 1000L,
                avatarInitial = "B",
                chatType = ChatType.PUBLIC_CITY
            )
        )

        // Einfügen von Nachrichten in ungeordneter Reihenfolge bezüglich Zeitstempel
        val msg3 = MessageEntity("msg_3", chatId, "user_3", "Anna", "Späteste Nachricht", 3000L, "2026-07-21T22:00:00Z", null, MessageStatus.SENT, false)
        val msg1 = MessageEntity("msg_1", chatId, "user_1", "Ben", "Früheste Nachricht", 1000L, "2026-07-21T20:00:00Z", null, MessageStatus.SENT, false)
        val msg2 = MessageEntity("msg_2", chatId, "user_2", "Du", "Mittlere Nachricht", 2000L, "2026-07-21T21:00:00Z", null, MessageStatus.SENT, true)

        chatDao.insertMessage(msg3)
        chatDao.insertMessage(msg1)
        chatDao.insertMessage(msg2)

        // Auslesen aus der Datenbank & Verifikation der chronologischen Sortierung (ASC)
        val sortedMessages = chatDao.getMessagesForChat(chatId).first()
        assertEquals(3, sortedMessages.size)
        assertEquals("msg_1", sortedMessages[0].id)
        assertEquals("msg_2", sortedMessages[1].id)
        assertEquals("msg_3", sortedMessages[2].id)
        assertEquals(1000L, sortedMessages[0].timestampMs)
        assertEquals(2000L, sortedMessages[1].timestampMs)
        assertEquals(3000L, sortedMessages[2].timestampMs)
    }

    @Test
    fun testMessageStatusTransitionFromSentToRead() = runBlocking {
        val chatId = "chat_status_flow"
        chatDao.insertChat(
            ChatEntity(
                id = chatId,
                name = "Lisa W.",
                lastMessageText = "Empfangen",
                lastMessageTimestampMs = System.currentTimeMillis(),
                avatarInitial = "L",
                chatType = ChatType.PRIVATE
            )
        )

        // 1. Nachricht im Status "SENT" senden
        val sendResult = chatRepository.sendTextMessage(
            chatId = chatId,
            senderUserId = "usr_alex",
            senderName = "Alex",
            text = "Bist du schon da?"
        )
        assertTrue(sendResult.isSuccess)
        val msg = sendResult.getOrNull()
        assertNotNull(msg)
        assertEquals(MessageStatus.SENT, msg!!.status)

        // 2. Statusaktualisierung auf "READ" (Gelesen)
        chatRepository.updateMessageStatus(msg.id, MessageStatus.READ)

        // 3. Verifikation des aktualisierten Status in der Datenbank
        val dbMessages = chatDao.getMessagesForChat(chatId).first()
        assertEquals(1, dbMessages.size)
        assertEquals(MessageStatus.READ, dbMessages[0].status)

        // 4. Verifikation der High-Contrast Lila UI-Sprechblasen-Formatierung
        val uiState = msg.copy(status = MessageStatus.READ, isMine = true)
            .toHighContrastBubbleState(isGroupChat = false)
        assertTrue(uiState.statusIconText.contains("Gelesen"))
        assertEquals(ChatHighContrastPalette.StatusReadViolet, uiState.statusIconColorHex)
    }

    @Test
    fun testDistinctionBetweenPrivateOneOnOneAndPublicCityGroupChat() = runBlocking {
        // 1. Erstellung eines privaten 1-zu-1-Chats
        val privateChatId = "chat_private_sarah"
        val privateChat = ChatEntity(
            id = privateChatId,
            name = "Sarah Connor",
            lastMessageText = "Bis gleich!",
            lastMessageTimestampMs = 1784600000000L,
            avatarInitial = "S",
            chatType = ChatType.PRIVATE,
            isOnline = true
        )
        chatDao.insertChat(privateChat)

        // 2. Erstellung eines öffentlichen Stadt-basierten Gruppenchats ("Berlin - Tonight")
        val cityChatId = "chat_public_berlin_tonight"
        val cityGroupChat = ChatEntity(
            id = cityChatId,
            name = "Berlin - Tonight",
            cityRegion = "Berlin",
            lastMessageText = "Party startet um 23 Uhr!",
            lastMessageTimestampMs = 1784601000000L,
            avatarInitial = "B",
            chatType = ChatType.PUBLIC_CITY,
            isOnline = false
        )
        chatDao.insertChat(cityGroupChat)

        // Nachrichten in beide Chats einfügen
        val privMsg = MessageEntity("msg_priv", privateChatId, "user_sarah", "Sarah", "Hi Alex", 100L, "2026-07-21T21:00:00Z", null, MessageStatus.READ, false)
        val cityMsg = MessageEntity("msg_city", cityChatId, "user_dj", "DJ Felix", "Welcher Club?", 200L, "2026-07-21T21:05:00Z", null, MessageStatus.SENT, false)
        chatDao.insertMessage(privMsg)
        chatDao.insertMessage(cityMsg)

        // 3. Verifikation der DAO-Query-Filterung (Private vs Öffentliche Stadt-Chats)
        val privateChatsList = chatDao.getPrivateChats().first()
        assertEquals(1, privateChatsList.size)
        assertEquals("Sarah Connor", privateChatsList[0].name)
        assertEquals(ChatType.PRIVATE, privateChatsList[0].chatType)

        val publicCityChatsList = chatDao.getPublicCityChats("Berlin").first()
        assertEquals(1, publicCityChatsList.size)
        assertEquals("Berlin - Tonight", publicCityChatsList[0].name)
        assertEquals("Berlin", publicCityChatsList[0].cityRegion)
        assertEquals(ChatType.PUBLIC_CITY, publicCityChatsList[0].chatType)

        // 4. Verifikation der unterschiedlichen Sprechblasen-Formatierung
        val privBubble = privMsg.toHighContrastBubbleState(isGroupChat = false)
        assertFalse("Private Nachrichten zeigen keinen Gruppen-Absender-Header", privBubble.showSenderHeader)

        val cityBubble = cityMsg.toHighContrastBubbleState(isGroupChat = true)
        assertTrue("Fremde Stadt-Gruppennachrichten zeigen den Absender-Header", cityBubble.showSenderHeader)
        assertEquals("DJ Felix", cityBubble.senderName)
    }
}
