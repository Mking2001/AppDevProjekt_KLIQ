package com.kliq.app.viewmodel

import com.kliq.app.data.datasource.GroupPresenceDataSourceImpl
import com.kliq.app.data.model.UserStatus
import com.kliq.app.data.repository.GroupPresenceRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

@OptIn(ExperimentalCoroutinesApi::class)
class GroupPresenceViewModelTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var repository: GroupPresenceRepositoryImpl
    private lateinit var viewModel: GroupPresenceViewModel

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        val dataSource = GroupPresenceDataSourceImpl()
        repository = GroupPresenceRepositoryImpl(dataSource)
        viewModel = GroupPresenceViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasDefaults() {
        val state = viewModel.uiState.value
        assertNotNull(state)
        assertEquals("", state.chatId)
        assertEquals(0, state.totalOnlineCount)
        assertFalse(state.isParticipantSheetExpanded)
        assertEquals(UserStatus.ONLINE, state.myPresenceStatus)
    }

    @Test
    fun loadGroupPresence_populatesOnlineCountAndMembers() = runTest(testDispatcher) {
        viewModel.loadGroupPresence("pub_1")

        val state = viewModel.uiState.value
        assertEquals("pub_1", state.chatId)
        assertEquals("Berlin - Tonight", state.chatTitle)
        assertTrue(state.totalOnlineCount > 0)
        assertFalse(state.onlineMembers.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun onSearchQueryChanged_filtersParticipantList() = runTest(testDispatcher) {
        viewModel.loadGroupPresence("pub_1")
        viewModel.onSearchQueryChanged("Elena")

        val state = viewModel.uiState.value
        assertEquals("Elena", state.searchQuery)
        assertEquals(1, state.filteredMembers.size)
        assertEquals("Elena M.", state.filteredMembers.first().displayName)
    }

    @Test
    fun toggleParticipantSheet_switchesExpandedFlag() {
        assertFalse(viewModel.uiState.value.isParticipantSheetExpanded)

        viewModel.toggleParticipantSheet()
        assertTrue(viewModel.uiState.value.isParticipantSheetExpanded)

        viewModel.toggleParticipantSheet()
        assertFalse(viewModel.uiState.value.isParticipantSheetExpanded)
    }

    @Test
    fun updateMyPresenceStatus_updatesLocalAndRemoteState() = runTest(testDispatcher) {
        viewModel.loadGroupPresence("pub_1")
        viewModel.updateMyPresenceStatus(UserStatus.AWAY)

        assertEquals(UserStatus.AWAY, viewModel.uiState.value.myPresenceStatus)
    }
}
