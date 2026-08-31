package com.kliq.app.viewmodel

import com.kliq.app.data.model.LocationData
import com.kliq.app.data.repository.LocationRepository
import com.kliq.app.data.repository.SessionRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.util.CityChatLocationMapper
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class CityPublicChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val sessionRepository: SessionRepository = mock(SessionRepository::class.java)
    private val locationRepository: LocationRepository = mock(LocationRepository::class.java)
    private val locationUpdatesFlow = MutableStateFlow<LocationData?>(null)

    private lateinit var chatRepository: FakeChatRepository
    private lateinit var viewModel: ChatListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(userRepository.getBlockedUserIds(anyString())).thenReturn(flowOf(emptyList()))
        `when`(userRepository.getUserById(anyString())).thenReturn(flowOf(null))
        `when`(locationRepository.locationUpdates).thenReturn(locationUpdatesFlow)

        chatRepository = FakeChatRepository()
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
    fun locationUpdate_suggestsForeignCityWhenDifferentFromActive() = runTest {
        val villachLocation = LocationData(latitude = 46.6103, longitude = 13.8558)
        locationUpdatesFlow.value = villachLocation
        testDispatcher.scheduler.advanceUntilIdle()

        val suggested = viewModel.uiState.value.suggestedForeignCity
        assertNotNull(suggested)
        assertEquals("Villach - Party Radar", suggested?.title)
        assertTrue(viewModel.uiState.value.showCitySwitchSuggestion)
    }

    @Test
    fun locationUpdate_createsCityChatInRepository() = runTest {
        val grazConfig = CityChatLocationMapper.SUPPORTED_CITIES.first { it.cityRegion == "Graz" }
        viewModel.selectCityChat(grazConfig)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.publicChats.any { it.id == "pub_graz" })
    }

    @Test
    fun manualCitySelection_updatesActiveChatAndClosesSwitcher() = runTest {
        val grazConfig = CityChatLocationMapper.SUPPORTED_CITIES.first { it.cityRegion == "Graz" }

        viewModel.selectCityChat(grazConfig)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Graz - Nightlife", viewModel.uiState.value.activeCity.title)
        assertFalse(viewModel.uiState.value.isCitySwitcherOpen)
        assertTrue(viewModel.uiState.value.publicChats.any { it.id == grazConfig.id })
    }

    @Test
    fun citySwitcher_opensAndCloses() {
        viewModel.openCitySwitcher()
        assertTrue(viewModel.uiState.value.isCitySwitcherOpen)

        viewModel.closeCitySwitcher()
        assertFalse(viewModel.uiState.value.isCitySwitcherOpen)
    }

    @Test
    fun defaultCity_isKlagenfurtWhenNoLocationAvailable() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()

        val activeCity = viewModel.uiState.value.activeCity
        assertEquals("Klagenfurt - Tonight", activeCity.title)
        assertFalse(viewModel.uiState.value.showCitySwitchSuggestion)
    }
}
