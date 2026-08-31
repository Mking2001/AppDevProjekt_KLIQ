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
import com.kliq.app.ui.theme.DarkOnSurface
import com.kliq.app.ui.theme.DarkOnSurfaceVariant
import com.kliq.app.ui.theme.DarkSurface
import com.kliq.app.ui.theme.ErrorRed

@Composable
fun BlockConfirmationDialog(
    username: String = "diesen Nutzer",
    onDismiss: () -> Unit,
    onConfirmBlock: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = "$username blockieren?",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = DarkOnBackground,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column {
                Text(
                    text = "Möchtest du $username wirklich blockieren?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DarkOnBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Blockierte Nutzer können dir keine Nachrichten mehr senden.\n" +
                            "• Ihr werdet gegenseitig auf der Karte, im Social Feed und in Chatlisten ausgeblendet.\n" +
                            "• Diese Aktion kann im Profil jederzeit rückgängig gemacht werden.",
                    style = MaterialTheme.typography.bodySmall.copy(color = DarkOnSurfaceVariant)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmBlock,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
            ) {
                Text("Nutzer blockieren", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen", color = DarkOnSurfaceVariant)
            }
        }
    )
}
