package com.kliq.app.data.model

data class GpsLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

enum class LiveOpeningStatus(val label: String) {
    OPEN_NOW("Jetzt geöffnet"),
    CLOSING_SOON("Schließt bald"),
    CLOSED("Geschlossen")
}

data class DaySchedule(
    val dayOfWeek: String,
    val openTime: String = "",
    val closeTime: String = "",
    val isClosed: Boolean = false,
    val isOpen24h: Boolean = false
)

data class OperatingHours(
    val isOpenNow: Boolean = false,
    val todayHours: String = "",
    val weeklySchedule: Map<String, String> = emptyMap(),
    val structuredSchedule: List<DaySchedule> = emptyList()
)

data class ClubContactInfo(
    val phoneNumber: String? = null,
    val email: String? = null,
    val websiteUrl: String? = null,
    val instagramHandle: String? = null,
    val facebookUrl: String? = null
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
    val totalLiveVisitors: Int = 0,
    val maxCapacity: Int = 1500,
    val occupancyTrend: OccupancyTrend = OccupancyTrend.STABLE,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

data class Club(
    val id: String,
    val name: String,
    val location: GpsLocation,
    val geofenceRadiusMeters: Double = 200.0,
    val averageRating: Double = 0.0,
    val operatingHours: OperatingHours = OperatingHours(),
    val isFavorite: Boolean = false,
    val category: String = "",
    val imageUrl: String = "",
    val region: String = "",
    val analytics: ClubAnalytics = ClubAnalytics(),
    val activeEvent: Event? = null,
    val externalSearchTags: String = "",
    val websiteUrl: String? = null,
    val phoneNumber: String? = null,
    val contactEmail: String? = null,
    val contactInfo: ClubContactInfo = ClubContactInfo(
        phoneNumber = phoneNumber,
        email = contactEmail,
        websiteUrl = websiteUrl
    )
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
