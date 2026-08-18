package com.kliq.app.ui.components.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Central Kliq High-Contrast Theme Color Tokens
val ColorKliqPurplePrimary = Color(0xFF6B46C1)
val ColorKliqPurpleLight = Color(0xFF805AD5)
val ColorKliqPurpleDarkBg = Color(0xFF2D1B4E)
val ColorKliqSurfaceDark = Color(0xFF1F1235)
val ColorKliqTextWhite = Color(0xFFFFFFFF)
val ColorKliqTextMuted = Color(0xFFCBD5E1)
val ColorKliqAccentPink = Color(0xFFEC4899)

/**
 * Reusable High-Contrast Primary CTA Button for Kliq Application.
 *
 * @param text Button label text.
 * @param onClick Callback triggered on click.
 * @param modifier Modifier for layout constraints.
 * @param enabled State of user interaction.
 * @param isLoading State of asynchronous operation showing spinner.
 */
@Composable
fun KliqPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ColorKliqPurplePrimary,
            contentColor = ColorKliqTextWhite,
            disabledContainerColor = ColorKliqPurplePrimary.copy(alpha = 0.4f),
            disabledContentColor = ColorKliqTextWhite.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = ColorKliqTextWhite,
                strokeWidth = 2.5.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ColorKliqTextWhite
            )
        }
    }
}

/**
 * Reusable High-Contrast Secondary Outlined Button for Kliq Application.
 */
@Composable
fun KliqSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, ColorKliqPurpleLight),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ColorKliqPurpleLight
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Reusable Glassmorphic Dark Container Card for Kliq UI screens.
 */
@Composable
fun KliqSurfaceCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = ColorKliqPurpleDarkBg,
    borderColor: Color = ColorKliqPurplePrimary.copy(alpha = 0.3f),
    cornerRadius: Dp = 20.dp,
    padding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content
        )
    }
}

/**
 * Reusable Category or Filter Header Chip for Map and Explore screens.
 */
@Composable
fun KliqHeaderChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) ColorKliqPurplePrimary else ColorKliqSurfaceDark
    val textColor = if (isSelected) ColorKliqTextWhite else ColorKliqTextMuted
    val borderColor = if (isSelected) ColorKliqPurpleLight else ColorKliqPurplePrimary.copy(alpha = 0.2f)

    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}
