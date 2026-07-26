package com.kliq.app.ui.screens.rating

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.viewmodel.RatingViewModel

@Composable
fun RatingLockScreen(
    viewModel: RatingViewModel,
    reviewerUserId: String,
    targetUserId: String,
    onNavigateBack: () -> Unit = {}
) {
    LaunchedEffect(reviewerUserId, targetUserId) {
        viewModel.initTargetUser(reviewerUserId, targetUserId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nutzer bewerten",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            VerificationLockCard(
                isLocked = uiState.isRatingLocked,
                verificationMethod = uiState.verificationMethod,
                verificationDetails = uiState.verificationDetails,
                onScanQrClick = { viewModel.onQrCodeScanned("KLIQ_PASS_$targetUserId") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Deine Bewertung",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    StarRatingRow(
                        rating = uiState.rating,
                        isLocked = uiState.isRatingLocked,
                        onRatingSelected = { viewModel.onRatingChanged(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.text,
                        onValueChange = { viewModel.onCommentChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .alpha(if (uiState.isRatingLocked) 0.5f else 1.0f),
                        enabled = !uiState.isRatingLocked,
                        placeholder = { Text("Schreibe einen Kommentar...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledBorderColor = Color.Gray,
                            disabledPlaceholderColor = Color.Gray
                        )
                    )

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.submitRating() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !uiState.isRatingLocked && uiState.rating > 0 && !uiState.isSubmitting,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = if (uiState.isRatingLocked) "Bewertung gesperrt" else "Bewertung abgeben",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    if (uiState.submitSuccess) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Vielen Dank! Deine Bewertung wurde erfolgreich verifiziert und gespeichert.",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VerificationLockCard(
    isLocked: Boolean,
    verificationMethod: ReviewVerificationMethod,
    verificationDetails: String,
    onScanQrClick: () -> Unit
) {
    val backgroundColor = if (isLocked) Color(0xFF331414) else Color(0xFF14331D)
    val borderColor = if (isLocked) Color(0xFFE53935) else Color(0xFF4CAF50)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.CheckCircle,
                    contentDescription = if (isLocked) "Sperre" else "Verifiziert",
                    tint = borderColor,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isLocked) "Anti-Spam Sperre aktiv" else "Bewertung freigeschaltet",
                    fontWeight = FontWeight.Bold,
                    color = borderColor,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = verificationDetails,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            if (isLocked) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onScanQrClick,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "QR Scannen"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("QR-Code scannen (Einlass-Pass)")
                }
            }
        }
    }
}

@Composable
fun StarRatingRow(
    rating: Int,
    isLocked: Boolean,
    onRatingSelected: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (star in 1..5) {
            val isSelected = star <= rating
            val icon = if (isSelected) Icons.Default.Star else Icons.Outlined.Star
            val tint = if (isLocked) {
                Color.Gray
            } else if (isSelected) {
                Color(0xFFFFC107)
            } else {
                Color.LightGray
            }

            Icon(
                imageVector = icon,
                contentDescription = "Stern $star",
                tint = tint,
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
                    .clickable(enabled = !isLocked) {
                        onRatingSelected(star)
                    }
            )
        }
    }
}
