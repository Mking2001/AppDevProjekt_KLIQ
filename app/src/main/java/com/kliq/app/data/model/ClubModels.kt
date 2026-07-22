package com.kliq.app.data.model

data class GpsLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

data class OperatingHours(
    val isOpenNow: Boolean,
    val todayHours: String,
    val weeklySchedule: Map<String, String> = emptyMap()
)

data class SpecialOffer(
    val id: String,
    val title: String,
    val discountDescription: String,
    val validUntil: String? = null
)

data class ClubAnalytics(
    val currentCapacityPercent: Int = 0,
    val malePercentage: Int = 0,
    val femalePercentage: Int = 0,
    val totalLiveVisitors: Int = 0
)

data class Club(
    val id: String,
    val name: String,
    val location: GpsLocation,
    val geofenceRadiusMeters: Double = 200.0,
    val averageRating: Double,
    val operatingHours: OperatingHours,
    val isFavorite: Boolean = false,
    val category: String = "",
    val imageUrl: String = "",
    val region: String = "",
    val analytics: ClubAnalytics = ClubAnalytics(),
    val activeEvent: Event? = null,
    val externalSearchTags: String = "",
    val websiteUrl: String? = null
)

data class Event(
    val id: String,
    val clubId: String,
    val title: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val price: String,
    val specialOffers: List<SpecialOffer> = emptyList(),
    val searchKeywords: String = "",
    val imageUrl: String? = null
)
