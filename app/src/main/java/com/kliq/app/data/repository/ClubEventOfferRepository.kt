package com.kliq.app.data.repository

import com.kliq.app.data.model.ClubEvent
import com.kliq.app.data.model.ClubOffer
import kotlinx.coroutines.flow.Flow

interface ClubEventOfferRepository {
    fun getEventsForClub(clubId: String): Flow<List<ClubEvent>>
    fun getOffersForClub(clubId: String): Flow<List<ClubOffer>>
    suspend fun saveEvents(events: List<ClubEvent>)
    suspend fun saveOffers(offers: List<ClubOffer>)
    suspend fun refreshClubEventsAndOffers(clubId: String)
}
