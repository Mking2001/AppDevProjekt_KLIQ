package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clubs")
data class ClubEntity(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val geofenceRadiusMeters: Double = 200.0,
    val averageRating: Double = 0.0,
    val openingHoursJson: String = "",
    val isFavorite: Boolean = false,
    val category: String = "",
    val rating: Float = 0.0f,
    val imageUrl: String = "",
    val region: String = "",
    val currentCapacityPercent: Int = 0,
    val malePercentage: Int = 0,
    val femalePercentage: Int = 0,
    val totalLiveVisitors: Int = 0,
    val externalSearchTags: String = "",
    val websiteUrl: String? = null,
    val isPromoted: Boolean = false,
    val city: String = "",
    val postalCode: String = ""
)
