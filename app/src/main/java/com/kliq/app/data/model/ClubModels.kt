package com.kliq.app.data.model

data class Club(
    val id: String,
    val name: String,
    val category: String,
    val rating: Float,
    val imageUrl: String,
    val region: String,
    val isFavorite: Boolean = false,
    val analytics: ClubAnalytics,
    val operatingHours: OperatingHours,
    val activeEvent: EventInfo? = null
)

data class ClubAnalytics(
    val currentCapacityPercent: Int, // 0 to 100
    val malePercentage: Int, // 0 to 100
    val femalePercentage: Int, // 0 to 100
    val totalLiveVisitors: Int
)

data class EventInfo(
    val title: String,
    val description: String,
    val price: String,
    val time: String
)

data class OperatingHours(
    val isOpenNow: Boolean,
    val todayHours: String,
    val allHours: Map<String, String>
)
