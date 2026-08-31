package com.kliq.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

object KliqScreenTransitions {

    val KliqDecelerationEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
    val KliqAccelerateEasing = CubicBezierEasing(0.4f, 0.0f, 1.0f, 1.0f)

    const val DURATION_TAB_SWITCH = 300
    const val DURATION_DETAIL_PUSH = 320
    const val DURATION_SHARED_ELEMENT = 380
    const val DURATION_MODAL = 350
    const val DURATION_FADE = 250

    fun AnimatedContentTransitionScope<NavBackStackEntry>.tabEnterTransition(
        slideRight: Boolean,
        durationMs: Int = DURATION_TAB_SWITCH
    ): EnterTransition {
        val direction = if (slideRight) {
            AnimatedContentTransitionScope.SlideDirection.Start
        } else {
            AnimatedContentTransitionScope.SlideDirection.End
        }
        return slideIntoContainer(
            towards = direction,
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing),
            initialOffset = { fullWidth -> fullWidth / 4 }
        ) + fadeIn(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.tabExitTransition(
        slideRight: Boolean,
        durationMs: Int = DURATION_TAB_SWITCH
    ): ExitTransition {
        val direction = if (slideRight) {
            AnimatedContentTransitionScope.SlideDirection.Start
        } else {
            AnimatedContentTransitionScope.SlideDirection.End
        }
        return slideOutOfContainer(
            towards = direction,
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing),
            targetOffset = { fullWidth -> fullWidth / 4 }
        ) + fadeOut(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.detailPushEnterTransition(
        durationMs: Int = DURATION_DETAIL_PUSH
    ): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> (fullWidth * 0.85f).toInt() },
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + fadeIn(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + scaleIn(
            initialScale = 0.96f,
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.detailPushExitTransition(
        durationMs: Int = DURATION_DETAIL_PUSH
    ): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> -(fullWidth * 0.25f).toInt() },
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + fadeOut(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + scaleOut(
            targetScale = 0.96f,
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.detailPopEnterTransition(
        durationMs: Int = DURATION_DETAIL_PUSH
    ): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -(fullWidth * 0.25f).toInt() },
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + fadeIn(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + scaleIn(
            initialScale = 0.96f,
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.detailPopExitTransition(
        durationMs: Int = DURATION_DETAIL_PUSH
    ): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> (fullWidth * 0.85f).toInt() },
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + fadeOut(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + scaleOut(
            targetScale = 0.96f,
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.sharedElementExpandEnterTransition(
        durationMs: Int = DURATION_SHARED_ELEMENT
    ): EnterTransition {
        return scaleIn(
            initialScale = 0.90f,
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight / 3 },
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + fadeIn(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.sharedElementExpandExitTransition(
        durationMs: Int = DURATION_SHARED_ELEMENT
    ): ExitTransition {
        return scaleOut(
            targetScale = 0.95f,
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + fadeOut(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.sharedElementPopExitTransition(
        durationMs: Int = DURATION_SHARED_ELEMENT
    ): ExitTransition {
        return scaleOut(
            targetScale = 0.90f,
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight / 3 },
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + fadeOut(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.modalSlideUpEnterTransition(
        durationMs: Int = DURATION_MODAL
    ): EnterTransition {
        return slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + fadeIn(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.modalSlideUpExitTransition(
        durationMs: Int = DURATION_MODAL
    ): ExitTransition {
        return slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        ) + fadeOut(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultFadeEnterTransition(
        durationMs: Int = DURATION_FADE
    ): EnterTransition {
        return fadeIn(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }

    fun AnimatedContentTransitionScope<NavBackStackEntry>.defaultFadeExitTransition(
        durationMs: Int = DURATION_FADE
    ): ExitTransition {
        return fadeOut(
            animationSpec = tween(durationMillis = durationMs, easing = KliqDecelerationEasing)
        )
    }
}
