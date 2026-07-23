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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kliq.app.ui.screens.auth.PhoneLoginScreen
import com.kliq.app.ui.screens.chat.ChatDetailScreen
import com.kliq.app.ui.screens.chat.ChatListScreen
import com.kliq.app.ui.screens.club.ClubDetailScreen
import com.kliq.app.ui.screens.explore.ExploreScreen
import com.kliq.app.ui.screens.home.HomeScreen
import com.kliq.app.ui.screens.map.MapScreen
import com.kliq.app.ui.screens.notifications.NotificationsScreen
import com.kliq.app.ui.screens.onboarding.IntentMatchingScreen
import com.kliq.app.ui.screens.onboarding.ProfileCreationScreen
import com.kliq.app.ui.screens.profile.ProfileScreen
import com.kliq.app.ui.screens.splash.SplashScreen
import com.kliq.app.ui.screens.verification.SmsVerificationScreen
import com.kliq.app.ui.screens.verification.SmsVerificationViewModel
import com.kliq.app.viewmodel.ThemeViewModel

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

/**
 * Main scaffold composable that hosts the Bottom Navigation Bar
 * and the [NavHost] for all primary screens.
 */
@Composable
fun KliqMainScaffold(
    navigationViewModel: NavigationViewModel = hiltViewModel(),
    topBarViewModel: TopBarViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val navigationState by navigationViewModel.navigationState.collectAsStateWithLifecycle()
    val topBarState by topBarViewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavigationRoute.Home.route

    val showBottomBar = currentRoute !in listOf(
        ChatRoutes.CHAT_LIST,
        ChatRoutes.CHAT_DETAIL,
        CoreRoutes.SPLASH,
        CoreRoutes.PHONE_LOGIN
    )

    if (currentRoute != navigationState.currentRoute) {
        navigationViewModel.onTabSelected(currentRoute)
    }

    LaunchedEffect(currentRoute) {
        topBarViewModel.updateTitleForRoute(currentRoute)
    }

    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    KliqBottomBar(
                        currentRoute = currentRoute,
                        notificationBadgeCount = navigationState.notificationBadgeCount,
                        onTabSelected = { route ->
                            navigationViewModel.onTabSelected(route)
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
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
                    when (action) {
                        TopBarMenuAction.Settings -> { /* Settings-Screen öffnen */ }
                        TopBarMenuAction.EditProfile -> {
                            navController.navigate(NavigationRoute.Profile.route) {
                                launchSingleTop = true
                            }
                        }
                        TopBarMenuAction.ToggleTheme -> { themeViewModel.toggleTheme() }
                        TopBarMenuAction.About -> { /* About-Dialog anzeigen */ }
                        TopBarMenuAction.Logout -> {
                            navController.navigate(
                                NavigationRoute.verificationRoute("+49 176 12345678")
                            )
                        }
                    }
                },
                onNavigateToChat = {
                    navController.navigate(ChatRoutes.CHAT_LIST) {
                        launchSingleTop = true
                    }
                },
                onNavigateToClub = { clubId ->
                    navController.navigate(ClubRoutes.clubDetail(clubId)) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
private fun KliqNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    currentRoute: String,
    previousRoute: String?,
    topBarState: TopBarUiState,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMenuAction: (TopBarMenuAction) -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToClub: (String) -> Unit
) {
    val routes = NavigationRoute.bottomBarItems.map { it.route }
    val currentIndex = routes.indexOf(currentRoute)
    val previousIndex = if (previousRoute != null) routes.indexOf(previousRoute) else -1
    val slideRight = currentIndex > previousIndex

    NavHost(
        navController = navController,
        startDestination = CoreRoutes.SPLASH,
        modifier = modifier,
        enterTransition = { slideEnterTransition(slideRight) },
        exitTransition = { slideExitTransition(slideRight) },
        popEnterTransition = { slideEnterTransition(!slideRight) },
        popExitTransition = { slideExitTransition(!slideRight) }
    ) {
        composable(CoreRoutes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(CoreRoutes.PHONE_LOGIN) {
                        popUpTo(CoreRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(CoreRoutes.PHONE_LOGIN) {
            PhoneLoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavigationRoute.Home.route) {
                        popUpTo(CoreRoutes.PHONE_LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(NavigationRoute.Home.route) {
            HomeScreen(
                topBarState = topBarState,
                onToggleMenu = onToggleMenu,
                onDismissMenu = onDismissMenu,
                onMenuAction = onMenuAction,
                onNavigateToChat = onNavigateToChat
            )
        }
        composable(NavigationRoute.Explore.route) {
            ExploreScreen(
                topBarState = topBarState,
                onToggleMenu = onToggleMenu,
                onDismissMenu = onDismissMenu,
                onMenuAction = onMenuAction,
                onNavigateToClub = onNavigateToClub
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
        composable(NavigationRoute.ProfileCreation.route) {
            ProfileCreationScreen(
                onProfileCreated = {
                    navController.navigate(NavigationRoute.IntentMatching.route) {
                        popUpTo(NavigationRoute.ProfileCreation.route) { inclusive = true }
                    }
                }
            )
        }
        composable(NavigationRoute.IntentMatching.route) {
            IntentMatchingScreen(
                onIntentConfirmed = {
                    navController.navigate(NavigationRoute.Home.route) {
                        popUpTo(NavigationRoute.IntentMatching.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavigationRoute.VERIFICATION_ROUTE,
            arguments = listOf(
                navArgument(SmsVerificationViewModel.PHONE_NUMBER_KEY) {
                    type = NavType.StringType
                }
            )
        ) {
            SmsVerificationScreen(
                onVerificationSuccess = {
                    navController.navigate(NavigationRoute.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ChatRoutes.CHAT_LIST) {
            ChatListScreen(
                onNavigateBack = { navController.popBackStack() },
                onChatSelected = { chatId ->
                    navController.navigate(ChatRoutes.chatDetail(chatId))
                }
            )
        }
        composable(
            route = ChatRoutes.CHAT_DETAIL,
            arguments = listOf(
                navArgument(ChatRoutes.ARG_CHAT_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString(ChatRoutes.ARG_CHAT_ID) ?: ""
            ChatDetailScreen(
                chatId = chatId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ClubRoutes.CLUB_DETAIL,
            arguments = listOf(
                navArgument(ClubRoutes.ARG_CLUB_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val clubId = backStackEntry.arguments?.getString(ClubRoutes.ARG_CLUB_ID) ?: ""
            ClubDetailScreen(
                clubId = clubId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

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
