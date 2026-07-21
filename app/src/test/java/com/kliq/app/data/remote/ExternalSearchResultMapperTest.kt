package com.kliq.app.data.remote

import com.kliq.app.data.remote.mapper.ExternalSearchResultMapper.toDomain
import com.kliq.app.data.remote.mapper.ExternalSearchResultMapper.toEntity
import com.kliq.app.data.remote.model.ExternalClubSearchResultDto
import com.kliq.app.data.remote.model.ExternalEventSearchResultDto
import com.kliq.app.data.remote.model.ExternalSpecialOfferDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExternalSearchResultMapperTest {

    @Test
    fun testClubDtoToDomainAndEntityMapping() {
        val dto = ExternalClubSearchResultDto(
            placeId = "ext_club_1",
            name = "KitKatClub",
            address = "Köpenicker Str. 76, 10179 Berlin",
            latitude = 52.5112,
            longitude = 13.4182,
            rating = 4.8,
            geofenceRadius = 300.0,
            isOpenNow = true,
            openingHoursText = "23:00 - Open End",
            category = "Underground",
            imageUrl = "https://example.com/kitkat.jpg",
            websiteUrl = "https://kitkatclub.org",
            searchTags = listOf("techno", "electro", "berlin")
        )

        val domain = dto.toDomain(isFavorite = true)
        assertEquals("ext_club_1", domain.id)
        assertEquals("KitKatClub", domain.name)
        assertEquals(52.5112, domain.location.latitude, 0.0001)
        assertEquals(13.4182, domain.location.longitude, 0.0001)
        assertEquals(300.0, domain.geofenceRadiusMeters, 0.01)
        assertTrue(domain.isFavorite)

        val entity = dto.toEntity(isFavorite = false)
        assertEquals("ext_club_1", entity.id)
        assertEquals(52.5112, entity.latitude, 0.0001)
        assertEquals("techno, electro, berlin", entity.externalSearchTags)
    }

    @Test
    fun testEventDtoToDomainMapping() {
        val offerDto = ExternalSpecialOfferDto(
            offerId = "off_1",
            title = "Happy Hour",
            discountDescription = "Buy 1 get 1 free"
        )
        val eventDto = ExternalEventSearchResultDto(
            eventId = "evt_99",
            clubPlaceId = "ext_club_1",
            title = "Gegen Night",
            description = "Electronic party",
            startTimestamp = 1700000000000L,
            endTimestamp = 1700028800000L,
            ticketPrice = "20 €",
            specialOffers = listOf(offerDto),
            keywords = listOf("electronic", "queer")
        )

        val domainEvent = eventDto.toDomain()
        assertEquals("evt_99", domainEvent.id)
        assertEquals("ext_club_1", domainEvent.clubId)
        assertEquals(1, domainEvent.specialOffers.size)
        assertEquals("Happy Hour", domainEvent.specialOffers[0].title)

        val entityEvent = eventDto.toEntity()
        assertEquals("evt_99", entityEvent.id)
        assertTrue(entityEvent.specialOffersJson.contains("Happy Hour"))
    }
}
