package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = ClubEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("clubId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EventEntity(
    @PrimaryKey val id: String,
    val clubId: String,
    val title: String,
    val description: String,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val price: String = "",
    val time: String = "",
    val specialOffersJson: String = "",
    val searchKeywords: String = "",
    val imageUrl: String? = null
)
