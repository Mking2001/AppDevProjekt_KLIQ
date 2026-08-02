package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.remote.KliqApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class ClubRepositorySearchTest {

    private val testDispatcher = StandardTestDispatcher()
    private val clubDao: ClubDao = mock(ClubDao::class.java)
    private val apiService: KliqApiService = mock(KliqApiService::class.java)
    private lateinit var repository: ClubRepositoryImpl

    private val sampleEntity = ClubEntity(
        id = "club_1",
        name = "Watergate",
        latitude = 52.501,
        longitude = 13.444,
        address = "Falckensteinstr. 49, Berlin",
        region = "Berlin",
        category = "Electro",
        averageRating = 4.7
    )

    @Before
    fun setUp() {
        repository = ClubRepositoryImpl(
            clubDao = clubDao,
            apiService = apiService,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun searchClubsFiltered_returnsMappedDomainClubs() = runTest(testDispatcher) {
        `when`(clubDao.searchClubsFiltered("Watergate", "", "")).thenReturn(flowOf(listOf(sampleEntity)))

        val results = repository.searchClubsFiltered("Watergate", null, null).first()

        assertEquals(1, results.size)
        assertEquals("Watergate", results[0].name)
        assertEquals("Berlin", results[0].region)
    }

    @Test
    fun searchRegionsAndCities_groupsClubsByRegion() = runTest(testDispatcher) {
        `when`(clubDao.getAllClubs()).thenReturn(flowOf(listOf(sampleEntity)))

        val regions = repository.searchRegionsAndCities("Berlin").first()

        assertEquals(1, regions.size)
        assertEquals("Berlin", regions[0].regionName)
        assertEquals(1, regions[0].clubCount)
    }
}
