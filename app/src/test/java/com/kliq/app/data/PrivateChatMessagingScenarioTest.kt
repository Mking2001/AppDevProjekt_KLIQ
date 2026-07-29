package com.kliq.app.data

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.DirectMessageDao
import com.kliq.app.data.model.DirectMessage
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.repository.ChatRepositoryImpl
import com.kliq.app.viewmodel.PrivateChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * End-to-End Test-Szenario zur Validierung der 1-zu-1 Private Messaging-Logik
 * gemaess Anforderungsspezifikation (Kapitel 6.4).
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PrivateChatMessagingScenarioTest {

    private val TAG = "PrivateChatScenarioTest"
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var db: KliqDatabase
    private lateinit var directMessageDao: DirectMessageDao
    private lateinit var chatRepository: ChatRepositoryImpl
    private lateinit var viewModel: PrivateChatViewModel

    private val userAId = "usr_alpha_101"
    private val userBId = "usr_beta_202"
    private val userBName = "User Beta"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        directMessageDao = db.directMessageDao()
        chatRepository = ChatRepositoryImpl(
            chatDao = db.chatDao(),
            directMessageDao = directMessageDao
        )

        viewModel = PrivateChatViewModel(chatRepository)

        Log.d(TAG, "=== SETUP: In-Memory DB & PrivateChatViewModel initialisiert ===")
    }

    @After
    fun tearDown() {
        db.close()
        Dispatchers.resetMain()
        Log.d(TAG, "=== TEARDOWN: Datenbank geschlossen ===")
    }

    @Test
    fun testCompletePrivateMessagingScenario() = runTest(testDispatcher) {
        println("[$TAG] SZENARIO-START: 1-zu-1 Private Messaging Validierung")

        // -------------------------------------------------------------
        // Schritt 1: Initialisiere Chat-Sitzung zwischen User A und User B
        // -------------------------------------------------------------
        Log.d(TAG, "Schritt 1: Chat-Sitzung fuer User A ($userAId) -> User B ($userBId) starten")
        viewModel.initConversation(
            currentUserId = userAId,
            receiverId = userBId,
            receiverName = userBName,
            isOnline = true
        )
        runCurrent()

        var uiState = viewModel.uiState.value
        assertEquals(userAId, uiState.currentUserId)
        assertEquals(userBId, uiState.receiverId)
        assertTrue(uiState.messages.isEmpty())
        Log.d(TAG, "Schritt 1 erfolgreich: Chat-Sitzung aktiv, Nachrichtenliste initial leer.")

        // -------------------------------------------------------------
        // Schritt 2: User A sendet eine Nachricht an User B
        // -------------------------------------------------------------
        val textMessageFromA = "Hallo User B, bist du heute Abend im Club?"
        Log.d(TAG, "Schritt 2: User A sendet Nachricht: '$textMessageFromA'")

        viewModel.onInputChanged(textMessageFromA)
        viewModel.sendMessage(receiverId = userBId)
        runCurrent()

        uiState = viewModel.uiState.value
        assertEquals(1, uiState.messages.size)
        val sentMsg = uiState.messages.first()
        assertEquals(textMessageFromA, sentMsg.text)
        assertEquals(userAId, sentMsg.senderId)
        assertEquals(userBId, sentMsg.receiverId)
        assertTrue(sentMsg.isMine)
        assertTrue(sentMsg.isEncrypted)
        Log.d(TAG, "Schritt 2a: Nachricht erfolgreich im ViewModel UI-State reflektiert.")

        // Verifiziere lokale Room-Datenbank-Speicherung
        val dbMessagesAfterSend = directMessageDao.getDirectMessagesBetweenUsers(userAId, userBId).first()
        assertEquals(1, dbMessagesAfterSend.size)
        assertEquals(textMessageFromA, dbMessagesAfterSend.first().text)
        Log.d(TAG, "Schritt 2b: Nachricht erfolgreich in der Room DB verifiziert (ID: ${sentMsg.messageId}).")

        // -------------------------------------------------------------
        // Schritt 3: Simuliere den Empfang einer Antwort von User B
        // -------------------------------------------------------------
        val responseTimestamp = System.currentTimeMillis() + 5000L
        val responseTextFromB = "Hey User A! Ja klar, ich bin ab 23 Uhr im Club Vibe!"
        val incomingMessageFromB = DirectMessage(
            messageId = "msg_resp_202",
            senderId = userBId,
            receiverId = userAId,
            text = responseTextFromB,
            timestamp = responseTimestamp,
            deliveryStatus = MessageStatus.DELIVERED,
            isEncrypted = true,
            isMine = false
        )

        Log.d(TAG, "Schritt 3: Empfang einer Antwortnachricht von User B simulieren: '$responseTextFromB'")
        viewModel.handleIncomingMessage(incomingMessageFromB)
        runCurrent()

        uiState = viewModel.uiState.value
        assertEquals(2, uiState.messages.size)

        // Verifiziere chronologische Sortierung (Nachricht 1 vor Nachricht 2)
        val msg1 = uiState.messages[0]
        val msg2 = uiState.messages[1]
        assertTrue(msg1.timestamp <= msg2.timestamp)
        assertEquals(textMessageFromA, msg1.text)
        assertEquals(responseTextFromB, msg2.text)
        Log.d(TAG, "Schritt 3 erfolgreich: Antwort empfangen und chronologisch korrekt einsortiert.")

        // -------------------------------------------------------------
        // Schritt 4: Offline-Faehigkeit & Persistenz nach App-Neustart
        // -------------------------------------------------------------
        Log.d(TAG, "Schritt 4: Simuliere Neustart der App / Instanziierung eines neuen ViewModels")

        // Erstelle neues ViewModel, um Speicherlecks/Cache zu umgehen
        val newViewModel = PrivateChatViewModel(chatRepository)
        newViewModel.initConversation(
            currentUserId = userAId,
            receiverId = userBId,
            receiverName = userBName
        )
        runCurrent()

        val restoredState = newViewModel.uiState.value
        assertEquals(2, restoredState.messages.size)
        assertEquals(textMessageFromA, restoredState.messages[0].text)
        assertEquals(responseTextFromB, restoredState.messages[1].text)
        Log.d(TAG, "Schritt 4 erfolgreich: Nachrichtenverlauf vollstaendig aus lokaler Room DB wiederhergestellt.")

        println("[$TAG] SZENARIO SUCCESS: Alle 4 Test-Schritte ohne Fehler bestanden!")
    }
}
