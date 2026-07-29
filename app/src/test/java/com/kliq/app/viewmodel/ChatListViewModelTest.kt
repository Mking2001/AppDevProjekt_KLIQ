package com.kliq.app.viewmodel

import com.kliq.app.data.model.ChatType
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class ChatListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val chatRepository: ChatRepository = mock(ChatRepository::class.java)
    private lateinit var viewModel: ChatListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(userRepository.getBlockedUserIds("current_user")).thenReturn(flowOf(emptyList()))
        `when`(chatRepository.getAllChats()).thenReturn(flowOf(emptyList()))
        viewModel = ChatListViewModel(userRepository, chatRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialStateLoadsPublicAndPrivateChats() {
        val state = viewModel.uiState.value
        assertEquals(ChatType.PUBLIC_CITY, state.selectedTab)
        assertTrue(state.publicChats.isNotEmpty())
        assertTrue(state.privateChats.isNotEmpty())
        assertFalse(state.isSearchActive)
        assertEquals("", state.searchQuery)
    }

    @Test
    fun testTabSelectionUpdatesSelectedTab() {
        viewModel.onTabSelected(ChatType.PRIVATE)
        assertEquals(ChatType.PRIVATE, viewModel.uiState.value.selectedTab)

        viewModel.onTabSelected(ChatType.PUBLIC_CITY)
        assertEquals(ChatType.PUBLIC_CITY, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun testSearchQueryFiltering() {
        viewModel.onSearchQueryChanged("Berlin")
        val state = viewModel.uiState.value

        assertEquals(1, state.publicChats.size)
        assertEquals("Berlin - Tonight", state.publicChats.first().title)

        viewModel.onSearchQueryChanged("Lisa")
        val privateState = viewModel.uiState.value
        assertEquals(1, privateState.privateChats.size)
        assertEquals("Lisa W.", privateState.privateChats.first().title)
    }

    @Test
    fun testSearchToggleResetsQueryWhenClosed() {
        viewModel.onToggleSearch(true)
        assertTrue(viewModel.uiState.value.isSearchActive)

        viewModel.onSearchQueryChanged("München")
        assertEquals("München", viewModel.uiState.value.searchQuery)

        viewModel.onToggleSearch(false)
        assertFalse(viewModel.uiState.value.isSearchActive)
        assertEquals("", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun testBlockedUserExclusion() = runTest {
        `when`(userRepository.getBlockedUserIds("current_user")).thenReturn(flowOf(listOf("priv_1")))

        val newViewModel = ChatListViewModel(userRepository, chatRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = newViewModel.uiState.value
        assertTrue(state.privateChats.none { it.id == "priv_1" })
        assertTrue(state.privateChats.any { it.id == "priv_2" })
    }

    @Test
    fun testDeleteAndUndoDelete() {
        val initialCount = viewModel.uiState.value.privateChats.size
        val targetChat = viewModel.uiState.value.privateChats.first()

        viewModel.onChatDeleted(targetChat.id)
        val countAfterDelete = viewModel.uiState.value.privateChats.size
        assertEquals(initialCount - 1, countAfterDelete)

        viewModel.onUndoDelete(targetChat)
        val countAfterUndo = viewModel.uiState.value.privateChats.size
        assertEquals(initialCount, countAfterUndo)
    }
}
