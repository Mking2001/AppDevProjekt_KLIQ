package com.kliq.app.viewmodel

import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.model.MessageType
import com.kliq.app.data.repository.SessionRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.domain.CurrentUserProvider
import com.kliq.app.testing.FakeChatRepository
import com.kliq.app.ui.screens.chat.ChatDetailViewModel
import com.kliq.app.util.ImageCompressor
import com.kliq.app.util.VoicePlayerManager
import com.kliq.app.util.VoiceRecorderManager
import com.kliq.app.util.VoiceRecordingResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class ChatDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val sessionRepository: SessionRepository = mock(SessionRepository::class.java)
    private val imageCompressor: ImageCompressor = mock(ImageCompressor::class.java)
    private val voiceRecorderManager: VoiceRecorderManager = mock(VoiceRecorderManager::class.java)
    private val voicePlayerManager: VoicePlayerManager = mock(VoicePlayerManager::class.java)

    private lateinit var chatRepository: FakeChatRepository
    private lateinit var viewModel: ChatDetailViewModel

    private val existingChat = ChatConversation(
        id = "priv_lena",
        name = "Lena P.",
        lastMessageText = "Bis später",
        lastMessageTimestampMs = 1_000L,
        avatarInitial = "L",
        unreadCount = 3,
        chatType = ChatType.PRIVATE,
        isOnline = true
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(userRepository.isUserBlocked(anyString(), anyString())).thenReturn(flowOf(false))

        chatRepository = FakeChatRepository(initialChats = listOf(existingChat))
        viewModel = ChatDetailViewModel(
            chatRepository = chatRepository,
            userRepository = userRepository,
            currentUserProvider = CurrentUserProvider(sessionRepository, userRepository),
            imageCompressor = imageCompressor,
            voiceRecorderManager = voiceRecorderManager,
            voicePlayerManager = voicePlayerManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadConversation_readsMetadataFromRepositoryAndClearsUnreadCounter() = runTest {
        viewModel.loadConversation("priv_lena")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Lena P.", state.conversationName)
        assertEquals("L", state.conversationInitial)
        assertEquals(ChatType.PRIVATE, state.chatType)
        assertTrue(state.isOnline)
        assertTrue(chatRepository.markedAsReadChatIds.contains("priv_lena"))
    }

    @Test
    fun loadConversation_createsChatWhenItDoesNotExist() = runTest {
        viewModel.loadConversation("chat_usr_david")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Direktnachricht", state.conversationName)
        assertEquals("usr_david", state.targetUserId)
    }

    @Test
    fun loadConversation_detectsPublicCityChatFromIdPrefix() = runTest {
        viewModel.loadConversation("pub_klagenfurt")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(ChatType.PUBLIC_CITY, viewModel.uiState.value.chatType)
    }

    @Test
    fun onSendMessage_persistsMessageAndClearsInput() = runTest {
        viewModel.loadConversation("priv_lena")
        testDispatcher.scheduler.advanceUntilIdle()

        val initialCount = viewModel.uiState.value.messages.size
        viewModel.onInputChanged("Treffen wir uns um 20 Uhr?")
        assertEquals("Treffen wir uns um 20 Uhr?", viewModel.uiState.value.currentInput)

        viewModel.onSendMessage()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.currentInput)
        assertEquals(initialCount + 1, state.messages.size)
        assertEquals("Treffen wir uns um 20 Uhr?", state.messages.last().text)
        assertTrue(state.messages.last().isMine)
    }

    @Test
    fun onSendMessage_marksMessageAsDeliveredAndRead() = runTest {
        viewModel.loadConversation("priv_lena")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onInputChanged("Statusverlauf prüfen")
        viewModel.onSendMessage()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(MessageStatus.READ, viewModel.uiState.value.messages.last().status)
    }

    @Test
    fun onInputChanged_isIgnoredWhenUserIsBlocked() = runTest {
        `when`(userRepository.isUserBlocked(anyString(), anyString())).thenReturn(flowOf(true))

        viewModel.loadConversation("priv_lena")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isBlocked)

        viewModel.onInputChanged("Test Nachricht")
        assertEquals("", viewModel.uiState.value.currentInput)
    }

    @Test
    fun stopAndSendVoiceRecording_persistsVoiceMessage() = runTest {
        viewModel.loadConversation("priv_lena")
        testDispatcher.scheduler.advanceUntilIdle()

        val initialCount = viewModel.uiState.value.messages.size
        `when`(voiceRecorderManager.stopRecording()).thenReturn(
            VoiceRecordingResult(filePath = "/cache/voice_test.m4a", durationMs = 5200L)
        )

        viewModel.stopAndSendVoiceRecording()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(initialCount + 1, state.messages.size)
        val lastMessage = state.messages.last()
        assertEquals(MessageType.VOICE, lastMessage.messageType)
        assertEquals("/cache/voice_test.m4a", lastMessage.mediaUrl)
        assertEquals(5200L, lastMessage.audioDurationMs)
        assertTrue(lastMessage.isMine)
    }

    @Test
    fun messagesSurviveReload_becauseTheyComeFromTheRepository() = runTest {
        viewModel.loadConversation("priv_lena")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onInputChanged("Bleibt gespeichert")
        viewModel.onSendMessage()
        testDispatcher.scheduler.advanceUntilIdle()

        val reopenedViewModel = ChatDetailViewModel(
            chatRepository = chatRepository,
            userRepository = userRepository,
            currentUserProvider = CurrentUserProvider(sessionRepository, userRepository),
            imageCompressor = imageCompressor,
            voiceRecorderManager = voiceRecorderManager,
            voicePlayerManager = voicePlayerManager
        )
        reopenedViewModel.loadConversation("priv_lena")
        testDispatcher.scheduler.advanceUntilIdle()

        val messages = reopenedViewModel.uiState.value.messages
        assertTrue(messages.any { it.text == "Bleibt gespeichert" })
    }
}
