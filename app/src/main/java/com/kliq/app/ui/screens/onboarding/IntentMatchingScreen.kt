package com.kliq.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.data.model.SearchIntent
import com.kliq.app.ui.theme.DarkBackground
import com.kliq.app.ui.theme.DarkOnBackground
import com.kliq.app.ui.theme.DarkOutline
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.DarkSurfaceVariant
import com.kliq.app.ui.theme.FuchsiaTertiary
import com.kliq.app.ui.theme.PurpleContainer
import com.kliq.app.ui.theme.PurplePrimary
import com.kliq.app.ui.theme.PurplePrimaryLight

/**
 * Onboarding screen for Intent-Matching preference selection.
 *
 * Implements high-contrast dark theme styling (Purple/Dark-Mode), interactive selection cards
 * for matching intentions ("Freunde", "Dating / Liebe", "Beides"), real-time state validation,
 * and seamless binding to the user profile repository.
 */
@Composable
fun IntentMatchingScreen(
    viewModel: IntentMatchingViewModel = hiltViewModel(),
    onIntentConfirmed: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onIntentConfirmed()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Step Indicator Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PurpleContainer.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PurplePrimaryLight.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "SCHRITT 2 VON 3 • INTENT MATCHING",
                        color = PurplePrimaryLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Screen Header
                Text(
                    text = "Was suchst du bei Kliq?",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = DarkOnBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Wähle deine bevorzugte Intentionsabsicht. So finden wir passende Matches und Events in deiner Umgebung.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkOnBackground.copy(alpha = 0.7f),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Selection Cards Stack
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IntentOptionCard(
                        intent = SearchIntent.FRIENDS,
                        icon = Icons.Default.Group,
                        isSelected = uiState.selectedIntent == SearchIntent.FRIENDS,
                        onSelect = { viewModel.selectIntent(SearchIntent.FRIENDS) }
                    )

                    IntentOptionCard(
                        intent = SearchIntent.DATING,
                        icon = Icons.Default.Favorite,
                        isSelected = uiState.selectedIntent == SearchIntent.DATING,
                        onSelect = { viewModel.selectIntent(SearchIntent.DATING) }
                    )

                    IntentOptionCard(
                        intent = SearchIntent.BOTH,
                        icon = Icons.Default.AutoAwesome,
                        isSelected = uiState.selectedIntent == SearchIntent.BOTH,
                        onSelect = { viewModel.selectIntent(SearchIntent.BOTH) }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Validation Status Info
                AnimatedVisibility(
                    visible = !uiState.isSelectionValid,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "* Bitte wähle eine Option aus, um fortzufahren.",
                        color = FuchsiaTertiary.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Action Button
                Button(
                    onClick = { viewModel.saveIntent() },
                    enabled = uiState.isSelectionValid && !uiState.isLoading,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurplePrimary,
                        contentColor = Color.White,
                        disabledContainerColor = DarkSurfaceVariant,
                        disabledContentColor = DarkOnBackground.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Auswahl bestätigen",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IntentOptionCard(
    intent: SearchIntent,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "cardScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) DarkSurfaceVariant else DarkSurface,
        animationSpec = tween(durationMillis = 200),
        label = "cardBgColor"
    )

    val iconContainerColor by animateColorAsState(
        targetValue = if (isSelected) PurplePrimary else DarkSurfaceVariant,
        animationSpec = tween(durationMillis = 200),
        label = "iconBgColor"
    )

    val borderModifier = if (isSelected) {
        Modifier.border(
            width = 2.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(PurplePrimary, FuchsiaTertiary)
            ),
            shape = RoundedCornerShape(20.dp)
        )
    } else {
        Modifier.border(
            width = 1.dp,
            color = DarkOutline.copy(alpha = 0.5f),
            shape = RoundedCornerShape(20.dp)
        )
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(borderModifier)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onSelect() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Icon Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = intent.title,
                    tint = if (isSelected) Color.White else DarkOnBackground.copy(alpha = 0.7f),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = intent.title,
                    color = DarkOnBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = intent.description,
                    color = DarkOnBackground.copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Selection Checkmark Badge
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Ausgewählt",
                    tint = FuchsiaTertiary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
