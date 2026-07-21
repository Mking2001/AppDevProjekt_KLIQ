package com.kliq.app.data.remote.mapper

import com.google.gson.Gson
import com.kliq.app.data.local.entities.ClubEntity
import com.kliq.app.data.local.entities.EventEntity
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.Event
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.model.SpecialOffer
import com.kliq.app.data.remote.model.ExternalClubSearchResultDto
import com.kliq.app.data.remote.model.ExternalEventSearchResultDto
import com.kliq.app.data.remote.model.ExternalSpecialOfferDto

object ExternalSearchResultMapper {
    private val gson = Gson()

    fun ExternalClubSearchResultDto.toDomain(isFavorite: Boolean = false): Club {
        return Club(
            id = placeId,
            name = name,
            location = GpsLocation(
                latitude = latitude,
                longitude = longitude,
                address = address
            ),
            geofenceRadiusMeters = geofenceRadius ?: 200.0,
            averageRating = rating,
            operatingHours = OperatingHours(
                isOpenNow = isOpenNow,
                todayHours = openingHoursText
            ),
            isFavorite = isFavorite,
            category = category,
            imageUrl = imageUrl,
            externalSearchTags = searchTags.joinToString(", "),
            websiteUrl = websiteUrl
        )
    }

    fun ExternalClubSearchResultDto.toEntity(isFavorite: Boolean = false): ClubEntity {
        val hoursJson = gson.toJson(
            mapOf("isOpenNow" to isOpenNow, "todayHours" to openingHoursText)
        )
        return ClubEntity(
            id = placeId,
            name = name,
            latitude = latitude,
            longitude = longitude,
            address = address,
            geofenceRadiusMeters = geofenceRadius ?: 200.0,
            averageRating = rating,
            openingHoursJson = hoursJson,
            isFavorite = isFavorite,
            category = category,
            rating = rating.toFloat(),
            imageUrl = imageUrl,
            externalSearchTags = searchTags.joinToString(", "),
            websiteUrl = websiteUrl
        )
    }

    fun ExternalEventSearchResultDto.toDomain(): Event {
        return Event(
            id = eventId,
            clubId = clubPlaceId,
            title = title,
            description = description,
            startTime = startTimestamp,
            endTime = endTimestamp,
            price = ticketPrice,
            specialOffers = specialOffers.map { it.toDomain() },
            searchKeywords = keywords.joinToString(", "),
            imageUrl = imageUrl
        )
    }

    fun ExternalEventSearchResultDto.toEntity(): EventEntity {
        val domainOffers = specialOffers.map { it.toDomain() }
        val offersJson = gson.toJson(domainOffers)
        return EventEntity(
            id = eventId,
            clubId = clubPlaceId,
            title = title,
            description = description,
            startTime = startTimestamp,
            endTime = endTimestamp,
            price = ticketPrice,
            specialOffersJson = offersJson,
            searchKeywords = keywords.joinToString(", "),
            imageUrl = imageUrl
        )
    }

    private fun ExternalSpecialOfferDto.toDomain(): SpecialOffer {
        return SpecialOffer(
            id = offerId,
            title = title,
            discountDescription = discountDescription,
            validUntil = validUntil
        )
    }
}
