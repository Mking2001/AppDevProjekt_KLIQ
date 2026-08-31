package com.kliq.app.ui.screens.profile

data class ProfileImageViewerState(
    val isFullscreenVisible: Boolean = false,
    val currentScale: Float = 1.0f,
    val translationOffsetX: Float = 0.0f,
    val translationOffsetY: Float = 0.0f,
    val targetImageUrl: String? = null
)
