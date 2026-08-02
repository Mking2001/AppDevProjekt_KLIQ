package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ClubOfferDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.entities.ClubOfferEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.model.EventCategory
import com.kliq.app.data.model.OfferType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class ClubEventOfferRepositoryTest {

    private val eventDao: EventDao = mock(EventDao::class.java)
    private val clubOfferDao: ClubOfferDao = mock(ClubOfferDao::class.java)
    private lateinit var repository: ClubEventOfferRepositoryImpl

    @Before
    fun setUp() {
        repository = ClubEventOfferRepositoryImpl(eventDao, clubOfferDao)
    }

    @Test
    fun getOffersForClub_mapsEntitiesToDomainCorrectly() = runTest {
        val clubId = "club_watergate"
        val offerEntity = ClubOfferEntity(
            id = "off_1",
            clubId = clubId,
            title = "Watergate Special",
            description = "Welcome Drink",
            offerType = OfferType.DRINK_SPECIAL.name,
            discountCode = "WATERGATE20",
            discountPercentage = 20,
            isExclusive = true
        )

        `when`(clubOfferDao.getOffersByClubId(clubId)).thenReturn(flowOf(listOf(offerEntity)))

        val offers = repository.getOffersForClub(clubId).first()
        assertEquals(1, offers.size)
        val offer = offers[0]
        assertEquals("off_1", offer.id)
        assertEquals("Watergate Special", offer.title)
        assertEquals(OfferType.DRINK_SPECIAL, offer.offerType)
        assertEquals("WATERGATE20", offer.discountCode)
        assertEquals(true, offer.isExclusive)
    }

    @Test
    fun getEventsForClub_mapsEntitiesToDomainCorrectly() = runTest {
        val clubId = "club_watergate"
        val eventEntity = EventEntity(
            id = "evt_1",
            clubId = clubId,
            title = "Open Air Night",
            description = "Summer Session",
            category = EventCategory.PARTY.name,
            price = "15 €"
        )

        `when`(eventDao.getEventsByClubId(clubId)).thenReturn(flowOf(listOf(eventEntity)))

        val events = repository.getEventsForClub(clubId).first()
        assertEquals(1, events.size)
        val event = events[0]
        assertEquals("evt_1", event.id)
        assertEquals("Open Air Night", event.title)
        assertEquals(EventCategory.PARTY, event.category)
        assertEquals("15 €", event.price)
    }
}
