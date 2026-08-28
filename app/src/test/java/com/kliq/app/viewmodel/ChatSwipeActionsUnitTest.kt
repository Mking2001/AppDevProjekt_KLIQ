package com.kliq.app.viewmodel

import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatListItem
import com.kliq.app.data.model.ChatType
import com.kliq.app.data.model.LastMessage
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.repository.LocationRepository
import com.kliq.app.data.repository.SessionRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.domain.CurrentUserProvider
import com.kliq.app.testing.FakeChatRepository
import com.kliq.app.ui.screens.chat.ChatListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

/**
 * Prüft die Swipe-Aktionen der Chat-Liste: Löschen, Archivieren und
 * Wiederherstellen wirken über das Repository und spiegeln sich im State.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatSwipeActionsUnitTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val sessionRepository: SessionRepository = mock(SessionRepository::class.java)
    private val locationRepository: LocationRepository = mock(LocationRepository::class.java)
    private val locationUpdatesFlow = MutableStateFlow<LocationData?>(null)

    private lateinit var chatRepository: FakeChatRepository
    private lateinit var viewModel: ChatListViewModel

    private val privateChat = ChatConversation(
        id = "priv_lena",
        name = "Lena P.",
        lastMessageText = "Hallo",
        lastMessageTimestampMs = 1_000L,
        avatarInitial = "L",
        chatType = ChatType.PRIVATE
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(userRepository.getBlockedUserIds(anyString())).thenReturn(flowOf(emptyList()))
        `when`(locationRepository.locationUpdates).thenReturn(locationUpdatesFlow)

        chatRepository = FakeChatRepository(initialChats = listOf(privateChat))
        viewModel = ChatListViewModel(
            chatRepository = chatRepository,
            userRepository = userRepository,
            locationRepository = locationRepository,
            currentUserProvider = CurrentUserProvider(sessionRepository, userRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onRequestDeleteChat_setsPendingDeleteChatInUiState() {
        val testChat = ChatListItem(
            id = "priv_lena",
            title = "Lena P.",
            lastMessage = LastMessage(text = "Hallo"),
            avatarInitial = "L",
            chatType = ChatType.PRIVATE
        )

        viewModel.onRequestDeleteChat(testChat)

        val state = viewModel.uiState.value
        assertNotNull(state.pendingDeleteChat)
        assertEquals("priv_lena", state.pendingDeleteChat?.id)
        assertEquals("Lena P.", state.pendingDeleteChat?.title)
    }

    @Test
    fun onConfirmDeleteChat_removesChatFromRepositoryAndState() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val testChat = viewModel.uiState.value.privateChats.first()

        viewModel.onRequestDeleteChat(testChat)
        viewModel.onConfirmDeleteChat()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.pendingDeleteChat)
        assertTrue(state.privateChats.none { it.id == testChat.id })
        assertTrue(chatRepository.deletedChatIds.contains(testChat.id))
    }

    @Test
    fun onDismissDeleteDialog_clearsPendingDeleteChatWithoutDeleting() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val testChat = viewModel.uiState.value.privateChats.first()

        viewModel.onRequestDeleteChat(testChat)
        viewModel.onDismissDeleteDialog()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.pendingDeleteChat)
        assertTrue(chatRepository.deletedChatIds.isEmpty())
    }

    @Test
    fun onArchiveChat_movesChatToArchivedList() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val testChat = viewModel.uiState.value.privateChats.first()

        viewModel.onArchiveChat(testChat)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.privateChats.none { it.id == testChat.id })
        assertTrue(state.archivedChats.any { it.id == testChat.id })
        assertTrue(chatRepository.archiveCalls.contains(testChat.id to true))
    }

    @Test
    fun onUnarchiveChat_restoresChatToActiveList() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val testChat = viewModel.uiState.value.privateChats.first()

        viewModel.onArchiveChat(testChat)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onUnarchiveChat(testChat)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.archivedChats.none { it.id == testChat.id })
        assertTrue(state.privateChats.any { it.id == testChat.id })
        assertTrue(chatRepository.archiveCalls.contains(testChat.id to false))
    }
}
