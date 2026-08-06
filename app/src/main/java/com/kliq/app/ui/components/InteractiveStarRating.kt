package com.kliq.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kliq.app.ui.theme.DarkOutline
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurplePrimary

@Composable
fun InteractiveStarRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = 36.dp,
    starSpacing: Dp = 8.dp,
    activeColor: Color = Color(0xFFFFC107), // High-contrast Gold/Amber
    inactiveColor: Color = DarkOutline,
    isReadOnly: Boolean = false
) {
    var rowWidthPx by remember { mutableIntStateOf(0) }
    var isInteracting by remember { mutableStateOf(false) }

    fun updateRatingFromTouch(xPosition: Float) {
        if (rowWidthPx <= 0) return
        val fraction = (xPosition / rowWidthPx.toFloat()).coerceIn(0f, 1f)
        val calculatedStar = (fraction * maxStars).toInt() + 1
        val newRating = calculatedStar.coerceIn(1, maxStars)
        if (newRating != rating) {
            onRatingChanged(newRating)
        }
    }

    val gestureModifier = if (!isReadOnly) {
        Modifier
            .pointerInput(maxStars) {
                detectTapGestures(
                    onPress = { offset ->
                        isInteracting = true
                        updateRatingFromTouch(offset.x)
                        tryAwaitRelease()
                        isInteracting = false
                    },
                    onTap = { offset ->
                        updateRatingFromTouch(offset.x)
                    }
                )
            }
            .pointerInput(maxStars) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isInteracting = true
                        updateRatingFromTouch(offset.x)
                    },
                    onDragEnd = {
                        isInteracting = false
                    },
                    onDragCancel = {
                        isInteracting = false
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        updateRatingFromTouch(change.position.x)
                    }
                )
            }
    } else {
        Modifier
    }

    val accessibilityDescription = if (isReadOnly) {
        "Bewertung: $rating von $maxStars Sternen"
    } else {
        "Interaktive Bewertung: $rating von $maxStars Sternen. Wischen oder tippen zum Ändern."
    }

    Row(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                rowWidthPx = coordinates.size.width
            }
            .then(gestureModifier)
            .padding(vertical = 4.dp)
            .clearAndSetSemantics {
                contentDescription = accessibilityDescription
                stateDescription = "$rating von $maxStars Sternen"
                role = androidx.compose.ui.semantics.Role.RadioButton
                if (!isReadOnly) {
                    customActions = listOf(
                        androidx.compose.ui.semantics.CustomAccessibilityAction("Wert erhöhen") {
                            if (rating < maxStars) {
                                onRatingChanged(rating + 1)
                                true
                            } else false
                        },
                        androidx.compose.ui.semantics.CustomAccessibilityAction("Wert verringern") {
                            if (rating > 1) {
                                onRatingChanged(rating - 1)
                                true
                            } else false
                        }
                    )
                }
            },
        horizontalArrangement = Arrangement.spacedBy(starSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (starIndex in 1..maxStars) {
            val isFilled = starIndex <= rating
            val starScale by animateFloatAsState(
                targetValue = if (isFilled && isInteracting) 1.25f else if (isFilled) 1.1f else 1.0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "star_scale_anim"
            )

            Icon(
                imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (isFilled) activeColor else inactiveColor,
                modifier = Modifier
                    .size(starSize)
                    .scale(starScale)
            )
        }
    }
}

