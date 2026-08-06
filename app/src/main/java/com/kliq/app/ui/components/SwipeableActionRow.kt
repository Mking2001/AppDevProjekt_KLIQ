package com.kliq.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.kliq.app.ui.theme.ErrorRed
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.util.HapticFeedbackUtils

/**
 * Reusable wrapper that adds swipe-to-action behavior to any list item in Kliq.
 * Swiping EndToStart (Swipe Left) triggers [onArchive] with Purple (#8A2BE2) background.
 * Swiping StartToEnd (Swipe Right) triggers [onDelete] with Red background and confirmation safety check.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableActionRow(
    onDelete: () -> Unit,
    onArchive: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    com.kliq.app.util.HapticFeedbackUtils.triggerPattern(view, com.kliq.app.util.HapticFeedbackPattern.HEAVY_CLICK)
                    onDelete()
                    false // Return false so item stays rendered until confirmed in dialog
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    com.kliq.app.util.HapticFeedbackUtils.triggerPattern(view, com.kliq.app.util.HapticFeedbackPattern.LIGHT_CLICK)
                    onArchive()
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                    SwipeToDismissBoxValue.StartToEnd -> ErrorRed // Rot für Löschen
                    SwipeToDismissBoxValue.EndToStart -> PurplePrimary // Kliq Lila für Archivieren
                }, 
                label = "swipe_color"
            )

            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                SwipeToDismissBoxValue.Settled -> Alignment.Center
            }
            
            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Delete
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Archive
                SwipeToDismissBoxValue.Settled -> Icons.Default.Archive
            }

            val scale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1.1f,
                label = "swipe_icon_scale"
            )

            if (direction != SwipeToDismissBoxValue.Settled) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(horizontal = 20.dp),
                    contentAlignment = alignment
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Swipe Action",
                        modifier = Modifier.scale(scale),
                        tint = Color.White
                    )
                }
            }
        },
        content = {
            content()
        }
    )
}

