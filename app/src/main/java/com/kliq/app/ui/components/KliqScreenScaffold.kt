package com.kliq.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.kliq.app.ui.navigation.KliqTopBar
import com.kliq.app.ui.navigation.TopBarMenuAction

@Composable
fun KliqScreenScaffold(
    title: String,
    isMenuExpanded: Boolean,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMenuAction: (TopBarMenuAction) -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable () -> Unit = {},
    showTopBar: Boolean = true,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {

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
                KliqTopBar(
                    title = title,
                    isMenuExpanded = isMenuExpanded,
                    onToggleMenu = onToggleMenu,
                    onDismissMenu = onDismissMenu,
                    onMenuAction = onMenuAction,
                    navigationIcon = navigationIcon,
                    actions = actions
                )
            }
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}
