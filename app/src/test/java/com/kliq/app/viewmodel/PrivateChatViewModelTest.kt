package com.kliq.app.viewmodel

import com.kliq.app.data.model.DirectMessage
import com.kliq.app.data.model.MessageStatus
import com.kliq.app.data.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
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

@OptIn(ExperimentalCoroutinesApi::class)
class PrivateChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val chatRepository: ChatRepository = mock(ChatRepository::class.java)
    private lateinit var viewModel: PrivateChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PrivateChatViewModel(chatRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initConversation updates state and collects messages`() = runTest(testDispatcher) {
        val userA = "user_1"
        val userB = "user_2"
        val mockMessages = listOf(
            DirectMessage(
                messageId = "msg_1",
                senderId = userA,
                receiverId = userB,
                text = "Hallo!",
                timestamp = 1000L,
                deliveryStatus = MessageStatus.SENT,
                isEncrypted = true,
                isMine = true
            )
        )

        `when`(chatRepository.getDirectMessages(userA, userB)).thenReturn(flowOf(mockMessages))

        viewModel.initConversation(
            currentUserId = userA,
            receiverId = userB,
            receiverName = "Lisa",
            isOnline = true
        )
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(userA, state.currentUserId)
        assertEquals(userB, state.receiverId)
        assertEquals("Lisa", state.receiverName)
        assertTrue(state.isOnline)
        assertEquals(1, state.messages.size)
        assertEquals("Hallo!", state.messages.first().text)
    }

    @Test
    fun `sendMessage delegates to chatRepository and clears input`() = runTest(testDispatcher) {
        val userA = "user_1"
        val userB = "user_2"
        val textToSend = "Treffen wir uns heute?"

        `when`(chatRepository.getDirectMessages(userA, userB)).thenReturn(flowOf(emptyList()))
        `when`(
            chatRepository.sendDirectMessage(
                senderId = userA,
                receiverId = userB,
                text = textToSend,
                isEncrypted = true,
                mediaUrl = null
            )
        ).thenReturn(
            Result.success(
                DirectMessage(
                    messageId = "msg_2",
                    senderId = userA,
                    receiverId = userB,
                    text = textToSend
                )
            )
        )

        viewModel.initConversation(currentUserId = userA, receiverId = userB)
        runCurrent()

        viewModel.onInputChanged(textToSend)
        assertEquals(textToSend, viewModel.uiState.value.currentInput)

        viewModel.sendMessage(receiverId = userB, text = textToSend)
        runCurrent()

        assertEquals("", viewModel.uiState.value.currentInput)
    }

    @Test
    fun `toggleEncryption updates encrypted session state`() {
        assertTrue(viewModel.uiState.value.isEncryptedSession)
        viewModel.toggleEncryption(false)
        assertFalse(viewModel.uiState.value.isEncryptedSession)
    }
}
