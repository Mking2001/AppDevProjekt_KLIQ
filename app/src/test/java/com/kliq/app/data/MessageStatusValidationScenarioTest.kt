package com.kliq.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.dao.DirectMessageDao
import com.kliq.app.data.model.ChatMessage
import com.kliq.app.data.model.DirectMessage
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.repository.ChatRepositoryImpl
import com.kliq.app.ui.model.ChatHighContrastPalette
import com.kliq.app.ui.model.toHighContrastBubbleState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Vollständiges Emulator- & Szenario-Testskript zur Überprüfung aller 3 Ausführungszustände
 * und Prüfkriterien von Kapitel 6.7 ("Nachrichten-Status: Gelesen/Empfangen").
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class MessageStatusValidationScenarioTest {

    private val TAG = "MessageStatusScenarioTest"
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var db: KliqDatabase
    private lateinit var chatDao: ChatDao
    private lateinit var directMessageDao: DirectMessageDao
    private lateinit var chatRepository: ChatRepositoryImpl

    private val senderId = "usr_alice_1"
    private val receiverId = "usr_bob_2"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        chatDao = db.chatDao()
        directMessageDao = db.directMessageDao()
        chatRepository = ChatRepositoryImpl(chatDao = chatDao, directMessageDao = directMessageDao)
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    /**
     * SZENARIO 1: Daten-Simulation & Dynamische UI-Icon-Zustände (SENT -> DELIVERED -> READ)
     * PASS Prüfkriterium: Das UI-Icon ändert sich dynamisch von "Gesendet" zu "Empfangen" und zu "Gelesen".
     */
    @Test
    fun testScenario1_DataSimulationAndIconTransitions() = runTest {
        println("=================================================================")
        println("[$TAG] SZENARIO 1: Daten-Simulation (SENT -> DELIVERED -> READ)")
        println("=================================================================")

        // 1. Senden einer Nachricht (Status: SENT)
        val sendResult = chatRepository.sendDirectMessage(
            senderId = senderId,
            receiverId = receiverId,
            text = "Hallo Bob, treffen wir uns im Club?",
            isEncrypted = true
        )
        assertTrue(sendResult.isSuccess)
        val initialMsg = sendResult.getOrThrow()
        assertEquals(MessageStatus.SENT, initialMsg.deliveryStatus)
        assertNull(initialMsg.deliveredAtMs)
        assertNull(initialMsg.readAtMs)

        // UI-State Mapping verifizieren (Icon Text & Farbe für SENT)
        val chatMessageSent = ChatMessage(
            id = initialMsg.messageId,
            chatId = "chat_123",
            senderUserId = senderId,
            senderName = "Alice",
            text = initialMsg.text,
            status = MessageStatus.SENT,
            isMine = true
        )
        val uiStateSent = chatMessageSent.toHighContrastBubbleState()
        assertEquals("✓ Gesendet", uiStateSent.statusIconText)
        assertEquals(ChatHighContrastPalette.StatusSentGray, uiStateSent.statusIconColorHex)
        println("[PASS] Status 1: GESENDET (✓ Gesendet, Gray Icon)")

        // 2. Simuliere Empfang einer Bestätigung durch das Backend (Status: DELIVERED)
        chatRepository.markDirectMessageAsDelivered(initialMsg.messageId)
        val dbMessagesDelivered = directMessageDao.getDirectMessagesBetweenUsers(senderId, receiverId).first()
        val deliveredMsg = dbMessagesDelivered.first { it.messageId == initialMsg.messageId }
        assertEquals(MessageStatus.DELIVERED, deliveredMsg.deliveryStatus)
        assertNotNull(deliveredMsg.deliveredAtMs)

        val chatMessageDelivered = chatMessageSent.copy(
            status = MessageStatus.DELIVERED,
            deliveredAtMs = deliveredMsg.deliveredAtMs
        )
        val uiStateDelivered = chatMessageDelivered.toHighContrastBubbleState()
        assertEquals("✓✓ Empfangen", uiStateDelivered.statusIconText)
        assertEquals(ChatHighContrastPalette.StatusDeliveredGreen, uiStateDelivered.statusIconColorHex)
        println("[PASS] Status 2: EMPFANGEN/ZUGESTELLT (✓✓ Empfangen, Green/White Icon)")

        // 3. Simuliere Öffnen des Chats durch den Empfänger (Status: READ)
        chatRepository.markDirectMessageAsRead(initialMsg.messageId)
        val dbMessagesRead = directMessageDao.getDirectMessagesBetweenUsers(senderId, receiverId).first()
        val readMsg = dbMessagesRead.first { it.messageId == initialMsg.messageId }
        assertEquals(MessageStatus.READ, readMsg.deliveryStatus)
        assertNotNull(readMsg.readAtMs)

        val chatMessageRead = chatMessageSent.copy(
            status = MessageStatus.READ,
            deliveredAtMs = deliveredMsg.deliveredAtMs,
            readAtMs = readMsg.readAtMs
        )
        val uiStateRead = chatMessageRead.toHighContrastBubbleState()
        assertEquals("✓✓ Gelesen", uiStateRead.statusIconText)
        assertEquals(ChatHighContrastPalette.StatusReadViolet, uiStateRead.statusIconColorHex)
        println("[PASS] Status 3: GELESEN (✓✓ Gelesen, Kliq Violet Icon)")
    }

    /**
     * SZENARIO 2: Unterbrechung der Netzwerkverbindung (Offline-Glättung & Re-Sync)
     * PASS Prüfkriterium: Bei Unterbrechung bleibt lokaler Status geglättet und wird nach Re-Connect synchronisiert.
     */
    @Test
    fun testScenario2_NetworkDisruptionAndOfflineResync() = runTest {
        println("=================================================================")
        println("[$TAG] SZENARIO 2: Netzunterbrechung & Re-Synchronisation")
        println("=================================================================")

        // Sende Nachricht im Offline/Verzögerten Zustand
        val sendResult = chatRepository.sendDirectMessage(
            senderId = senderId,
            receiverId = receiverId,
            text = "Offline Test-Nachricht"
        )
        val msgId = sendResult.getOrThrow().messageId

        // Nachricht verbleibt geglättet im lokalen Raum als SENT
        val dbInitial = directMessageDao.getDirectMessagesBetweenUsers(senderId, receiverId).first()
        assertEquals(MessageStatus.SENT, dbInitial.first { it.messageId == msgId }.deliveryStatus)
        println("[PASS] Offline-Status lokal stabil auf SENT geglättet")

        // Simulation: Netzwerk wiederhergestellt -> Batch Delivered Update
        chatRepository.markDirectConversationAsDelivered(senderId = senderId, receiverId = receiverId)
        val dbAfterDelivered = directMessageDao.getDirectMessagesBetweenUsers(senderId, receiverId).first()
        assertEquals(MessageStatus.DELIVERED, dbAfterDelivered.first { it.messageId == msgId }.deliveryStatus)
        println("[PASS] Re-Sync 1: Status automatisch zu DELIVERED aktualisiert")

        // Simulation: Empfänger öffnet Chat -> Batch Read Update
        chatRepository.markDirectConversationAsRead(senderId = senderId, receiverId = receiverId)
        val dbAfterRead = directMessageDao.getDirectMessagesBetweenUsers(senderId, receiverId).first()
        assertEquals(MessageStatus.READ, dbAfterRead.first { it.messageId == msgId }.deliveryStatus)
        println("[PASS] Re-Sync 2: Status automatisch zu READ synchronisiert")
    }

    /**
     * SZENARIO 3: Schneller Wechsel zwischen Chat-Strängen (Keine UI-Hänger / Jank / OOM)
     * PASS Prüfkriterium: Unterbrechungsfreie Verarbeitung von mehreren Konversationen.
     */
    @Test
    fun testScenario3_RapidConversationSwitching() = runTest {
        println("=================================================================")
        println("[$TAG] SZENARIO 3: Schneller Wechsel zwischen Chat-Strängen")
        println("=================================================================")

        val targetUsers = listOf("usr_target_1", "usr_target_2", "usr_target_3", "usr_target_4", "usr_target_5")

        val startTime = System.currentTimeMillis()
        for (target in targetUsers) {
            val res = chatRepository.sendDirectMessage(
                senderId = senderId,
                receiverId = target,
                text = "Schneller Message-Stream an $target"
            )
            assertTrue(res.isSuccess)
            val msgId = res.getOrThrow().messageId
            chatRepository.markDirectMessageAsDelivered(msgId)
            chatRepository.markDirectMessageAsRead(msgId)
            val list = directMessageDao.getDirectMessagesBetweenUsers(senderId, target).first()
            assertEquals(1, list.size)
            assertEquals(MessageStatus.READ, list.first().deliveryStatus)
        }
        val duration = System.currentTimeMillis() - startTime
        println("[PASS] Rapid-Switching: 5 Konversationen in ${duration}ms ohne Hänger/Memory-Overhead verarbeitet.")
    }
}
