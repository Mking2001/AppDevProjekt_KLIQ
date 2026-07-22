package com.kliq.app.ui.theme

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

object KliqAnimations {
    val slideInUp: EnterTransition = slideInVertically(
        initialOffsetY = { it / 2 },
        animationSpec = tween(durationMillis = 300)
    ) + fadeIn(animationSpec = tween(durationMillis = 300))

    val slideOutDown: ExitTransition = slideOutVertically(
        targetOffsetY = { it / 2 },
        animationSpec = tween(durationMillis = 300)
    ) + fadeOut(animationSpec = tween(durationMillis = 300))

    val defaultFadeIn: EnterTransition = fadeIn(animationSpec = tween(200))
    val defaultFadeOut: ExitTransition = fadeOut(animationSpec = tween(200))
}
