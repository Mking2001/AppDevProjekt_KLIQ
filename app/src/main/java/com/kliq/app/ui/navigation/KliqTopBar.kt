package com.kliq.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight

/**
 * Globale Top-App-Bar-Komponente der Kliq-App.
 * Zeigt den Screen-Titel zentriert, optionale Screen-spezifische
 * Action-Icons links und ein Overflow-Menü rechts.
 *
 * Die Bar verwendet das Lila/Dark-Mode High-Contrast-Design
 * mit einer Gradient-Akzentlinie am unteren Rand.
 *
 * @param title Angezeigter Titel in der Mitte der Bar.
 * @param isMenuExpanded Ob das Dropdown-Menü aktuell geöffnet ist.
 * @param onToggleMenu Callback zum Umschalten des Menü-Zustands.
 * @param onDismissMenu Callback zum Schließen des Menüs.
 * @param onMenuAction Callback wenn ein Menü-Eintrag ausgewählt wird.
 * @param actions Optionale Screen-spezifische Action-Icons (linke Seite).
 * @param modifier Optionaler [Modifier] für die Komponente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KliqTopBar(
    title: String,
    isMenuExpanded: Boolean,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMenuAction: (TopBarMenuAction) -> Unit,
    actions: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            actions = {
                actions()
                OverflowMenuButton(
                    isExpanded = isMenuExpanded,
                    onToggle = onToggleMenu,
                    onDismiss = onDismissMenu,
                    onAction = onMenuAction
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Gradient-Akzentlinie unter der Top-Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            PurplePrimary.copy(alpha = 0.0f),
                            PurplePrimaryLight.copy(alpha = 0.5f),
                            PurplePrimary.copy(alpha = 0.5f),
                            PurplePrimaryLight.copy(alpha = 0.5f),
                            PurplePrimary.copy(alpha = 0.0f)
                        )
                    )
                )
        )
    }
}

/**
 * Overflow-Menü-Button mit animierter Drehung und einem
 * Dropdown-Menü mit allen globalen Aktionen.
 *
 * @param isExpanded Ob das Dropdown aktuell sichtbar ist.
 * @param onToggle Callback zum Umschalten.
 * @param onDismiss Callback bei Außenklick.
 * @param onAction Callback bei Auswahl eines Menü-Eintrags.
 */
@Composable
private fun OverflowMenuButton(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onDismiss: () -> Unit,
    onAction: (TopBarMenuAction) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "menuIconRotation"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isExpanded) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        label = "menuIconTint"
    )

    Box {
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Menü öffnen",
                tint = iconTint,
                modifier = Modifier.rotate(rotation)
            )
        }

        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(
                extraSmall = RoundedCornerShape(16.dp)
            )
        ) {
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = onDismiss,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                TopBarMenuAction.allActions.forEachIndexed { index, action ->
                    val isLogout = action is TopBarMenuAction.Logout

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = action.label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (isLogout) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        },
                        onClick = {
                            onDismiss()
                            onAction(action)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.label,
                                tint = if (isLogout) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}
