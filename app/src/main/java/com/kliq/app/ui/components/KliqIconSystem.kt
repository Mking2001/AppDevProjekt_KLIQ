package com.kliq.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standard icon sizes in the Kliq design system.
 */
enum class KliqIconSize(val dp: Dp) {
    SMALL(16.dp),
    MEDIUM(24.dp),
    LARGE(32.dp),
    DISPLAY(48.dp)
}

/**
 * Categories of icons in Kliq UI for systematic color tinting.
 */
enum class KliqIconCategory {
    ACTION,
    NAVIGATION,
    EVENT_MARKER,
    STANDARD
}

/**
 * Calculates the dynamic icon tint color according to Kliq theme state.
 */
@Composable
fun kliqIconTint(
    category: KliqIconCategory,
    isSelected: Boolean = false,
    customTint: Color? = null
): Color {
    if (customTint != null) return customTint

    val colorScheme = MaterialTheme.colorScheme
    return when (category) {
        KliqIconCategory.ACTION -> {
            colorScheme.primary
        }
        KliqIconCategory.NAVIGATION -> {
            if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant
        }
        KliqIconCategory.EVENT_MARKER -> {
            if (isSelected) colorScheme.tertiary else colorScheme.secondary
        }
        KliqIconCategory.STANDARD -> {
            LocalContentColor.current
        }
    }
}

/**
 * Centralized Icon composable wrapper for ImageVector assets.
 */
@Composable
fun KliqIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: KliqIconSize = KliqIconSize.MEDIUM,
    category: KliqIconCategory = KliqIconCategory.STANDARD,
    isSelected: Boolean = false,
    tint: Color? = null
) {
    val resolvedTint = kliqIconTint(category = category, isSelected = isSelected, customTint = tint)
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = resolvedTint,
        modifier = modifier.size(size.dp)
    )
}

/**
 * Centralized Icon composable wrapper for Painter assets.
 */
@Composable
fun KliqIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: KliqIconSize = KliqIconSize.MEDIUM,
    category: KliqIconCategory = KliqIconCategory.STANDARD,
    isSelected: Boolean = false,
    tint: Color? = null
) {
    val resolvedTint = kliqIconTint(category = category, isSelected = isSelected, customTint = tint)
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = resolvedTint,
        modifier = modifier.size(size.dp)
    )
}

/**
 * Reusable Icon Button wrapper supporting Kliq sizing and consistent padding rules.
 */
@Composable
fun KliqIconButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: KliqIconSize = KliqIconSize.MEDIUM,
    category: KliqIconCategory = KliqIconCategory.ACTION,
    isSelected: Boolean = false,
    tint: Color? = null,
    enabled: Boolean = true,
    padding: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        KliqIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            size = size,
            category = category,
            isSelected = isSelected,
            tint = tint
        )
    }
}
