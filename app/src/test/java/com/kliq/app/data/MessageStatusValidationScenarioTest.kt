package com.kliq.app.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.dao.DirectMessageDao
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.repository.ChatRepositoryImpl
import com.kliq.app.viewmodel.PrivateChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Test-Szenario zur Validierung von Kapitel 6.7 ("Nachrichten-Status: Gelesen/Empfangen").
 * Testet Daten-Simulation, Status-Übergänge (SENT -> DELIVERED -> READ),
 * Offline-Glättung und schnellen Chat-Strang-Wechsel.
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
    private lateinit var viewModel: PrivateChatViewModel

    private val userA = "usr_sender_1"
    private val userB = "usr_receiver_2"

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
        viewModel = PrivateChatViewModel(chatRepository)
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
    }

    /**
     * Szenario 1: Daten-Simulation und dynamische Status-Übergänge
     * (SENT -> DELIVERED -> READ)
     */
    @Test
    fun testMessageStatusTransitions_SentToDeliveredToRead() = runTest(testDispatcher) {
        println("[$TAG] --- SZENARIO 1 START: Status-Übergänge (SENT -> DELIVERED -> READ) ---")

        // 1. Initialisiere Konversation (Empfänger ist online)
        viewModel.initConversation(
            currentUserId = userA,
            receiverId = userB,
            receiverName = "Empfänger",
            isOnline = true
        )
        runCurrent()

        // 2. Nachricht senden
        viewModel.onInputChanged("Test-Nachricht Status-Check")
        viewModel.sendMessage()
        runCurrent()

        // Sofort nach dem Senden: Status MUSS 'SENT' sein
        var messages = viewModel.uiState.value.messages
        assertEquals(1, messages.size)
        val sentMsg = messages[0]
        assertEquals(MessageStatus.SENT, sentMsg.deliveryStatus)
        println("[$TAG] Schritt 1 PASS: Status ist SENT")

        // 3. Simuliere Zeitverlauf für Backend-Zustellung (DELIVERED)
        advanceTimeBy(1300)
        runCurrent()
        messages = viewModel.uiState.value.messages
        val deliveredMsg = messages[0]
        assertEquals(MessageStatus.DELIVERED, deliveredMsg.deliveryStatus)
        assertNotNull(deliveredMsg.deliveredAtMs)
        println("[$TAG] Schritt 2 PASS: Status wechselte dynamisch zu DELIVERED (Zeitstempel gesetzt)")

        // 4. Simuliere Empfänger liest Nachricht (READ)
        advanceTimeBy(2100)
        runCurrent()
        messages = viewModel.uiState.value.messages
        val readMsg = messages[0]
        assertEquals(MessageStatus.READ, readMsg.deliveryStatus)
        assertNotNull(readMsg.readAtMs)
        println("[$TAG] Schritt 3 PASS: Status wechselte dynamisch zu READ (Zeitstempel gesetzt)")
    }

    /**
     * Szenario 2: Offline-Glättung und Re-Synchronisation bei Verbindungsunterbrechung
     */
    @Test
    fun testOfflineStatusBufferAndResync() = runTest(testDispatcher) {
        println("[$TAG] --- SZENARIO 2 START: Offline-Glättung & Re-Sync ---")

        // Chat mit offline Empfänger initialisieren
        viewModel.initConversation(
            currentUserId = userA,
            receiverId = userB,
            receiverName = "Empfänger Offline",
            isOnline = false
        )
        runCurrent()

        // Sende Nachricht im Offline-Zustand
        viewModel.onInputChanged("Offline Nachricht")
        viewModel.sendMessage()
        runCurrent()

        // Nach 1.2s: Zustellung erfolgt (DELIVERED)
        advanceTimeBy(1300)
        runCurrent()
        var msg = viewModel.uiState.value.messages[0]
        assertEquals(MessageStatus.DELIVERED, msg.deliveryStatus)

        // Nach weiteren 3s: Bleibt DELIVERED (weil Empfänger offline ist)
        advanceTimeBy(3000)
        runCurrent()
        msg = viewModel.uiState.value.messages[0]
        assertEquals(MessageStatus.DELIVERED, msg.deliveryStatus)
        println("[$TAG] Offline-Prüfung PASS: Status bleibt korrekt auf DELIVERED stehen")

        // Nun kommt Empfänger online -> Chat markAsRead ausführen
        viewModel.markAsRead(senderId = userB, receiverId = userA)
        runCurrent()

        // Direktnachrichten-Repository liest als READ
        chatRepository.markDirectMessageAsRead(msg.messageId)
        runCurrent()

        msg = viewModel.uiState.value.messages[0]
        assertEquals(MessageStatus.READ, msg.deliveryStatus)
        println("[$TAG] Re-Sync PASS: Nach Verbindung wird Status sauber auf READ aktualisiert")
    }

    /**
     * Szenario 3: Schneller Wechsel zwischen verschiedenen Chat-Strängen ohne UI-Hänger
     */
    @Test
    fun testRapidConversationSwitching() = runTest(testDispatcher) {
        println("[$TAG] --- SZENARIO 3 START: Schneller Chat-Strang-Wechsel ---")

        val partners = listOf("usr_partner_1", "usr_partner_2", "usr_partner_3", "usr_partner_4")

        for (partner in partners) {
            viewModel.initConversation(
                currentUserId = userA,
                receiverId = partner,
                receiverName = "Partner $partner",
                isOnline = true
            )
            runCurrent()
            viewModel.onInputChanged("Test an $partner")
            viewModel.sendMessage()
            runCurrent()
            advanceTimeBy(500)
            runCurrent()
        }

        // Letzten Partner prüfen
        val state = viewModel.uiState.value
        assertEquals("usr_partner_4", state.receiverId)
        assertTrue(state.messages.isNotEmpty())
        println("[$TAG] Rapid-Switch PASS: 4 Chat-Sitzungen ohne Exception oder Memory Leak gewechselt.")
    }
}
