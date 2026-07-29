package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.BlockedUserDao
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.BlockedUserEntity
import com.kliq.app.data.remote.BlockUserRequestDto
import com.kliq.app.data.remote.KliqApiService
import com.kliq.app.data.remote.ReportUserRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryReportingTest {

    private val testDispatcher = StandardTestDispatcher()
    private val userDao: UserDao = mock(UserDao::class.java)
    private val apiService = TestKliqApiService()
    private val blockedUserDao = TestBlockedUserDao()

    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = UserRepositoryImpl(
            userDao = userDao,
            apiService = apiService,
            reviewDao = null,
            ioDispatcher = testDispatcher,
            blockedUserDao = blockedUserDao
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isUserBlocked delegates to BlockedUserDao`() = runTest {
        blockedUserDao.blockedList.add(BlockedUserEntity("usr_1", "usr_2"))

        val result = repository.isUserBlocked("usr_1", "usr_2").first()
        assertTrue(result)
    }

    @Test
    fun `getBlockedUserIds returns list of blocked ids`() = runTest {
        blockedUserDao.blockedList.add(BlockedUserEntity("usr_1", "usr_2"))
        blockedUserDao.blockedList.add(BlockedUserEntity("usr_1", "usr_3"))

        val result = repository.getBlockedUserIds("usr_1").first()
        assertEquals(2, result.size)
        assertTrue(result.contains("usr_2"))
        assertTrue(result.contains("usr_3"))
    }

    @Test
    fun `blockUser inserts into local DAO and calls apiService`() = runTest {
        val result = repository.blockUser("usr_1", "usr_2", "Spam")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertEquals(1, blockedUserDao.blockedList.size)
        assertEquals("usr_1", blockedUserDao.blockedList[0].userId)
        assertEquals("usr_2", blockedUserDao.blockedList[0].blockedUserId)
        assertEquals("Spam", blockedUserDao.blockedList[0].reason)

        assertEquals(1, apiService.blockedUserRequests.size)
        assertEquals("usr_1", apiService.blockedUserRequests[0].currentUserId)
        assertEquals("usr_2", apiService.blockedUserRequests[0].targetUserId)
        assertEquals("Spam", apiService.blockedUserRequests[0].reason)
    }

    @Test
    fun `unblockUser deletes from local DAO and calls apiService`() = runTest {
        blockedUserDao.blockedList.add(BlockedUserEntity("usr_1", "usr_2"))

        val result = repository.unblockUser("usr_1", "usr_2")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertTrue(blockedUserDao.blockedList.isEmpty())

        assertEquals(1, apiService.unblockedUsers.size)
        assertEquals("usr_1", apiService.unblockedUsers[0].first)
        assertEquals("usr_2", apiService.unblockedUsers[0].second)
    }

    @Test
    fun `reportUser calls apiService with correct report payload`() = runTest {
        val result = repository.reportUser("usr_1", "usr_2", "Fake Profil", "Mehrere gefälschte Fotos")
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertEquals(1, apiService.reportedUserRequests.size)
        assertEquals("usr_1", apiService.reportedUserRequests[0].reporterUserId)
        assertEquals("usr_2", apiService.reportedUserRequests[0].targetUserId)
        assertEquals("Fake Profil", apiService.reportedUserRequests[0].reason)
        assertEquals("Mehrere gefälschte Fotos", apiService.reportedUserRequests[0].details)
    }

    private class TestKliqApiService : KliqApiService {
        val blockedUserRequests = mutableListOf<BlockUserRequestDto>()
        val unblockedUsers = mutableListOf<Pair<String, String>>()
        val reportedUserRequests = mutableListOf<ReportUserRequestDto>()

        override suspend fun getUserProfile(userId: String): com.kliq.app.data.local.entities.UserEntity {
            return com.kliq.app.data.local.entities.UserEntity(id = userId, username = "Test", email = "test@kliq.app")
        }

        override suspend fun searchExternalClubsAndEvents(
            query: String,
            latitude: Double?,
            longitude: Double?,
            radiusKm: Int?
        ): com.kliq.app.data.remote.model.ExternalSearchResponseDto {
            return com.kliq.app.data.remote.model.ExternalSearchResponseDto(emptyList(), emptyList())
        }

        override suspend fun reportUser(request: ReportUserRequestDto): Response<Unit> {
            reportedUserRequests.add(request)
            return Response.success(Unit)
        }

        override suspend fun blockUser(request: BlockUserRequestDto): Response<Unit> {
            blockedUserRequests.add(request)
            return Response.success(Unit)
        }

        override suspend fun unblockUser(currentUserId: String, targetUserId: String): Response<Unit> {
            unblockedUsers.add(Pair(currentUserId, targetUserId))
            return Response.success(Unit)
        }
    }

    private class TestBlockedUserDao : BlockedUserDao {
        val blockedList = mutableListOf<BlockedUserEntity>()

        override suspend fun getBlockedUser(currentUserId: String, targetUserId: String): BlockedUserEntity? {
            return blockedList.find { it.userId == currentUserId && it.blockedUserId == targetUserId }
        }

        override fun isUserBlockedFlow(currentUserId: String, targetUserId: String): Flow<Boolean> {
            val isBlocked = blockedList.any { it.userId == currentUserId && it.blockedUserId == targetUserId }
            return flowOf(isBlocked)
        }

        override suspend fun isUserBlockedOneShot(currentUserId: String, targetUserId: String): Boolean {
            return blockedList.any { it.userId == currentUserId && it.blockedUserId == targetUserId }
        }

        override fun getBlockedUserIdsFlow(currentUserId: String): Flow<List<String>> {
            val ids = blockedList.filter { it.userId == currentUserId }.map { it.blockedUserId }
            return flowOf(ids)
        }

        override suspend fun getBlockedUserIds(currentUserId: String): List<String> {
            return blockedList.filter { it.userId == currentUserId }.map { it.blockedUserId }
        }

        override suspend fun blockUser(entity: BlockedUserEntity) {
            blockedList.add(entity)
        }

        override suspend fun unblockUser(currentUserId: String, targetUserId: String) {
            blockedList.removeAll { it.userId == currentUserId && it.blockedUserId == targetUserId }
        }

        override fun getAllBlockedUsers(currentUserId: String): Flow<List<BlockedUserEntity>> {
            return flowOf(blockedList.filter { it.userId == currentUserId })
        }
    }
}
