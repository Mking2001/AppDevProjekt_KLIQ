package com.kliq.app.viewmodel

import com.kliq.app.data.model.ClubEvent
import com.kliq.app.data.model.ClubOffer
import com.kliq.app.data.model.EventCategory
import com.kliq.app.data.model.OfferType
import com.kliq.app.data.repository.ClubEventOfferRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@OptIn(ExperimentalCoroutinesApi::class)
class ClubEventOfferViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: ClubEventOfferRepository = mock(ClubEventOfferRepository::class.java)
    private lateinit var viewModel: ClubEventOfferViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ClubEventOfferViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadEventsAndOffers_successState() = runTest {
        val clubId = "club_berghain"
        val sampleEvent = ClubEvent(
            id = "e1",
            clubId = clubId,
            title = "Klubnacht",
            description = "Techno party",
            category = EventCategory.PARTY,
            startTime = 1000L,
            endTime = 2000L,
            price = "20 €"
        )
        val sampleOffer = ClubOffer(
            id = "o1",
            clubId = clubId,
            title = "2 für 1",
            description = "Drink deal",
            offerType = OfferType.DRINK_SPECIAL,
            discountCode = "KLIQ20"
        )

        `when`(repository.getEventsForClub(clubId)).thenReturn(flowOf(listOf(sampleEvent)))
        `when`(repository.getOffersForClub(clubId)).thenReturn(flowOf(listOf(sampleOffer)))

        viewModel.loadEventsAndOffers(clubId)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(repository).refreshClubEventsAndOffers(clubId)
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.events.size)
        assertEquals("Klubnacht", state.events[0].title)
        assertEquals(1, state.offers.size)
        assertEquals("KLIQ20", state.offers[0].discountCode)
        assertNull(state.errorMessage)
    }

    @Test
    fun selectOffer_updatesSelectedOffer() = runTest {
        val sampleOffer = ClubOffer(
            id = "o1",
            clubId = "club_1",
            title = "Special",
            description = "Desc",
            offerType = OfferType.SPECIAL_DEAL
        )

        viewModel.selectOffer(sampleOffer)
        assertEquals(sampleOffer, viewModel.uiState.value.selectedOffer)

        viewModel.selectOffer(null)
        assertNull(viewModel.uiState.value.selectedOffer)
    }

    @Test
    fun onCodeCopied_setsMessageAndClears() = runTest {
        viewModel.onCodeCopied("PROMO2026")
        assertEquals("Gutscheincode 'PROMO2026' kopiert!", viewModel.uiState.value.codeCopiedMessage)

        viewModel.clearCopiedMessage()
        assertNull(viewModel.uiState.value.codeCopiedMessage)
    }
}
