package com.kliq.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * Animated Favorite Heart Toggle Button for Kliq dark mode UI.
 * Uses high-contrast Kliq Purple Accent (#8A2BE2) for the active state
 * and a spring scaling animation when toggling state.
 */
@Composable
fun AnimatedFavoriteButton(
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = Color(0xFF8A2BE2),
    inactiveColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.25f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "favorite_scale_anim"
    )

    val tintColor by animateColorAsState(
        targetValue = if (isFavorite) activeColor else inactiveColor,
        label = "favorite_color_anim"
    )

    val interactionSource = remember { MutableInteractionSource() }

    IconButton(
        onClick = onToggleFavorite,
        modifier = modifier
            .semantics {
                contentDescription = if (isFavorite) "Aus Favoriten entfernen" else "Zu Favoriten hinzufügen"
            },
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.scale(scale)
        )
    }
}
