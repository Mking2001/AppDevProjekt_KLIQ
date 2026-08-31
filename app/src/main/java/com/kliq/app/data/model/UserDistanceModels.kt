package com.kliq.app.data.model

data class UserDistanceResult(
    val userId: String,
    val rawDistanceMeters: Double?,
    val isValid: Boolean = rawDistanceMeters != null
)

data class UserLocationSnapshot(
    val userId: String,
    val latitude: Double,
    val longitude: Double,
    val timestampMs: Long = System.currentTimeMillis()
)

data class NearbyUserDistance(
    val userId: String,
    val username: String,
    val avatarUrl: String? = null,
    val rawDistanceMeters: Double? = null,
    val formattedDistance: String = "",
    val isOnline: Boolean = true
)
