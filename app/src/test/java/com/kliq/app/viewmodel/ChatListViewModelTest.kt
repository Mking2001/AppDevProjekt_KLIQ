package com.kliq.app.viewmodel

import com.kliq.app.data.model.ChatConversation
import com.kliq.app.data.model.ChatType
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

/**
 * Prüft, dass die Chat-Übersicht ihre Inhalte vollständig aus dem
 * ChatRepository bezieht und Schreibvorgänge dorthin delegiert.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val sessionRepository: SessionRepository = mock(SessionRepository::class.java)
    private val locationRepository: LocationRepository = mock(LocationRepository::class.java)
    private val locationUpdatesFlow = MutableStateFlow<LocationData?>(null)

    private lateinit var chatRepository: FakeChatRepository
    private lateinit var viewModel: ChatListViewModel

    private val cityChat = ChatConversation(
        id = "pub_klagenfurt",
        name = "Klagenfurt - Tonight",
        cityRegion = "Klagenfurt",
        lastMessageText = "Wer ist heute im Volksgarten?",
        lastMessageTimestampMs = 3_000L,
        avatarInitial = "K",
        unreadCount = 3,
        chatType = ChatType.PUBLIC_CITY
    )

    private val privateChatLena = ChatConversation(
        id = "priv_lena",
        name = "Lena P.",
        lastMessageText = "Treffen wir uns vorher?",
        lastMessageTimestampMs = 2_000L,
        avatarInitial = "L",
        unreadCount = 2,
        chatType = ChatType.PRIVATE
    )

    private val privateChatDavid = ChatConversation(
        id = "priv_david",
        name = "David M.",
        lastMessageText = "Line-up ist online",
        lastMessageTimestampMs = 1_000L,
        avatarInitial = "D",
        chatType = ChatType.PRIVATE
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(userRepository.getBlockedUserIds(anyString())).thenReturn(flowOf(emptyList()))
        `when`(locationRepository.locationUpdates).thenReturn(locationUpdatesFlow)

        chatRepository = FakeChatRepository(
            initialChats = listOf(cityChat, privateChatLena, privateChatDavid)
        )
        viewModel = createViewModel()
    }

    private fun createViewModel() = ChatListViewModel(
        chatRepository = chatRepository,
        userRepository = userRepository,
        locationRepository = locationRepository,
        currentUserProvider = CurrentUserProvider(sessionRepository, userRepository)
    )

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_loadsPublicAndPrivateChatsFromRepository() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(ChatType.PUBLIC_CITY, state.selectedTab)
        assertEquals(1, state.publicChats.size)
        assertEquals(2, state.privateChats.size)
        assertFalse(state.isLoading)
        assertFalse(state.isSearchActive)
    }

    @Test
    fun tabSelection_updatesSelectedTab() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTabSelected(ChatType.PRIVATE)
        assertEquals(ChatType.PRIVATE, viewModel.uiState.value.selectedTab)

        viewModel.onTabSelected(ChatType.PUBLIC_CITY)
        assertEquals(ChatType.PUBLIC_CITY, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun searchQuery_filtersByTitleAndLastMessage() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onSearchQueryChanged("Klagenfurt")
        assertEquals(1, viewModel.uiState.value.publicChats.size)
        assertEquals("Klagenfurt - Tonight", viewModel.uiState.value.publicChats.first().title)

        viewModel.onSearchQueryChanged("Lena")
        assertEquals(1, viewModel.uiState.value.privateChats.size)
        assertEquals("Lena P.", viewModel.uiState.value.privateChats.first().title)

        viewModel.onSearchQueryChanged("Line-up")
        assertEquals(1, viewModel.uiState.value.privateChats.size)
        assertEquals("David M.", viewModel.uiState.value.privateChats.first().title)
    }

    @Test
    fun searchToggle_resetsQueryWhenClosed() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onToggleSearch(true)
        assertTrue(viewModel.uiState.value.isSearchActive)

        viewModel.onSearchQueryChanged("David")
        assertEquals("David", viewModel.uiState.value.searchQuery)

        viewModel.onToggleSearch(false)
        assertFalse(viewModel.uiState.value.isSearchActive)
        assertEquals("", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun blockedUser_isExcludedFromPrivateChats() = runTest {
        `when`(userRepository.getBlockedUserIds(anyString())).thenReturn(flowOf(listOf("priv_lena")))

        val newViewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = newViewModel.uiState.value
        assertTrue(state.privateChats.none { it.id == "priv_lena" })
        assertTrue(state.privateChats.any { it.id == "priv_david" })
    }

    @Test
    fun onChatOpened_resetsUnreadCounterInRepository() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onChatOpened("pub_klagenfurt")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(chatRepository.markedAsReadChatIds.contains("pub_klagenfurt"))
        assertEquals(0, viewModel.uiState.value.publicChats.first().unreadCount)
    }

    @Test
    fun locationUpdate_assignsNearestCityChat() = runTest {
        locationUpdatesFlow.value = LocationData(latitude = 46.6236, longitude = 14.3084)
        testDispatcher.scheduler.advanceUntilIdle()

        val activeChat = viewModel.uiState.value.activeGpsCityChat
        assertEquals("Klagenfurt - Tonight", activeChat?.title)
        assertTrue(activeChat?.isGpsAssigned == true)
    }
}
