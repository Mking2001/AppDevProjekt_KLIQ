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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.SmokingRooms
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
import com.kliq.app.data.model.DrinkingHabit
import com.kliq.app.data.model.SmokingHabit
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
 * Onboarding screen for collecting user consumption habits (smoking & drinking).
 *
 * Implements high-contrast dark theme styling, accessible toggle cards, state validation,
 * and clean MVVM architecture bound to [ConsumptionHabitsViewModel].
 */
@Composable
fun ConsumptionHabitsScreen(
    viewModel: ConsumptionHabitsViewModel = hiltViewModel(),
    onHabitsConfirmed: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onHabitsConfirmed()
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
                        text = "SCHRITT 3 VON 3 • KONSUM-GEWOHNHEITEN",
                        color = PurplePrimaryLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Header Title & Subtitle
                Text(
                    text = "Rauchen & Trinken",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = DarkOnBackground,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Teile deine Gewohnheiten mit, um passende Party-Buddies und passende Event-Empfehlungen zu erhalten.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkOnBackground.copy(alpha = 0.7f),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Smoking Habits Category
                Text(
                    text = "Rauchverhalten",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = DarkOnBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SmokingHabit.entries.forEach { habit ->
                        HabitChipCard(
                            title = habit.title,
                            description = habit.description,
                            icon = Icons.Default.SmokingRooms,
                            isSelected = uiState.selectedSmokingHabit == habit,
                            onSelect = { viewModel.selectSmokingHabit(habit) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Drinking Habits Category
                Text(
                    text = "Trinkverhalten",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = DarkOnBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DrinkingHabit.entries.forEach { habit ->
                        HabitChipCard(
                            title = habit.title,
                            description = habit.description,
                            icon = Icons.Default.LocalBar,
                            isSelected = uiState.selectedDrinkingHabit == habit,
                            onSelect = { viewModel.selectDrinkingHabit(habit) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Validation Status Info
                AnimatedVisibility(
                    visible = !uiState.isSelectionValid,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "* Bitte wähle für beide Kategorien eine Option aus.",
                        color = FuchsiaTertiary.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Action Button
                Button(
                    onClick = { viewModel.saveConsumptionHabits() },
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
                                text = "Auswahl speichern & Weiter",
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
private fun HabitChipCard(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.01f else 1.0f,
        animationSpec = tween(durationMillis = 180),
        label = "chipScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) DarkSurfaceVariant else DarkSurface,
        animationSpec = tween(durationMillis = 180),
        label = "chipBgColor"
    )

    val iconContainerColor by animateColorAsState(
        targetValue = if (isSelected) PurplePrimary else DarkSurfaceVariant,
        animationSpec = tween(durationMillis = 180),
        label = "chipIconBgColor"
    )

    val borderModifier = if (isSelected) {
        Modifier.border(
            width = 2.dp,
            brush = Brush.horizontalGradient(
                colors = listOf(PurplePrimary, FuchsiaTertiary)
            ),
            shape = RoundedCornerShape(16.dp)
        )
    } else {
        Modifier.border(
            width = 1.dp,
            color = DarkOutline.copy(alpha = 0.4f),
            shape = RoundedCornerShape(16.dp)
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(borderModifier)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onSelect() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconContainerColor)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) Color.White else DarkOnBackground.copy(alpha = 0.7f),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = DarkOnBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = description,
                    color = DarkOnBackground.copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Ausgewählt",
                    tint = FuchsiaTertiary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
