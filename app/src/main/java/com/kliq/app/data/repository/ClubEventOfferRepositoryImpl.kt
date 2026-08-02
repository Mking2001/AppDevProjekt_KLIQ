package com.kliq.app.data.repository

import com.kliq.app.data.local.dao.ClubOfferDao
import com.kliq.app.data.local.dao.EventDao
import com.kliq.app.data.local.entities.ClubOfferEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.model.ClubEvent
import com.kliq.app.data.model.ClubOffer
import com.kliq.app.data.model.EventCategory
import com.kliq.app.data.model.OfferType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClubEventOfferRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val clubOfferDao: ClubOfferDao
) : ClubEventOfferRepository {

    override fun getEventsForClub(clubId: String): Flow<List<ClubEvent>> {
        return eventDao.getEventsByClubId(clubId).map { entities ->
            entities.map { it.toClubEvent() }
        }.flowOn(Dispatchers.IO)
    }

    override fun getOffersForClub(clubId: String): Flow<List<ClubOffer>> {
        return clubOfferDao.getOffersByClubId(clubId).map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun saveEvents(events: List<ClubEvent>) = withContext(Dispatchers.IO) {
        eventDao.insertEvents(events.map { it.toEntity() })
    }

    override suspend fun saveOffers(offers: List<ClubOffer>) = withContext(Dispatchers.IO) {
        clubOfferDao.insertOffers(offers.map { it.toEntity() })
    }

    override suspend fun refreshClubEventsAndOffers(clubId: String) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val mockEvents = listOf(
            ClubEvent(
                id = "evt_${clubId}_1",
                clubId = clubId,
                title = "Midnight Techno Rave & Visuals",
                description = "Dark Beats, Immersive Light-Show und Headline DJ Sets.",
                category = EventCategory.PARTY,
                startTime = now + 86400000L,
                endTime = now + 129600000L,
                price = "20,00 €",
                imageUrl = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7",
                isVipEvent = true
            ),
            ClubEvent(
                id = "evt_${clubId}_2",
                clubId = clubId,
                title = "Deep House Sunset Session",
                description = "Melodischer House und entspannte Atmosphere im Open-Air-Bereich.",
                category = EventCategory.DJ_SET,
                startTime = now + 172800000L,
                endTime = now + 201600000L,
                price = "15,00 €",
                imageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745",
                isVipEvent = false
            )
        )

        val mockOffers = listOf(
            ClubOffer(
                id = "off_${clubId}_1",
                clubId = clubId,
                title = "2-for-1 Cocktail Special",
                description = "Erhalte zwei Signature Cocktails zum Preis von einem bis 01:00 Uhr.",
                offerType = OfferType.DRINK_SPECIAL,
                discountCode = "KLIQ2FOR1",
                discountPercentage = 50,
                validUntil = now + 259200000L,
                termsAndConditions = "Gilt an der Hauptbar. Nur einmal pro Gast einlösbar.",
                isExclusive = false
            ),
            ClubOffer(
                id = "off_${clubId}_2",
                clubId = clubId,
                title = "VIP Fast-Track & Welcome Shot",
                description = "Bevorzugter Einlass ohne Anstehen inklusive Premium Welcome Shot.",
                offerType = OfferType.VIP_ACTION,
                discountCode = "KLIQVIPGUEST",
                discountPercentage = 25,
                validUntil = now + 518400000L,
                termsAndConditions = "Gültig für Kliq-VIP Member am Gästeeingang.",
                isExclusive = true
            ),
            ClubOffer(
                id = "off_${clubId}_3",
                clubId = clubId,
                title = "5€ Eintrittsrabatt vor Mitternacht",
                description = "Zeige deinen Kliq QR-Code an der Kasse für ermäßigten Eintritt.",
                offerType = OfferType.ENTRY_DISCOUNT,
                discountCode = "EARLYBIRD5",
                discountPercentage = 20,
                validUntil = now + 172800000L,
                termsAndConditions = "Nur gültig vor 00:00 Uhr.",
                isExclusive = false
            )
        )

        eventDao.insertEvents(mockEvents.map { it.toEntity() })
        clubOfferDao.insertOffers(mockOffers.map { it.toEntity() })
    }

    private fun ClubOfferEntity.toDomain(): ClubOffer {
        return ClubOffer(
            id = id,
            clubId = clubId,
            title = title,
            description = description,
            offerType = runCatching { OfferType.valueOf(offerType) }.getOrDefault(OfferType.SPECIAL_DEAL),
            discountCode = discountCode,
            discountPercentage = discountPercentage,
            validUntil = validUntil,
            imageUrl = imageUrl,
            termsAndConditions = termsAndConditions,
            isExclusive = isExclusive
        )
    }

    private fun ClubOffer.toEntity(): ClubOfferEntity {
        return ClubOfferEntity(
            id = id,
            clubId = clubId,
            title = title,
            description = description,
            offerType = offerType.name,
            discountCode = discountCode,
            discountPercentage = discountPercentage,
            validUntil = validUntil,
            imageUrl = imageUrl,
            termsAndConditions = termsAndConditions,
            isExclusive = isExclusive
        )
    }

    private fun EventEntity.toClubEvent(): ClubEvent {
        return ClubEvent(
            id = id,
            clubId = clubId,
            title = title,
            description = description,
            category = runCatching { EventCategory.valueOf(category) }.getOrDefault(EventCategory.PARTY),
            startTime = startTime,
            endTime = endTime,
            price = price,
            imageUrl = imageUrl,
            isVipEvent = searchKeywords.contains("VIP", ignoreCase = true)
        )
    }

    private fun ClubEvent.toEntity(): EventEntity {
        return EventEntity(
            id = id,
            clubId = clubId,
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            price = price,
            category = category.name,
            searchKeywords = if (isVipEvent) "VIP, Special" else "Party",
            imageUrl = imageUrl
        )
    }
}
