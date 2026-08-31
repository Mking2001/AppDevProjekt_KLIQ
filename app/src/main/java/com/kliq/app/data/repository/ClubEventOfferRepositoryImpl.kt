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
