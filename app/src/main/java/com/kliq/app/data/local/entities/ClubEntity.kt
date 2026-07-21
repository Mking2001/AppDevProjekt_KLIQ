package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clubs")
data class ClubEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val rating: Float,
    val imageUrl: String,
    val region: String,
    val isFavorite: Boolean = false,
    val currentCapacityPercent: Int = 0,
    val malePercentage: Int = 0,
    val femalePercentage: Int = 0,
    val totalLiveVisitors: Int = 0
)
