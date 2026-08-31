package com.kliq.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kliq.app.ui.theme.DarkOnBackground
import com.kliq.app.ui.theme.DarkOnSurfaceVariant
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.ErrorRed

@Composable
fun DeleteChatConfirmationDialog(
    chatTitle: String = "diesen Chat",
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "Chat löschen?",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = DarkOnBackground,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column {
                Text(
                    text = "Möchtest du den Chat mit „$chatTitle“ wirklich löschen?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkOnBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Alle gesendeten und empfangenen Nachrichten in diesem Chat werden dauerhaft von deinem Gerät entfernt.\n" +
                            "• Dieser Vorgang kann nicht rückgängig gemacht werden.",
                    style = MaterialTheme.typography.bodySmall.copy(color = DarkOnSurfaceVariant)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
            ) {
                Text("Chat löschen", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = DarkOnSurfaceVariant)
            }
        }
    )
}
