package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.VisitedLogDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.remote.KliqApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class ClubFavoriteRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val clubDao: ClubDao = mock(ClubDao::class.java)
    private val visitedLogDao: VisitedLogDao = mock(VisitedLogDao::class.java)
    private val apiService: KliqApiService = mock(KliqApiService::class.java)

    private lateinit var repository: ClubRepositoryImpl

    @Before
    fun setUp() {
        repository = ClubRepositoryImpl(
            clubDao = clubDao,
            visitedLogDao = visitedLogDao,
            apiService = apiService,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun getFavoriteClubs_returnsOnlyFavoriteClubsFromDao() = runTest(testDispatcher) {
        val favoriteEntity = ClubEntity(
            id = "club_1",
            name = "Watergate",
            isFavorite = true
        )
        `when`(clubDao.getFavoriteClubs()).thenReturn(flowOf(listOf(favoriteEntity)))

        val result = repository.getFavoriteClubs().first()

        assertEquals(1, result.size)
        assertEquals("Watergate", result[0].name)
        assertTrue(result[0].isFavorite)
    }

    @Test
    fun toggleFavorite_invokesDaoUpdateWithInvertedState() = runTest(testDispatcher) {
        val clubId = "club_berghain"

        repository.toggleFavorite(clubId, currentFavoriteState = false)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(clubDao).updateFavoriteStatus(clubId, true)
    }
}
