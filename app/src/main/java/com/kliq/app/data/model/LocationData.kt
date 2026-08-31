package com.kliq.app.data.model

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val timestampMs: Long = System.currentTimeMillis(),
    val speed: Float = 0f,
    val isMock: Boolean = false
)
