package com.kliq.app.viewmodel

import com.kliq.app.data.model.ChatListItem
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.LastMessage
import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.ui.screens.chat.ChatListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class ChatSwipeActionsUnitTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val chatRepository: ChatRepository = mock(ChatRepository::class.java)

    private lateinit var viewModel: ChatListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(userRepository.getBlockedUserIds("current_user")).thenReturn(flowOf(emptyList()))
        `when`(chatRepository.getAllChats()).thenReturn(flowOf(emptyList()))
        viewModel = ChatListViewModel(
            userRepository = userRepository,
            chatRepository = chatRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onRequestDeleteChat_setsPendingDeleteChatInUiState() {
        val testChat = ChatListItem(
            id = "priv_1",
            title = "Lisa W.",
            lastMessage = LastMessage(text = "Hallo!"),
            avatarInitial = "L",
            chatType = ChatType.PRIVATE
        )

        viewModel.onRequestDeleteChat(testChat)

        val state = viewModel.uiState.value
        assertNotNull(state.pendingDeleteChat)
        assertEquals("priv_1", state.pendingDeleteChat?.id)
        assertEquals("Lisa W.", state.pendingDeleteChat?.title)
    }

    @Test
    fun onConfirmDeleteChat_removesChatFromStateAndCallsRepository() = runTest {
        val testChat = viewModel.uiState.value.privateChats.first()

        viewModel.onRequestDeleteChat(testChat)
        viewModel.onConfirmDeleteChat()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.pendingDeleteChat)
        assertTrue(state.privateChats.none { it.id == testChat.id })
        verify(chatRepository).deleteChat(testChat.id)
    }

    @Test
    fun onDismissDeleteDialog_clearsPendingDeleteChatWithoutDeleting() {
        val testChat = ChatListItem(
            id = "priv_1",
            title = "Lisa W.",
            lastMessage = LastMessage(text = "Hallo!"),
            avatarInitial = "L",
            chatType = ChatType.PRIVATE
        )

        viewModel.onRequestDeleteChat(testChat)
        viewModel.onDismissDeleteDialog()

        val state = viewModel.uiState.value
        assertNull(state.pendingDeleteChat)
    }

    @Test
    fun onArchiveChat_movesChatToArchivedListAndCallsRepository() = runTest {
        val testChat = viewModel.uiState.value.privateChats.first()

        viewModel.onArchiveChat(testChat)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.privateChats.none { it.id == testChat.id })
        assertTrue(state.archivedChats.any { it.id == testChat.id })
        verify(chatRepository).archiveChat(testChat.id, isArchived = true)
    }

    @Test
    fun onUnarchiveChat_restoresChatToActiveListAndUpdatesRepository() = runTest {
        val testChat = viewModel.uiState.value.privateChats.first()

        viewModel.onArchiveChat(testChat)
        viewModel.onUnarchiveChat(testChat)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.archivedChats.none { it.id == testChat.id })
        assertTrue(state.privateChats.any { it.id == testChat.id })
        verify(chatRepository).archiveChat(testChat.id, isArchived = false)
    }
}
