package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.VisitedLogDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.remote.KliqApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class ClubRepositoryTest {

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
    fun testAddAndRemoveClubFromFavorites_updatesStateAndFlowInstantly() = runTest(testDispatcher) {
        val favoriteDbState = MutableStateFlow<List<ClubEntity>>(emptyList())
        `when`(clubDao.getFavoriteClubs()).thenReturn(favoriteDbState)

        val club1 = ClubEntity(id = "c_1", name = "Berghain", isFavorite = true)
        val club2 = ClubEntity(id = "c_2", name = "Watergate", isFavorite = true)

        var favorites = repository.getFavoriteClubs().first()
        assertTrue(favorites.isEmpty())

        favoriteDbState.value = listOf(club1)
        favorites = repository.getFavoriteClubs().first()
        assertEquals(1, favorites.size)
        assertEquals("Berghain", favorites[0].name)
        assertTrue(favorites[0].isFavorite)

        favoriteDbState.value = listOf(club1, club2)
        favorites = repository.getFavoriteClubs().first()
        assertEquals(2, favorites.size)

        favoriteDbState.value = listOf(club2)
        favorites = repository.getFavoriteClubs().first()
        assertEquals(1, favorites.size)
        assertEquals("Watergate", favorites[0].name)
    }

    @Test
    fun testToggleFavorite_flipsIsFavoriteState() = runTest(testDispatcher) {
        val clubId = "c_tresor"

        repository.toggleFavorite(clubId, currentFavoriteState = false)
        testDispatcher.scheduler.advanceUntilIdle()

        repository.toggleFavorite(clubId, currentFavoriteState = true)
        testDispatcher.scheduler.advanceUntilIdle()

        org.mockito.Mockito.verify(clubDao).updateFavoriteStatus(clubId, true)
        org.mockito.Mockito.verify(clubDao).updateFavoriteStatus(clubId, false)
    }
}
