package com.kliq.app.data.model

import com.kliq.app.R

/**
 * Domain model representing map camera viewport and positioning.
 */
data class CameraPositionStateData(
    val latitude: Double = 52.5200, // Berlin Default Coordinates
    val longitude: Double = 13.4050,
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
