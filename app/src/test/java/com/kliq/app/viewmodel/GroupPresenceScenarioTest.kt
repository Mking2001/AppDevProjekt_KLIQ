package com.kliq.app.viewmodel

import com.kliq.app.data.datasource.GroupPresenceDataSourceImpl
import com.kliq.app.data.model.GroupMemberRole
import com.kliq.app.data.model.UserStatus
import com.kliq.app.data.repository.GroupPresenceRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GroupPresenceScenarioTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var dataSource: GroupPresenceDataSourceImpl
    private lateinit var repository: GroupPresenceRepositoryImpl
    private lateinit var viewModel: GroupPresenceViewModel

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        dataSource = GroupPresenceDataSourceImpl()
        repository = GroupPresenceRepositoryImpl(dataSource, testDispatcher)
        viewModel = GroupPresenceViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun step1_simulateUserJoinPublicCityChat() = runTest(testDispatcher) {
        val chatId = "pub_1" // Berlin - Tonight
        viewModel.loadGroupPresence(chatId)

        val state = viewModel.uiState.value
        assertEquals("pub_1", state.chatId)
        assertEquals("Berlin - Tonight", state.chatTitle)
        assertTrue("Online count must be > 0 when joining public city chat", state.totalOnlineCount > 0)
    }

    @Test
    fun step2_generateAndVerifyMockPresenceDataWithDifferentStates() = runTest(testDispatcher) {
        val chatId = "pub_1"
        viewModel.loadGroupPresence(chatId)

        val members = viewModel.uiState.value.onlineMembers
        val hasOnline = members.any { it.status == UserStatus.ONLINE }
        val hasAway = members.any { it.status == UserStatus.AWAY }
        val hasOffline = members.any { it.status == UserStatus.OFFLINE }

        assertTrue("Mock data should include ONLINE members", hasOnline)
        assertTrue("Mock data should include AWAY members", hasAway)
        assertTrue("Mock data should include OFFLINE members", hasOffline)
    }

    @Test
    fun step3_verifyDynamicPresenceHeaderAndBadgeReactivity() = runTest(testDispatcher) {
        val chatId = "pub_1"
        viewModel.loadGroupPresence(chatId)

        val initialOnlineCount = viewModel.uiState.value.totalOnlineCount
        assertTrue(initialOnlineCount > 0)

        // Simulating status change of current user to AWAY
        viewModel.updateMyPresenceStatus(UserStatus.AWAY)

        val updatedState = viewModel.uiState.value
        assertEquals(UserStatus.AWAY, updatedState.myPresenceStatus)

        // Verify repository reflects update without delay
        val summary = repository.observeGroupPresence(chatId).first()
        val currentUserMember = summary.members.find { it.userId == "current_user" }
        assertNotNull("current_user should be present in city presence summary", currentUserMember)
        assertEquals(UserStatus.AWAY, currentUserMember?.status)
    }

    @Test
    fun step4_verifyFilteringAndAccessibilityMetadata() = runTest(testDispatcher) {
        val chatId = "pub_1"
        viewModel.loadGroupPresence(chatId)

        // Filter for specific user
        viewModel.onSearchQueryChanged("Sophie")
        val filtered = viewModel.uiState.value.filteredMembers

        assertEquals(1, filtered.size)
        val sophie = filtered.first()
        assertEquals("Sophie W.", sophie.displayName)
        assertEquals(GroupMemberRole.VIP, sophie.role)
        assertEquals(UserStatus.ONLINE, sophie.status)
    }
}
