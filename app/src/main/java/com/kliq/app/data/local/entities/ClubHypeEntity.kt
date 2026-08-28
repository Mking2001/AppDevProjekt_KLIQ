package com.kliq.app.data.local.entities

import androidx.room.Entity

@Entity(
    tableName = "club_hypes",
    primaryKeys = ["clubId", "userId", "dateString"]
)
data class ClubHypeEntity(
    val clubId: String,
    val userId: String,
    val dateString: String,
    val createdAtMs: Long = System.currentTimeMillis()
)
