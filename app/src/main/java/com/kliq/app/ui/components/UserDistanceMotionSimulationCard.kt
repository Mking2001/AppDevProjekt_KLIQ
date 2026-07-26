package com.kliq.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kliq.app.domain.usecase.CalculateUserDistanceUseCase
import com.kliq.app.util.UserDistanceFormatter
import kotlinx.coroutines.delay

/**
 * Interactive Live Simulation Card component for verifying motion signal feeds
 * and real-time distance updates between two test users.
 */
@Composable
fun UserDistanceMotionSimulationCard(
    modifier: Modifier = Modifier,
    useCase: CalculateUserDistanceUseCase = remember { CalculateUserDistanceUseCase() },
    formatter: UserDistanceFormatter = remember { UserDistanceFormatter.default }
) {
    // Target user fixed position (Berlin Alexanderplatz)
    val targetLat = 52.521918
    val targetLng = 13.413215

    // Motion track coordinates (approaching target)
    val motionSteps = remember {
        listOf(
            Pair(52.5419, 13.4132), // ~2.2 km
            Pair(52.5350, 13.4132), // ~1.5 km
            Pair(52.5280, 13.4132), // ~670 m
            Pair(52.5235, 13.4132), // ~175 m
            Pair(52.5222, 13.4132), // ~30 m
            Pair(52.521918, 13.413215) // 0 m
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    var isSimulating by remember { mutableStateOf(false) }

    val currentCoords = motionSteps[currentStepIndex]
    val rawDistance = useCase.calculateDistanceMeters(
        startLat = currentCoords.first,
        startLng = currentCoords.second,
        endLat = targetLat,
        endLng = targetLng
    )
    val formattedDistance = formatter.formatDistance(rawDistance)
    val formattedBadge = formatter.formatDistanceBadge(rawDistance)

    LaunchedEffect(isSimulating) {
        if (isSimulating) {
            while (isSimulating) {
                delay(1200)
                if (currentStepIndex < motionSteps.size - 1) {
                    currentStepIndex++
                } else {
                    isSimulating = false
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Live-Distanz Simulator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        currentStepIndex = 0
                        isSimulating = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Zurücksetzen",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ziel-User: Alex (Alexanderplatz)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Entfernung: $formattedDistance",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = formattedBadge,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val progress = (currentStepIndex + 1).toFloat() / motionSteps.size
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (currentStepIndex >= motionSteps.size - 1) {
                        currentStepIndex = 0
                    }
                    isSimulating = !isSimulating
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSimulating) "Simulation Pausieren" else if (currentStepIndex >= motionSteps.size - 1) "Neustart" else "Bewegung Simulieren"
                )
            }
        }
    }
}

@Preview
@Composable
fun UserDistanceMotionSimulationCardPreview() {
    UserDistanceMotionSimulationCard()
}
