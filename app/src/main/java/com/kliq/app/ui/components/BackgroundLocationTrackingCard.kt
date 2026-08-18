package com.kliq.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.data.model.LocationTrackingMode
import com.kliq.app.viewmodel.LocationTrackingUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PurplePrimary = Color(0xFF7C4DFF)
private val PurpleAccent = Color(0xFFA855F7)
private val DarkBackground = Color(0xFF0F0B15)
private val DarkCardBg = Color(0xFF181224)
private val CardBorderColor = Color(0xFF2D2240)
private val ActiveGreen = Color(0xFF00E676)
private val WarningAmber = Color(0xFFFFAB00)
private val EcoCyan = Color(0xFF00E5FF)
private val HighAccuracyOrange = Color(0xFFFF6D00)

/**
 * High-Contrast Kliq Jetpack Compose Card UI component for managing background live location tracking
 * with stepped adaptive power modes (High-Accuracy, Balanced Ambient, Idle Passive).
 */
@Composable
fun BackgroundLocationTrackingCard(
    uiState: LocationTrackingUiState,
    onToggleTracking: () -> Unit,
    onOpenSettings: () -> Unit,
    onClearHistory: () -> Unit,
    onSelectMode: (LocationTrackingMode) -> Unit = {},
    onTriggerBurst: () -> Unit = {},
    onCancelBurst: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isGranted = uiState.backgroundPermissionState == LocationPermissionState.Granted

    val infiniteTransition = rememberInfiniteTransition(label = "LivePulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, CardBorderColor, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCardBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Icon, Title, Live Status Badge, Switch Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(PurplePrimary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location Tracking",
                            tint = PurplePrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Live-Hintergrund Tracking",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (uiState.isTrackingActive) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .alpha(pulseAlpha)
                                        .background(ActiveGreen, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "LIVE AKTIV",
                                    color = ActiveGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color.Gray, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "INAKTIV",
                                    color = Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Switch(
                    checked = uiState.isTrackingActive,
                    onCheckedChange = { onToggleTracking() },
                    enabled = isGranted,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PurplePrimary,
                        uncheckedThumbColor = Color.LightGray,
                        uncheckedTrackColor = DarkBackground
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stepped Tracking Mode Selector Segmented Controls
            Text(
                text = "Ortungsmodus & Batterie-Strategie",
                color = Color.LightGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ModeSegmentButton(
                    title = "Eco / Idle",
                    isSelected = uiState.trackingMode == LocationTrackingMode.IDLE_PASSIVE,
                    activeColor = EcoCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectMode(LocationTrackingMode.IDLE_PASSIVE) }
                )
                ModeSegmentButton(
                    title = "Balanced",
                    isSelected = uiState.trackingMode == LocationTrackingMode.BALANCED_AMBIENT && !uiState.isBurstActive,
                    activeColor = PurpleAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectMode(LocationTrackingMode.BALANCED_AMBIENT) }
                )
                ModeSegmentButton(
                    title = "High-Acc",
                    isSelected = uiState.trackingMode == LocationTrackingMode.HIGH_ACCURACY || uiState.isBurstActive,
                    activeColor = HighAccuracyOrange,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelectMode(LocationTrackingMode.HIGH_ACCURACY) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Burst Active Countdown Badge Banner
            AnimatedVisibility(visible = uiState.isBurstActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HighAccuracyOrange.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .border(1.dp, HighAccuracyOrange.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Burst Active",
                                tint = HighAccuracyOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "High-Accuracy Burst aktiv (${uiState.burstRemainingSeconds}s)",
                                color = HighAccuracyOrange,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Abbrechen",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(HighAccuracyOrange.copy(alpha = 0.4f))
                                .clickable { onCancelBurst() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (uiState.isBurstActive) {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Permission Warning Banner if ACCESS_BACKGROUND_LOCATION is restricted
            if (!isGranted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WarningAmber.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, WarningAmber.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Permission Warning",
                                tint = WarningAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hintergrund-Standortzugriff erforderlich",
                                color = WarningAmber,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Um den Live-Standort im Hintergrund zu teilen, wähle in den Einstellungen \"Immer zulassen\".",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onOpenSettings,
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "In Einstellungen freigeben", fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Location Fix Coordinates & Telemetry Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Aktuelle GPS-Koordinaten",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Power Optimization Badge
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val badgeColor = if (uiState.isStationary) EcoCyan else PurpleAccent
                            val badgeIcon = if (uiState.isStationary) Icons.Default.BatterySaver else Icons.AutoMirrored.Filled.DirectionsWalk
                            val badgeText = if (uiState.isStationary) "Stationär (Drosselung)" else "Adaptiv (>${uiState.minDisplacementMeters.toInt()}m)"

                            Icon(
                                imageVector = badgeIcon,
                                contentDescription = "Mode Badge",
                                tint = badgeColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = badgeText,
                                color = badgeColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val loc = uiState.currentLocation
                    if (loc != null) {
                        Text(
                            text = "Lat: ${String.format(Locale.US, "%.5f", loc.latitude)}, Lon: ${String.format(Locale.US, "%.5f", loc.longitude)}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val formattedTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(loc.timestampMs))
                        Text(
                            text = "Genauigkeit: ${String.format(Locale.US, "%.1f", loc.accuracy)}m  •  Intervall: ${uiState.samplingIntervalMs / 1000}s  •  $formattedTime",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    } else {
                        Text(
                            text = if (uiState.isTrackingActive) "Warte auf GPS-Signal..." else "Kein aktives Signal",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: GPS Boost trigger + Clear History
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onTriggerBurst,
                    enabled = isGranted && !uiState.isBurstActive,
                    colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "GPS Burst",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "GPS-Boost (30s)", fontSize = 11.sp)
                }

                if (uiState.totalSavedPoints > 0) {
                    OutlinedButton(
                        onClick = onClearHistory,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Verlauf leeren (${uiState.totalSavedPoints})", fontSize = 11.sp, color = PurpleAccent)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeSegmentButton(
    title: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) activeColor.copy(alpha = 0.25f) else Color.Transparent)
            .border(
                1.dp,
                if (isSelected) activeColor else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else Color.Gray,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
