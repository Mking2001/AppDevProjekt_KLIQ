package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "club_offers",
    foreignKeys = [
        ForeignKey(
            entity = ClubEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("clubId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["clubId"])
    ]
)
data class ClubOfferEntity(
    @PrimaryKey val id: String,
    val clubId: String,
    val title: String,
    val description: String,
    val offerType: String,
    val discountCode: String? = null,
    val discountPercentage: Int? = null,
    val validUntil: Long? = null,
    val imageUrl: String? = null,
    val termsAndConditions: String? = null,
    val isExclusive: Boolean = false
)
