package com.kliq.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kliq.app.ui.screens.map.MapLocationFilterMode
import com.kliq.app.ui.theme.DarkOutline
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight

import com.kliq.app.util.accessibilityHeading
import com.kliq.app.util.ensureMinTouchTarget
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription

@Composable
fun MapFilterSegmentedControl(
    selectedMode: MapLocationFilterMode,
    onModeSelected: (MapLocationFilterMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .accessibilityHeading(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        tonalElevation = 8.dp,
        shadowElevation = 6.dp
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = DarkOutline.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterSegmentButton(
                    text = "Alle",
                    accessibilityLabel = "Kartenfilter: Alle Standorte anzeigen",
                    icon = Icons.Default.Layers,
                    isSelected = selectedMode == MapLocationFilterMode.ALL,
                    onClick = { onModeSelected(MapLocationFilterMode.ALL) },
                    modifier = Modifier.weight(1f)
                )

                FilterSegmentButton(
                    text = "Öffentlich",
                    accessibilityLabel = "Kartenfilter: Nur öffentliche Clubs und Venues anzeigen",
                    icon = Icons.Default.Event,
                    isSelected = selectedMode == MapLocationFilterMode.PUBLIC_ONLY,
                    onClick = { onModeSelected(MapLocationFilterMode.PUBLIC_ONLY) },
                    modifier = Modifier.weight(1f)
                )

                FilterSegmentButton(
                    text = "Private",
                    accessibilityLabel = "Kartenfilter: Nur private Nutzerstandorte anzeigen",
                    icon = Icons.Default.People,
                    isSelected = selectedMode == MapLocationFilterMode.PRIVATE_ONLY,
                    onClick = { onModeSelected(MapLocationFilterMode.PRIVATE_ONLY) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FilterSegmentButton(
    text: String,
    accessibilityLabel: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) PurplePrimary else Color.Transparent,
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "SegmentBackgroundColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 200),
        label = "SegmentContentColor"
    )

    val borderModifier = if (isSelected) {
        Modifier.border(
            width = 1.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(PurplePrimaryLight, PurplePrimary)
            ),
            shape = RoundedCornerShape(20.dp)
        )
    } else Modifier

    Box(
        modifier = modifier
            .height(48.dp)
            .ensureMinTouchTarget()
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .then(borderModifier)
            .clickable { onClick() }
            .clearAndSetSemantics {
                contentDescription = accessibilityLabel
                stateDescription = if (isSelected) "Ausgewählt" else "Nicht ausgewählt"
                selected = isSelected
                role = Role.Tab
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}
