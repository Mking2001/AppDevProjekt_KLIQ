package com.kliq.app.ui.navigation

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
        /** Ordered list of all bottom bar tabs */
        val bottomBarItems: List<NavigationRoute>
            get() = listOf(Home, Explore, Map, Notifications, Profile)
    }
}

/**
 * Zusätzliche Navigationsrouten, die nicht in der Bottom Bar
 * angezeigt werden (Chat-Screens, Detail-Ansichten, etc.).
 */
object ChatRoutes {
    /** Route zur Chat-Listen-Übersicht */
    const val CHAT_LIST = "chat_list"

    /** Route zum Chat-Detail-Screen mit chatId-Parameter */
    const val CHAT_DETAIL = "chat_detail/{chatId}"

    /** Erzeugt die konkrete Route für einen bestimmten Chat */
    fun chatDetail(chatId: String): String = "chat_detail/$chatId"

    /** Argument-Name für die Chat-ID im NavGraph */
    const val ARG_CHAT_ID = "chatId"
}

/**
 * Kern-Routen für Onboarding und Splash.
 */
object CoreRoutes {
    const val SPLASH = "splash"
}

/**
 * Routen für Club-Analytics und Info-System.
 */
object ClubRoutes {
    const val CLUB_DETAIL = "club_detail/{clubId}"
    
    fun clubDetail(clubId: String): String = "club_detail/$clubId"
    
    const val ARG_CLUB_ID = "clubId"
}
