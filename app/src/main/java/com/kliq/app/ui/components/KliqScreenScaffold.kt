/**
 * AI-GENERATED CODE
 * Dieses wiederverwendbare Screen-Scaffold wurde vollständig durch KI generiert.
 * Erstellt im Rahmen von Schritt 4: Layout-Scaffolding der Haupt-Screens.
 * Datum: 2026-05-11
 */
package com.kliq.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight

/**
 * AI-generiert: Wiederverwendbares Screen-Scaffold für alle Haupt-Screens.
 * Stellt eine konsistente Top-App-Bar, Fade-in-Animation und
 * den Lila/Dark-Mode Background bereit.
 *
 * @param title Titel für die Top-App-Bar.
 * @param actions Optionale Action-Icons rechts in der Top-Bar.
 * @param navigationIcon Optionales Navigations-Icon links in der Top-Bar.
 * @param showTopBar Ob die Top-App-Bar angezeigt werden soll.
 * @param floatingActionButton Optionaler FAB.
 * @param content Screen-Inhalt mit innerPadding.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KliqScreenScaffold(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    showTopBar: Boolean = true,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    // AI-generiert: Fade-in Animation beim Screen-Eintritt
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "screenFadeIn"
    )
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .alpha(alpha),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (showTopBar) {
                // AI-generiert: Top-App-Bar mit Gradient-Unterlinie im Lila-Design
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    actions = actions,
                    navigationIcon = navigationIcon,
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
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}
