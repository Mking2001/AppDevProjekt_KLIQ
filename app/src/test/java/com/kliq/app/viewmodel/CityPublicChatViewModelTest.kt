package com.kliq.app.viewmodel

import com.kliq.app.data.model.LocationData
import com.kliq.app.data.repository.ChatRepository
import com.kliq.app.data.repository.LocationRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.util.CityChatLocationMapper
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class CityPublicChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val chatRepository: ChatRepository = mock(ChatRepository::class.java)
    private val locationRepository: LocationRepository = mock(LocationRepository::class.java)
    private val locationUpdatesFlow = MutableStateFlow<LocationData?>(null)

    private lateinit var viewModel: ChatListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(userRepository.getBlockedUserIds("current_user")).thenReturn(flowOf(emptyList()))
        `when`(chatRepository.getAllChats()).thenReturn(flowOf(emptyList()))
        `when`(locationRepository.locationUpdates).thenReturn(locationUpdatesFlow)

        viewModel = ChatListViewModel(userRepository, chatRepository, locationRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLocationUpdatesReflectActiveGpsCityChat() = runTest {
        val hamburgLocation = LocationData(latitude = 53.5511, longitude = 9.9937)
        locationUpdatesFlow.value = hamburgLocation
        testDispatcher.scheduler.advanceUntilIdle()

        val activeChat = viewModel.uiState.value.activeGpsCityChat
        assertNotNull(activeChat)
        assertEquals("Hamburg - Reeperbahn", activeChat?.title)
        assertTrue(activeChat?.isGpsAssigned == true)
    }

    @Test
    fun testManualCitySelectionUpdatesActiveChat() {
        val munichConfig = CityChatLocationMapper.SUPPORTED_CITIES[1] // Munich
        viewModel.selectCityChat(munichConfig)

        val activeChat = viewModel.uiState.value.activeGpsCityChat
        assertEquals("München - Party Radar", activeChat?.title)
        assertFalse(viewModel.uiState.value.isCitySwitcherOpen)
    }

    @Test
    fun testCitySwitcherOpenAndClose() {
        viewModel.openCitySwitcher()
        assertTrue(viewModel.uiState.value.isCitySwitcherOpen)

        viewModel.closeCitySwitcher()
        assertFalse(viewModel.uiState.value.isCitySwitcherOpen)
    }
}
