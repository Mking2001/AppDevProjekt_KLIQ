package com.kliq.app.ui.screens.catalog

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kliq.app.ui.components.KliqIcon
import com.kliq.app.ui.components.KliqIconButton
import com.kliq.app.ui.components.KliqIconCategory
import com.kliq.app.ui.components.KliqIconSize
import com.kliq.app.ui.theme.KliqTheme
import com.kliq.app.ui.theme.PoppinsFontFamily
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight
import com.kliq.app.ui.theme.bodyRegular
import com.kliq.app.ui.theme.button
import com.kliq.app.ui.theme.caption
import com.kliq.app.ui.theme.heading1
import com.kliq.app.ui.theme.heading2
import com.kliq.app.ui.theme.heading3
import com.kliq.app.viewmodel.ThemeMode

/**
 * Catalog Screen for testing and inspecting Custom Typography (Poppins)
 * and the unified Icon Styling System in different theme modes and states.
 */
@Composable
fun TypographyAndIconCatalogScreen(
    modifier: Modifier = Modifier
) {
    var selectedThemeMode by remember { mutableStateOf(ThemeMode.DARK) }
    var isHighContrast by remember { mutableStateOf(false) }

    KliqTheme(
        themeMode = selectedThemeMode,
        isHighContrast = isHighContrast
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Text(
                        text = "Design Catalog (Kapitel 8.6)",
                        style = MaterialTheme.typography.heading1,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("catalog_title")
                    )
                    Text(
                        text = "Custom Fonts (Poppins) & Icon Styling System",
                        style = MaterialTheme.typography.bodyRegular,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Theme Switcher Section
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Theme & Kontrast Modus",
                                style = MaterialTheme.typography.heading3,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = selectedThemeMode == ThemeMode.DARK && !isHighContrast,
                                    onClick = {
                                        selectedThemeMode = ThemeMode.DARK
                                        isHighContrast = false
                                    },
                                    label = { Text("Dark Mode") },
                                    modifier = Modifier.testTag("chip_dark_mode")
                                )
                                FilterChip(
                                    selected = selectedThemeMode == ThemeMode.LIGHT && !isHighContrast,
                                    onClick = {
                                        selectedThemeMode = ThemeMode.LIGHT
                                        isHighContrast = false
                                    },
                                    label = { Text("Light Mode") },
                                    modifier = Modifier.testTag("chip_light_mode")
                                )
                                FilterChip(
                                    selected = isHighContrast,
                                    onClick = {
                                        isHighContrast = true
                                    },
                                    label = { Text("High-Contrast") },
                                    modifier = Modifier.testTag("chip_high_contrast")
                                )
                            }
                        }
                    }
                }

                // Typography Section
                item {
                    TypographyCatalogSection()
                }

                // Icon Matrix Section
                item {
                    IconMatrixCatalogSection()
                }
            }
        }
    }
}

@Composable
private fun TypographyCatalogSection() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.testTag("typography_section")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "1. Typografie Hierarchy (Poppins)",
                style = MaterialTheme.typography.heading2,
                color = MaterialTheme.colorScheme.primary
            )

            TypographyRow(
                label = "Display Large",
                sample = "Kliq Events 57pt",
                style = MaterialTheme.typography.displayLarge,
                details = "Bold • 57sp • LH 64sp"
            )
            TypographyRow(
                label = "Headline Large (H1)",
                sample = "Vibe & Nightlife H1",
                style = MaterialTheme.typography.heading1,
                details = "Bold • 32sp • LH 40sp"
            )
            TypographyRow(
                label = "Headline Medium (H2)",
                sample = "Angesagte Clubs H2",
                style = MaterialTheme.typography.heading2,
                details = "Bold • 28sp • LH 36sp"
            )
            TypographyRow(
                label = "Headline Small (H3)",
                sample = "Quick Actions & Filters H3",
                style = MaterialTheme.typography.heading3,
                details = "SemiBold • 24sp • LH 32sp"
            )
            TypographyRow(
                label = "Body Medium (BodyRegular)",
                sample = "Finde die besten Partys und vergleiche Live-Auslastungen in deiner Nähe.",
                style = MaterialTheme.typography.bodyRegular,
                details = "Normal • 14sp • LH 20sp"
            )
            TypographyRow(
                label = "Label Large (Button)",
                sample = "JETZT NAVIGATION STARTEN",
                style = MaterialTheme.typography.button,
                details = "Medium • 14sp • LS 0.1sp"
            )
            TypographyRow(
                label = "Body Small (Caption)",
                sample = "Vor 5 Minuten aktualisiert • 4.8 Rating",
                style = MaterialTheme.typography.caption,
                details = "Normal • 12sp • LH 16sp"
            )
        }
    }
}

@Composable
private fun TypographyRow(
    label: String,
    sample: String,
    style: TextStyle,
    details: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = sample,
            style = style,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = details,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun IconMatrixCatalogSection() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.testTag("icon_matrix_section")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "2. Icon Matrix & Dynamic Tinting",
                style = MaterialTheme.typography.heading2,
                color = MaterialTheme.colorScheme.primary
            )

            // Icon Sizes Row
            Text(
                text = "Icon Größen (Small: 16dp, Medium: 24dp, Large: 32dp, Display: 48dp)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                KliqIcon(imageVector = Icons.Default.Favorite, contentDescription = "Small Favorite Icon", size = KliqIconSize.SMALL, category = KliqIconCategory.ACTION)
                KliqIcon(imageVector = Icons.Default.Favorite, contentDescription = "Medium Favorite Icon", size = KliqIconSize.MEDIUM, category = KliqIconCategory.ACTION)
                KliqIcon(imageVector = Icons.Default.Favorite, contentDescription = "Large Favorite Icon", size = KliqIconSize.LARGE, category = KliqIconCategory.ACTION)
                KliqIcon(imageVector = Icons.Default.Favorite, contentDescription = "Display Favorite Icon", size = KliqIconSize.DISPLAY, category = KliqIconCategory.ACTION)
            }

            // Category Matrix: Active, Inactive, Disabled, Highlighted
            Text(
                text = "Zustands-Matrix (Active, Inactive, Disabled, Highlighted)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconStateRow(title = "Navigation Icons", vector = Icons.Default.Map, category = KliqIconCategory.NAVIGATION)
            IconStateRow(title = "Action Icons", vector = Icons.Default.Notifications, category = KliqIconCategory.ACTION)
            IconStateRow(title = "Event Marker Icons", vector = Icons.Default.LocationOn, category = KliqIconCategory.EVENT_MARKER)
            IconStateRow(title = "Standard Icons", vector = Icons.Default.Person, category = KliqIconCategory.STANDARD)
        }
    }
}

@Composable
private fun IconStateRow(
    title: String,
    vector: ImageVector,
    category: KliqIconCategory
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Active
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                KliqIcon(imageVector = vector, contentDescription = "$title Active", isSelected = true, category = category)
                Text(text = "Active", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Inactive
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                KliqIcon(imageVector = vector, contentDescription = "$title Inactive", isSelected = false, category = category)
                Text(text = "Inactive", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Disabled
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.alpha(0.38f)) {
                    KliqIcon(imageVector = vector, contentDescription = "$title Disabled", isSelected = false, category = category)
                }
                Text(text = "Disabled", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Highlighted Neon Lila
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                KliqIcon(imageVector = vector, contentDescription = "$title Neon Highlight", tint = PurplePrimaryLight, category = category)
                Text(text = "Neon Lila", style = MaterialTheme.typography.labelSmall, color = PurplePrimaryLight)
            }
        }
    }
}

// Preview Suite
@Preview(name = "Dark Mode Preview", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun TypographyAndIconCatalogDarkPreview() {
    TypographyAndIconCatalogScreen()
}

@Preview(name = "Light Mode Preview", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Composable
fun TypographyAndIconCatalogLightPreview() {
    TypographyAndIconCatalogScreen()
}
