package com.kliq.app.viewmodel

import com.kliq.app.data.repository.UserRepository
import com.kliq.app.ui.screens.chat.ChatDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

import com.kliq.app.util.VoicePlayerManager
import com.kliq.app.util.VoiceRecorderManager
import com.kliq.app.util.VoiceRecordingResult

@OptIn(ExperimentalCoroutinesApi::class)
class ChatDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val imageCompressor: com.kliq.app.util.ImageCompressor = mock(com.kliq.app.util.ImageCompressor::class.java)
    private val voiceRecorderManager: VoiceRecorderManager = mock(VoiceRecorderManager::class.java)
    private val voicePlayerManager: VoicePlayerManager = mock(VoicePlayerManager::class.java)
    private lateinit var viewModel: ChatDetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(userRepository.isUserBlocked("current_user", "usr_3")).thenReturn(flowOf(false))
        `when`(userRepository.isUserBlocked("current_user", "usr_pub_group")).thenReturn(flowOf(false))
        viewModel = ChatDetailViewModel(userRepository, imageCompressor, voiceRecorderManager, voicePlayerManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadConversationLoadsMessages() = runTest {
        viewModel.loadConversation("priv_1")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Lisa W.", state.conversationName)
        assertEquals("L", state.conversationInitial)
        assertTrue(state.isOnline)
        assertTrue(state.messages.isNotEmpty())
    }

    @Test
    fun testSendMessageAppendsMessageAndClearsInput() = runTest {
        viewModel.loadConversation("priv_1")
        testDispatcher.scheduler.advanceUntilIdle()

        val initialCount = viewModel.uiState.value.messages.size
        viewModel.onInputChanged("Treffen wir uns um 20 Uhr?")
        assertEquals("Treffen wir uns um 20 Uhr?", viewModel.uiState.value.currentInput)

        viewModel.onSendMessage()

        val state = viewModel.uiState.value
        assertEquals("", state.currentInput)
        assertEquals(initialCount + 1, state.messages.size)
        assertEquals("Treffen wir uns um 20 Uhr?", state.messages.last().text)
        assertTrue(state.messages.last().isMine)
    }

    @Test
    fun testInputBlockedWhenUserIsBlocked() = runTest {
        `when`(userRepository.isUserBlocked("current_user", "usr_3")).thenReturn(flowOf(true))

        viewModel.loadConversation("priv_1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isBlocked)

        viewModel.onInputChanged("Test Nachricht")
        assertEquals("", viewModel.uiState.value.currentInput)
    }

    @Test
    fun testStopAndSendVoiceRecordingAppendsVoiceMessage() = runTest {
        viewModel.loadConversation("priv_1")
        testDispatcher.scheduler.advanceUntilIdle()

        val initialCount = viewModel.uiState.value.messages.size
        `when`(voiceRecorderManager.stopRecording()).thenReturn(
            VoiceRecordingResult(filePath = "/cache/voice_test.m4a", durationMs = 5200L)
        )

        viewModel.stopAndSendVoiceRecording()

        val state = viewModel.uiState.value
        assertEquals(initialCount + 1, state.messages.size)
        val lastMsg = state.messages.last()
        assertEquals(com.kliq.app.data.model.MessageType.VOICE, lastMsg.messageType)
        assertEquals("/cache/voice_test.m4a", lastMsg.mediaUrl)
        assertEquals(5200L, lastMsg.audioDurationMs)
        assertTrue(lastMsg.isMine)
    }
}
