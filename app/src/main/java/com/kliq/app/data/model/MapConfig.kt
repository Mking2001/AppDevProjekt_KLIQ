package com.kliq.app.data.model

import com.kliq.app.R

data class CameraPositionStateData(
    val latitude: Double = 46.6247,
    val longitude: Double = 14.3053,
    val zoom: Float = 13.5f,
    val tilt: Float = 0.0f,
    val bearing: Float = 0.0f
)

data class MapStyleConfig(
    val isCustomStyleEnabled: Boolean = true,
    val styleRawResId: Int = R.raw.map_style_dark_purple,
    val isBuildingEnabled: Boolean = true,
    val isIndoorEnabled: Boolean = false,
    val isTrafficEnabled: Boolean = false
)
