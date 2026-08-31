package com.kliq.app.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kliq.app.BuildConfig
import com.kliq.app.viewmodel.ThemeMode

@Composable
fun KliqSettingsDialog(
    isVisible: Boolean,
    themeMode: ThemeMode,
    onToggleTheme: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onClearCache: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Einstellungen",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SettingsRow(
                    icon = Icons.Outlined.DarkMode,
                    title = "Darstellung",
                    subtitle = themeModeLabel(themeMode),
                    onClick = onToggleTheme
                )
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                SettingsRow(
                    icon = Icons.Outlined.Notifications,
                    title = "Benachrichtigungen",
                    subtitle = "System-Einstellungen für Kliq öffnen",
                    onClick = onOpenNotificationSettings
                )
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                SettingsRow(
                    icon = Icons.Outlined.CleaningServices,
                    title = "Cache leeren",
                    subtitle = "Zwischengespeicherte Bilder und Aufnahmen entfernen",
                    onClick = onClearCache
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Schließen", fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

private fun themeModeLabel(themeMode: ThemeMode): String = when (themeMode) {
    ThemeMode.DARK -> "Dunkel"
    ThemeMode.LIGHT -> "Hell"
    ThemeMode.SYSTEM -> "Systemvorgabe"
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun KliqAboutDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Über Kliq",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Kliq ist eine Nightlife-App für Klagenfurt am Wörthersee. " +
                            "Sie bündelt Karte, Stadt-Chat, Bewertungen und Club-Informationen " +
                            "in einer Anwendung.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                AboutRow(label = "Version", value = BuildConfig.VERSION_NAME)
                AboutRow(label = "Build", value = BuildConfig.VERSION_CODE.toString())
                AboutRow(label = "Paket", value = BuildConfig.APPLICATION_ID)
                AboutRow(label = "Architektur", value = "MVVM mit Hilt und Room")
                AboutRow(label = "Region", value = "Klagenfurt, Kärnten")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Schließen", fontWeight = FontWeight.SemiBold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun openAppNotificationSettings(context: Context) {
    val notificationIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }

    runCatching { context.startActivity(notificationIntent) }.onFailure {
        val detailsIntent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        runCatching { context.startActivity(detailsIntent) }
    }
}

fun clearApplicationCache(context: Context): Int {
    val entries = context.cacheDir?.listFiles() ?: return 0
    var removed = 0
    entries.forEach { entry ->
        if (entry.deleteRecursively()) removed++
    }
    return removed
}
