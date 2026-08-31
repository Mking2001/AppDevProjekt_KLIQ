package com.kliq.app.ui.components

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReviewCommentInputCard(
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    selectedRating: Int,
    onRatingSelected: (Int) -> Unit,
    isVerificationLocked: Boolean,
    maxCommentLength: Int = 280,
    isSubmitting: Boolean = false,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = Color(0xFF1E1B2E)
    val accentPurple = Color(0xFF7C3AED)
    val starGold = Color(0xFFFFC107)
    val remainingChars = maxCommentLength - commentText.length
    val isSubmitEnabled = !isVerificationLocked && commentText.trim().isNotEmpty() && remainingChars >= 0 && !isSubmitting

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(1.dp, accentPurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Bewertung & Kommentar abgeben",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isVerificationLocked) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFDC2626).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Sperre",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Sicherheits-Sperre: Kommentare nur bei physischer Nähe (GPS) oder QR-Scan freigeschaltet!",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFFCA5A5)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sterne-Bewertung:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )

                Row {
                    for (star in 1..5) {
                        val isSelected = star <= selectedRating
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = "Stern $star",
                            tint = if (isSelected) starGold else Color.Gray,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable(enabled = !isVerificationLocked) { onRatingSelected(star) }
                                .padding(2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = commentText,
                onValueChange = { onCommentTextChange(it) },
                enabled = !isVerificationLocked,
                placeholder = {
                    Text(
                        text = if (isVerificationLocked) "Eingabe durch Logik-Sperre blockiert..." else "Schreibe deinen verifizierten Erfahrungstext...",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = accentPurple,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedContainerColor = Color(0xFF141221),
                    unfocusedContainerColor = Color(0xFF141221),
                    disabledContainerColor = Color(0xFF141221).copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$remainingChars / $maxCommentLength Zeichen übrig",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (remainingChars < 20) Color(0xFFEF4444) else Color.Gray
                )

                Button(
                    onClick = onSubmitClick,
                    enabled = isSubmitEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentPurple,
                        disabledContainerColor = accentPurple.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Absenden",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSubmitting) "Wird gesendet..." else "Veröffentlichen",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
