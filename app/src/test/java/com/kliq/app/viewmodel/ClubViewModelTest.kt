package com.kliq.app.viewmodel

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.Event
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.model.SpecialOffer
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.EventRepository
import com.kliq.app.ui.model.HighContrastVioletPalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class ClubViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val clubRepository: ClubRepository = mock(ClubRepository::class.java)
    private val eventRepository: EventRepository = mock(EventRepository::class.java)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testHighContrastStateMappingForClubAndEvent() = runTest {
        val club = Club(
            id = "c_tresor",
            name = "Tresor Berlin",
            location = GpsLocation(52.5111, 13.4194, "Köpenicker Str. 70"),
            geofenceRadiusMeters = 200.0,
            averageRating = 4.6,
            operatingHours = OperatingHours(isOpenNow = true, todayHours = "23:59 - 08:00"),
            isFavorite = true,
            category = "Techno"
        )

        val event = Event(
            id = "e_tresor_1",
            clubId = "c_tresor",
            title = "Klubnacht Tresor",
            description = "Industrial techno",
            startTime = 1700000000000L,
            endTime = 1700028800000L,
            price = "18 €",
            specialOffers = listOf(SpecialOffer("so_1", "Free Shot", "Shot at the entrance"))
        )

        `when`(clubRepository.getAllClubs()).thenReturn(flowOf(listOf(club)))
        `when`(eventRepository.getAllEvents()).thenReturn(flowOf(listOf(event)))

        val viewModel = EventSearchViewModel(clubRepository, eventRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.clubs.size)
        assertEquals("Tresor Berlin", state.clubs[0].name)
        assertTrue(state.clubs[0].isFavorite)
        assertEquals(HighContrastVioletPalette.BadgeOpenGreen, state.clubs[0].openStatusBadgeColorHex)

        assertEquals(1, state.events.size)
        assertEquals("Klubnacht Tresor", state.events[0].title)
        assertEquals(1, state.events[0].specialOfferTags.size)
        assertTrue(state.events[0].specialOfferTags[0].contains("Free Shot"))
    }
}
