package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity storing background location track points.
 *
 * @property id Auto-generated primary key.
 * @property latitude Geographic latitude coordinate.
 * @property longitude Geographic longitude coordinate.
 * @property accuracy Fix accuracy in meters.
 * @property timestampMs Epoch timestamp in milliseconds.
 * @property speed Speed in meters/second.
 */
@Entity(tableName = "user_locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestampMs: Long,
    val speed: Float = 0f
)
