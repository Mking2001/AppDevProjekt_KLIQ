package com.kliq.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

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

    data object DeleteAccount : TopBarMenuAction(
        label = "Profil löschen",
        icon = Icons.Outlined.DeleteForever
    )

    companion object {

        val allActions: List<TopBarMenuAction> = listOf(
            Settings, EditProfile, ToggleTheme, About, Logout, DeleteAccount
        )
    }
}
