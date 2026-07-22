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
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.kliq.app.util.HapticFeedbackUtils

/**
 * Reusable wrapper that adds swipe-to-action behavior to any list item.
 * Swiping StartToEnd (Left to Right) triggers [onArchive].
 * Swiping EndToStart (Right to Left) triggers [onDelete].
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
    val dismissState = rememberDismissState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.DismissedToEnd -> {
                    HapticFeedbackUtils.triggerMediumImpact(view)
                    onArchive()
                    true
                }
                DismissValue.DismissedToStart -> {
                    HapticFeedbackUtils.triggerMediumImpact(view)
                    onDelete()
                    true
                }
                DismissValue.Default -> false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val direction = dismissState.dismissDirection
            
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    DismissValue.Default -> Color.Transparent
                    DismissValue.DismissedToEnd -> Color(0xFFE2B93B) // Gelb für Archiv
                    DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error // Rot für Delete
                }, 
                label = "swipe_color"
            )

            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
                null -> Alignment.Center
            }
            
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Archive
                DismissDirection.EndToStart -> Icons.Default.Delete
                null -> Icons.Default.Delete
            }

            val scale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f,
                label = "swipe_icon_scale"
            )

            if (direction != null) {
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
        dismissContent = {
            content()
        }
    )
}
