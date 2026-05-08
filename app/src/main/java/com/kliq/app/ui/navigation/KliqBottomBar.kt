package com.kliq.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight

/**
 * Custom-styled Bottom Navigation Bar for the Kliq app.
 * Applies the Lila/Dark-Mode High-Contrast theme with animated
 * selection indicators, icon scaling, and notification badge support.
 *
 * @param currentRoute The currently selected navigation route string.
 * @param notificationBadgeCount Badge count for the Notifications tab.
 * @param onTabSelected Callback invoked when a tab is tapped.
 * @param modifier Optional [Modifier] for this composable.
 */
@Composable
fun KliqBottomBar(
    currentRoute: String,
    notificationBadgeCount: Int,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                ambientColor = PurplePrimary.copy(alpha = 0.3f),
                spotColor = PurplePrimary.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column {
            // Top accent gradient line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                PurplePrimary.copy(alpha = 0.0f),
                                PurplePrimaryLight,
                                PurplePrimary,
                                PurplePrimaryLight,
                                PurplePrimary.copy(alpha = 0.0f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .selectableGroup(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationRoute.bottomBarItems.forEach { navItem ->
                    val isSelected = currentRoute == navItem.route
                    val showBadge = navItem is NavigationRoute.Notifications
                            && notificationBadgeCount > 0

                    KliqBottomBarItem(
                        navItem = navItem,
                        isSelected = isSelected,
                        badgeCount = if (showBadge) notificationBadgeCount else 0,
                        onClick = { onTabSelected(navItem.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Individual item in the Kliq Bottom Bar.
 * Features spring-animated scaling, color transitions, and an
 * active-state indicator pill beneath the icon.
 */
@Composable
private fun KliqBottomBarItem(
    navItem: NavigationRoute,
    isSelected: Boolean,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "iconColor"
    )

    val labelColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        },
        label = "labelColor"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.Tab
            )
            .padding(vertical = 6.dp)
            .semantics {
                contentDescription = navItem.label
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Active indicator pill
        Box(
            modifier = Modifier
                .size(
                    width = if (isSelected) 48.dp else 0.dp,
                    height = 3.dp
                )
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else Color.Transparent
                )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Icon with optional badge
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = if (isSelected) navItem.selectedIcon else navItem.unselectedIcon,
                contentDescription = navItem.label,
                tint = iconColor,
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Label
        Text(
            text = navItem.label,
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}
