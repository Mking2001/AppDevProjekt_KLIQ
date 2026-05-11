package com.kliq.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class definiert die verfügbaren Aktionen im globalen
 * Overflow-Menü der Top-App-Bar. Jede Aktion kapselt ihr Label
 * und Icon für konsistente Darstellung über alle Screens hinweg.
 *
 * @param label Anzeigename der Menü-Aktion.
 * @param icon Icon-Vektor für die Menü-Zeile.
 */
sealed class TopBarMenuAction(
    val label: String,
    val icon: ImageVector
) {
    data object Settings : TopBarMenuAction(
        label = "Einstellungen",
        icon = Icons.Outlined.Settings
    )

    data object EditProfile : TopBarMenuAction(
        label = "Profil bearbeiten",
        icon = Icons.Outlined.Edit
    )

    data object ToggleTheme : TopBarMenuAction(
        label = "Darstellung",
        icon = Icons.Outlined.DarkMode
    )

    data object About : TopBarMenuAction(
        label = "Über Kliq",
        icon = Icons.Outlined.Info
    )

    data object Logout : TopBarMenuAction(
        label = "Abmelden",
        icon = Icons.Outlined.Logout
    )

    companion object {
        /** Geordnete Liste aller Menü-Einträge */
        val allActions: List<TopBarMenuAction> = listOf(
            Settings, EditProfile, ToggleTheme, About, Logout
        )
    }
}
