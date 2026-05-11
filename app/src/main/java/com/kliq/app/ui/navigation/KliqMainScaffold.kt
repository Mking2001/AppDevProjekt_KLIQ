package com.kliq.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kliq.app.ui.screens.explore.ExploreScreen
import com.kliq.app.ui.screens.home.HomeScreen
import com.kliq.app.ui.screens.map.MapScreen
import com.kliq.app.ui.screens.notifications.NotificationsScreen
import com.kliq.app.ui.screens.profile.ProfileScreen

/**
 * Main scaffold composable that hosts the Bottom Navigation Bar
 * and the [NavHost] for all 5 primary screens.
 *
 * Follows MVVM: The [NavigationViewModel] owns the navigation state,
 * the [TopBarViewModel] owns the top bar state, and this composable
 * purely observes and renders based on those states.
 *
 * @param navigationViewModel Hilt-injected ViewModel for navigation state.
 * @param topBarViewModel Hilt-injected ViewModel for top bar state.
 * @param navController The [NavHostController] managing the back stack.
 */
@Composable
fun KliqMainScaffold(
    navigationViewModel: NavigationViewModel = hiltViewModel(),
    topBarViewModel: TopBarViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val navigationState by navigationViewModel.navigationState.collectAsStateWithLifecycle()
    val topBarState by topBarViewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavigationRoute.Home.route

    // Sync ViewModel state when NavController route changes externally
    // (e.g., system back press)
    if (currentRoute != navigationState.currentRoute) {
        navigationViewModel.onTabSelected(currentRoute)
    }

    // Update top bar title whenever the route changes
    LaunchedEffect(currentRoute) {
        topBarViewModel.updateTitleForRoute(currentRoute)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            KliqBottomBar(
                currentRoute = currentRoute,
                notificationBadgeCount = navigationState.notificationBadgeCount,
                onTabSelected = { route ->
                    navigationViewModel.onTabSelected(route)
                    navController.navigate(route) {
                        // Pop up to start destination to avoid building a large back stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected tab
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        KliqNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            currentRoute = currentRoute,
            previousRoute = navigationState.previousRoute,
            topBarState = topBarState,
            onToggleMenu = topBarViewModel::toggleMenu,
            onDismissMenu = topBarViewModel::dismissMenu,
            onMenuAction = { action ->
                // Globale Menü-Aktionen werden hier zentral verarbeitet
                when (action) {
                    TopBarMenuAction.Settings -> { /* TODO: Settings-Screen öffnen */ }
                    TopBarMenuAction.EditProfile -> {
                        navController.navigate(NavigationRoute.Profile.route) {
                            launchSingleTop = true
                        }
                    }
                    TopBarMenuAction.ToggleTheme -> { /* TODO: Theme-Wechsel implementieren */ }
                    TopBarMenuAction.About -> { /* TODO: About-Dialog anzeigen */ }
                    TopBarMenuAction.Logout -> { /* TODO: Logout-Flow starten */ }
                }
            }
        )
    }
}

/**
 * Navigation host defining the composable destinations for each
 * bottom bar tab. Includes directional slide + fade transitions
 * based on tab position for polished screen-to-screen animation.
 *
 * @param navController The [NavHostController] managing navigation.
 * @param modifier Modifier applied to the NavHost container.
 * @param currentRoute The currently active route for transition direction.
 * @param previousRoute The previously active route for transition direction.
 * @param topBarState The current top bar UI state.
 * @param onToggleMenu Callback to toggle the overflow menu.
 * @param onDismissMenu Callback to dismiss the overflow menu.
 * @param onMenuAction Callback when a menu action is selected.
 */
@Composable
private fun KliqNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    currentRoute: String,
    previousRoute: String?,
    topBarState: TopBarUiState,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMenuAction: (TopBarMenuAction) -> Unit
) {
    val routes = NavigationRoute.bottomBarItems.map { it.route }
    val currentIndex = routes.indexOf(currentRoute)
    val previousIndex = if (previousRoute != null) routes.indexOf(previousRoute) else -1
    val slideRight = currentIndex > previousIndex

    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Home.route,
        modifier = modifier,
        enterTransition = { slideEnterTransition(slideRight) },
        exitTransition = { slideExitTransition(slideRight) },
        popEnterTransition = { slideEnterTransition(!slideRight) },
        popExitTransition = { slideExitTransition(!slideRight) }
    ) {
        composable(NavigationRoute.Home.route) {
            HomeScreen(
                topBarState = topBarState,
                onToggleMenu = onToggleMenu,
                onDismissMenu = onDismissMenu,
                onMenuAction = onMenuAction
            )
        }
        composable(NavigationRoute.Explore.route) {
            ExploreScreen(
                topBarState = topBarState,
                onToggleMenu = onToggleMenu,
                onDismissMenu = onDismissMenu,
                onMenuAction = onMenuAction
            )
        }
        composable(NavigationRoute.Map.route) {
            MapScreen(
                topBarState = topBarState,
                onToggleMenu = onToggleMenu,
                onDismissMenu = onDismissMenu,
                onMenuAction = onMenuAction
            )
        }
        composable(NavigationRoute.Notifications.route) {
            NotificationsScreen(
                topBarState = topBarState,
                onToggleMenu = onToggleMenu,
                onDismissMenu = onDismissMenu,
                onMenuAction = onMenuAction
            )
        }
        composable(NavigationRoute.Profile.route) {
            ProfileScreen(
                topBarState = topBarState,
                onToggleMenu = onToggleMenu,
                onDismissMenu = onDismissMenu,
                onMenuAction = onMenuAction
            )
        }
    }
}

/** Slide-in + fade-in enter transition based on navigation direction. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideEnterTransition(
    slideRight: Boolean
): EnterTransition {
    return slideIntoContainer(
        towards = if (slideRight) {
            AnimatedContentTransitionScope.SlideDirection.Start
        } else {
            AnimatedContentTransitionScope.SlideDirection.End
        },
        animationSpec = tween(durationMillis = 350),
        initialOffset = { it / 4 }
    ) + fadeIn(animationSpec = tween(durationMillis = 350))
}

/** Slide-out + fade-out exit transition based on navigation direction. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideExitTransition(
    slideRight: Boolean
): ExitTransition {
    return slideOutOfContainer(
        towards = if (slideRight) {
            AnimatedContentTransitionScope.SlideDirection.Start
        } else {
            AnimatedContentTransitionScope.SlideDirection.End
        },
        animationSpec = tween(durationMillis = 350),
        targetOffset = { it / 4 }
    ) + fadeOut(animationSpec = tween(durationMillis = 350))
}
