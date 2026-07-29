package com.kliq.app.data.repository

import com.kliq.app.data.datasource.GroupPresenceDataSourceImpl
import com.kliq.app.data.model.UserStatus
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
class GroupPresenceRepositoryTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var dataSource: GroupPresenceDataSourceImpl
    private lateinit var repository: GroupPresenceRepositoryImpl

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        dataSource = GroupPresenceDataSourceImpl()
        repository = GroupPresenceRepositoryImpl(dataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun observeGroupPresence_returnsDefaultBerlinPresence() = runTest(testDispatcher) {
        val summary = repository.observeGroupPresence("pub_1").first()

        assertNotNull(summary)
        assertEquals("Berlin - Tonight", summary.chatTitle)
        assertEquals(248, summary.totalOnlineCount)
        assertTrue(summary.members.size >= 5)
    }

    @Test
    fun filterActiveMembers_returnsFilteredList() = runTest(testDispatcher) {
        val filtered = repository.filterActiveMembers("pub_1", "Lukas").first()

        assertEquals(1, filtered.size)
        assertEquals("Lukas K.", filtered.first().displayName)
    }

    @Test
    fun updatePresenceStatus_updatesOnlineCountAndStatus() = runTest(testDispatcher) {
        val result = repository.updatePresenceStatus("pub_1", "u_1", UserStatus.AWAY)

        assertTrue(result.isSuccess)
        val updatedSummary = repository.observeGroupPresence("pub_1").first()
        val updatedMember = updatedSummary.members.find { it.userId == "u_1" }
        assertNotNull(updatedMember)
        assertEquals(UserStatus.AWAY, updatedMember?.status)
    }
}
