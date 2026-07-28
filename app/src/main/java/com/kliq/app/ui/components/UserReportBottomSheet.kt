package com.kliq.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kliq.app.ui.theme.DarkBackground
import com.kliq.app.ui.theme.DarkOnBackground
import com.kliq.app.ui.theme.DarkOnSurface
import com.kliq.app.ui.theme.DarkOnSurfaceVariant
import com.kliq.app.ui.theme.DarkOutline
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.ErrorRed
import com.kliq.app.ui.theme.PurpleContainer
import com.kliq.app.ui.theme.PurplePrimary

/**
 * Modal Bottom Sheet für den Nutzer-Meldeprozess im Kliq High-Contrast Lila/Dark-Theme.
 * Bietet vordefinierte Meldegründe sowie ein optionales Freitextfeld für Details.
 *
 * @param targetUsername Name des zu meldenden Nutzers.
 * @param isSubmitting Status der Absendung.
 * @param onDismiss Callback zum Schließen.
 * @param onSubmitReport Callback nach Wahl eines Grundes und Freitexts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserReportBottomSheet(
    targetUsername: String = "Nutzer",
    isSubmitting: Boolean = false,
    onDismiss: () -> Unit,
    onSubmitReport: (reason: String, details: String) -> Unit
) {
    val reportReasons = listOf(
        "Spam oder ungewollte Werbung",
        "Beleidigung / Harassment",
        "Unangebrachte Inhalte / Bilder",
        "Fake-Profil / Identitätsdiebstahl",
        "Sonstiges"
    )

    var selectedReason by remember { mutableStateOf(reportReasons.first()) }
    var additionalDetails by remember { mutableStateOf("") }
    val maxDetailsLength = 250
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$targetUsername melden",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = DarkOnBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Wähle bitte den Hauptgrund für deine Meldung. Das Kliq-Sicherheitsteam prüft den Fall diskret.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DarkOnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            reportReasons.forEach { reason ->
                val isSelected = selectedReason == reason
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedReason = reason }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = if (isSelected) PurplePrimary else Color.Transparent,
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = if (isSelected) PurplePrimary else DarkOutline,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.White, CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isSelected) DarkOnBackground else DarkOnSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = additionalDetails,
                onValueChange = {
                    if (it.length <= maxDetailsLength) {
                        additionalDetails = it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                placeholder = { Text("Zusätzliche Details (optional)...", color = DarkOnSurfaceVariant) },
                supportingText = {
                    Text(
                        text = "${additionalDetails.length} / $maxDetailsLength",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = DarkOnSurfaceVariant
                    )
                },
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = DarkOutline,
                    focusedTextColor = DarkOnBackground,
                    unfocusedTextColor = DarkOnSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Abbrechen", color = DarkOnSurfaceVariant)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = { onSubmitReport(selectedReason, additionalDetails) },
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed,
                        disabledContainerColor = ErrorRed.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Meldung absenden", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
