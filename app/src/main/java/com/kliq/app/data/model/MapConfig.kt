package com.kliq.app.data.model

import com.kliq.app.R

/**
 * Domain model representing map camera viewport and positioning.
 */
data class CameraPositionStateData(
    val latitude: Double = 46.6236, // Klagenfurt, Neuer Platz
    val longitude: Double = 14.3084,
    val zoom: Float = 13.5f,
    val tilt: Float = 0.0f,
    val bearing: Float = 0.0f
)

/**
 * Domain model for Map rendering configuration and custom dark-purple styling.
 */
data class MapStyleConfig(
    val isCustomStyleEnabled: Boolean = true,
    val styleRawResId: Int = R.raw.map_style_dark_purple,
    val isBuildingEnabled: Boolean = true,
    val isIndoorEnabled: Boolean = false,
    val isTrafficEnabled: Boolean = false
)
