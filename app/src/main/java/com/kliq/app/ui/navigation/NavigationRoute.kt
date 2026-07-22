package com.kliq.app.ui.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class defining all top-level navigation routes for the Kliq Bottom Bar.
 * Each route encapsulates its route string, display label, and icon variants
 * (filled for selected state, outlined for unselected state).
 */
sealed class NavigationRoute(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : NavigationRoute(
        route = "home",
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Explore : NavigationRoute(
        route = "explore",
        label = "Entdecken",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore
    )

    data object Map : NavigationRoute(
        route = "map",
        label = "Karte",
        selectedIcon = Icons.Filled.Map,
        unselectedIcon = Icons.Outlined.Map
    )

    data object Notifications : NavigationRoute(
        route = "notifications",
        label = "Aktivität",
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    )

    data object Profile : NavigationRoute(
        route = "profile",
        label = "Profil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )

    companion object {
        /** Route-Template für den SMS-Verifizierungs-Screen. Erwartet phoneNumber als Argument. */
        const val VERIFICATION_ROUTE = "verification/{phoneNumber}"

        /** Erzeugt die Navigations-Route zur Verifizierung mit URL-kodierter Telefonnummer. */
        fun verificationRoute(phoneNumber: String): String {
            return "verification/${Uri.encode(phoneNumber)}"
        }

        /** Ordered list of all bottom bar tabs */
        val bottomBarItems: List<NavigationRoute> = listOf(
            Home, Explore, Map, Notifications, Profile
        )
    }
}

