package com.kliq.app.data.remote.model

import com.google.gson.annotations.SerializedName

data class ExternalClubSearchResultDto(
    @SerializedName("place_id") val placeId: String,
    @SerializedName("name") val name: String,
    @SerializedName("address") val address: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("rating") val rating: Double,
    @SerializedName("geofence_radius") val geofenceRadius: Double? = null,
    @SerializedName("is_open_now") val isOpenNow: Boolean = false,
    @SerializedName("opening_hours_text") val openingHoursText: String = "",
    @SerializedName("category") val category: String = "",
    @SerializedName("image_url") val imageUrl: String = "",
    @SerializedName("website_url") val websiteUrl: String? = null,
    @SerializedName("search_tags") val searchTags: List<String> = emptyList()
)

data class ExternalSpecialOfferDto(
    @SerializedName("offer_id") val offerId: String,
    @SerializedName("title") val title: String,
    @SerializedName("discount_description") val discountDescription: String,
    @SerializedName("valid_until") val validUntil: String? = null
)

data class ExternalEventSearchResultDto(
    @SerializedName("event_id") val eventId: String,
    @SerializedName("club_place_id") val clubPlaceId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("start_timestamp") val startTimestamp: Long,
    @SerializedName("end_timestamp") val endTimestamp: Long,
    @SerializedName("ticket_price") val ticketPrice: String = "",
    @SerializedName("special_offers") val specialOffers: List<ExternalSpecialOfferDto> = emptyList(),
    @SerializedName("keywords") val keywords: List<String> = emptyList(),
    @SerializedName("image_url") val imageUrl: String? = null
)

data class ExternalSearchResponseDto(
    @SerializedName("clubs") val clubs: List<ExternalClubSearchResultDto> = emptyList(),
    @SerializedName("events") val events: List<ExternalEventSearchResultDto> = emptyList(),
    @SerializedName("query") val query: String = "",
    @SerializedName("total_results") val totalResults: Int = 0
)

data class SearchFilterQueryDto(
    val queryText: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radiusKm: Int = 25,
    val openOnly: Boolean = false
)
