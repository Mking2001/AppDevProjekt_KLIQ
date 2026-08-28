package com.kliq.app.ui.navigation

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import androidx.navigation.navDeepLink
import com.kliq.app.data.model.ChatPushPayload
import com.kliq.app.ui.components.KliqAboutDialog
import com.kliq.app.ui.components.KliqSettingsDialog
import com.kliq.app.ui.components.clearApplicationCache
import com.kliq.app.ui.components.openAppNotificationSettings
import com.kliq.app.ui.screens.auth.AuthSelectionScreen
import com.kliq.app.ui.screens.auth.PhoneLoginScreen
import com.kliq.app.ui.screens.auth.RegisterScreen
import com.kliq.app.ui.screens.chat.ChatDetailScreen
import com.kliq.app.ui.screens.chat.ChatListScreen
import com.kliq.app.ui.screens.club.ClubDetailScreen
import com.kliq.app.ui.screens.explore.ExploreScreen
import com.kliq.app.ui.screens.home.HomeScreen
import com.kliq.app.ui.screens.map.MapScreen
import com.kliq.app.ui.screens.notifications.NotificationsScreen
import com.kliq.app.ui.screens.onboarding.ConsumptionHabitsScreen
import com.kliq.app.ui.screens.onboarding.IntentMatchingScreen
import com.kliq.app.ui.screens.onboarding.ProfileCreationScreen
import com.kliq.app.ui.screens.profile.OtherUserProfileScreen
import com.kliq.app.ui.screens.profile.ProfileScreen
import com.kliq.app.ui.screens.qr.QRScannerScreen
import com.kliq.app.ui.screens.splash.SplashScreen
import com.kliq.app.ui.screens.verification.SmsVerificationScreen
import com.kliq.app.ui.screens.verification.SmsVerificationViewModel
import com.kliq.app.ui.navigation.KliqScreenTransitions.defaultFadeEnterTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.defaultFadeExitTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.detailPopEnterTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.detailPopExitTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.detailPushEnterTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.detailPushExitTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.modalSlideUpEnterTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.modalSlideUpExitTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.sharedElementExpandEnterTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.sharedElementExpandExitTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.sharedElementPopExitTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.tabEnterTransition
import com.kliq.app.ui.navigation.KliqScreenTransitions.tabExitTransition
import com.kliq.app.viewmodel.AuthViewModel
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
    initialIntent: Intent? = null,
    navigationViewModel: NavigationViewModel = hiltViewModel(),
    topBarViewModel: TopBarViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val themeState by themeViewModel.themeState.collectAsStateWithLifecycle()
    var isSettingsDialogVisible by remember { mutableStateOf(false) }
    var isAboutDialogVisible by remember { mutableStateOf(false) }
    val navigationState by navigationViewModel.navigationState.collectAsStateWithLifecycle()
    val topBarState by topBarViewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavigationRoute.Home.route

    val showBottomBar = currentRoute !in listOf(
        ChatRoutes.CHAT_DETAIL,
        CoreRoutes.SPLASH,
        CoreRoutes.AUTH_SELECTION,
        CoreRoutes.PHONE_LOGIN,
        CoreRoutes.REGISTER,
        ProfileRoutes.QR_SCANNER
    )

    if (currentRoute != navigationState.currentRoute) {
        navigationViewModel.onTabSelected(currentRoute)
    }

    LaunchedEffect(currentRoute) {
        com.kliq.app.service.crash.CrashReportingLogger.setCustomKey("current_route", currentRoute)
        com.kliq.app.service.crash.CrashReportingLogger.logBreadcrumb("Navigated to $currentRoute")
    }


    LaunchedEffect(currentRoute) {
        topBarViewModel.updateTitleForRoute(currentRoute)
    }

    LaunchedEffect(initialIntent) {
        initialIntent?.let { intent ->
            if (intent.action == Intent.ACTION_VIEW && intent.data != null) {
                navController.handleDeepLink(intent)
            } else {
                val chatId = intent.getStringExtra(ChatPushPayload.KEY_CHAT_ID)
                if (!chatId.isNullOrBlank()) {
                    navController.navigate(ChatRoutes.chatDetail(chatId)) {
                        launchSingleTop = true
                    }
                }
            }
        }
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
                            if (route != currentRoute) {
                                navigationViewModel.onTabSelected(route)
                                navController.navigate(route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            KliqNavHost(
                navController = navController,
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                currentRoute = currentRoute,
                previousRoute = navigationState.previousRoute,
                topBarState = topBarState,
                authViewModel = authViewModel,
                onToggleMenu = topBarViewModel::toggleMenu,
                onDismissMenu = topBarViewModel::dismissMenu,
                onMenuAction = { action ->
                    when (action) {
                        TopBarMenuAction.Settings -> { isSettingsDialogVisible = true }
                        TopBarMenuAction.EditProfile -> {
                            navController.navigate(NavigationRoute.Profile.route) {
                                launchSingleTop = true
                            }
                        }
                        TopBarMenuAction.ToggleTheme -> { themeViewModel.toggleTheme() }
                        TopBarMenuAction.About -> { isAboutDialogVisible = true }
                        TopBarMenuAction.Logout -> {
                            authViewModel.logout()
                            navController.navigate(CoreRoutes.AUTH_SELECTION) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                },
                onNavigateToActivities = {
                    navController.navigate(NavigationRoute.Notifications.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToChatDetail = { chatId ->
                    navController.navigate(ChatRoutes.chatDetail(chatId)) {
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

    KliqSettingsDialog(
        isVisible = isSettingsDialogVisible,
        themeMode = themeState.themeMode,
        onToggleTheme = { themeViewModel.toggleTheme() },
        onOpenNotificationSettings = { openAppNotificationSettings(context) },
        onClearCache = {
            val removed = clearApplicationCache(context)
            Toast.makeText(
                context,
                if (removed > 0) "Cache geleert ($removed Einträge)." else "Der Cache war bereits leer.",
                Toast.LENGTH_SHORT
            ).show()
        },
        onDismiss = { isSettingsDialogVisible = false }
    )

    KliqAboutDialog(
        isVisible = isAboutDialogVisible,
        onDismiss = { isAboutDialogVisible = false }
    )
}

@Composable
private fun KliqNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    currentRoute: String,
    previousRoute: String?,
    topBarState: TopBarUiState,
    authViewModel: AuthViewModel,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMenuAction: (TopBarMenuAction) -> Unit,
    onNavigateToActivities: () -> Unit,
    onNavigateToChatDetail: (String) -> Unit,
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
        enterTransition = { tabEnterTransition(slideRight) },
        exitTransition = { tabExitTransition(slideRight) },
        popEnterTransition = { tabEnterTransition(!slideRight) },
        popExitTransition = { tabExitTransition(!slideRight) }
    ) {
        composable(
            route = CoreRoutes.SPLASH,
            enterTransition = { defaultFadeEnterTransition() },
            exitTransition = { defaultFadeExitTransition() }
        ) {
            SplashScreen(
                authViewModel = authViewModel,
                onNavigateToHome = {
                    navController.navigate(NavigationRoute.Home.route) {
                        popUpTo(CoreRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToAuthSelection = {
                    navController.navigate(CoreRoutes.AUTH_SELECTION) {
                        popUpTo(CoreRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = CoreRoutes.AUTH_SELECTION,
            enterTransition = { defaultFadeEnterTransition() },
            exitTransition = { defaultFadeExitTransition() }
        ) {
            AuthSelectionScreen(
                onNavigateToLogin = {
                    navController.navigate(CoreRoutes.PHONE_LOGIN)
                },
                onNavigateToRegister = {
                    navController.navigate(CoreRoutes.REGISTER)
                }
            )
        }
        composable(
            route = CoreRoutes.PHONE_LOGIN,
            enterTransition = { defaultFadeEnterTransition() },
            exitTransition = { defaultFadeExitTransition() }
        ) {
            PhoneLoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavigationRoute.Home.route) {
                        popUpTo(CoreRoutes.AUTH_SELECTION) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToRegister = {
                    navController.navigate(CoreRoutes.REGISTER)
                }
            )
        }
        composable(
            route = CoreRoutes.REGISTER,
            enterTransition = { detailPushEnterTransition() },
            exitTransition = { detailPushExitTransition() },
            popEnterTransition = { detailPopEnterTransition() },
            popExitTransition = { detailPopExitTransition() }
        ) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegistrationSuccess = {
                    navController.navigate(NavigationRoute.Home.route) {
                        popUpTo(CoreRoutes.AUTH_SELECTION) { inclusive = true }
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
                onNavigateToActivities = onNavigateToActivities,
                onNavigateToUserProfile = { userId ->
                    navController.navigate(ProfileRoutes.otherUserProfile(userId))
                }
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
                onMenuAction = onMenuAction,
                onNavigateToClub = onNavigateToClub,
                onNavigateToChat = onNavigateToChatDetail
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
                onMenuAction = onMenuAction,
                onNavigateToActivities = onNavigateToActivities,
                onNavigateToQrScanner = {
                    navController.navigate(ProfileRoutes.QR_SCANNER) {
                        launchSingleTop = true
                    }
                },
                onNavigateToClub = onNavigateToClub
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
                    navController.navigate(NavigationRoute.ConsumptionHabits.route) {
                        popUpTo(NavigationRoute.IntentMatching.route) { inclusive = true }
                    }
                }
            )
        }
        composable(NavigationRoute.ConsumptionHabits.route) {
            ConsumptionHabitsScreen(
                onHabitsConfirmed = {
                    navController.navigate(NavigationRoute.Home.route) {
                        popUpTo(NavigationRoute.ConsumptionHabits.route) { inclusive = true }
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

        composable(
            route = ChatRoutes.CHAT_LIST,
            enterTransition = { detailPushEnterTransition() },
            exitTransition = { detailPushExitTransition() },
            popEnterTransition = { detailPopEnterTransition() },
            popExitTransition = { detailPopExitTransition() }
        ) {
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
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = ChatRoutes.DEEP_LINK_URI_PATTERN
                }
            ),
            enterTransition = { detailPushEnterTransition() },
            exitTransition = { detailPushExitTransition() },
            popEnterTransition = { detailPopEnterTransition() },
            popExitTransition = { detailPopExitTransition() }
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
            ),
            enterTransition = { sharedElementExpandEnterTransition() },
            exitTransition = { sharedElementExpandExitTransition() },
            popEnterTransition = { sharedElementExpandEnterTransition() },
            popExitTransition = { sharedElementPopExitTransition() }
        ) { backStackEntry ->
            val clubId = backStackEntry.arguments?.getString(ClubRoutes.ARG_CLUB_ID) ?: ""
            ClubDetailScreen(
                clubId = clubId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = ProfileRoutes.OTHER_USER_PROFILE,
            arguments = listOf(
                navArgument(ProfileRoutes.ARG_USER_ID) {
                    type = NavType.StringType
                }
            ),
            enterTransition = { detailPushEnterTransition() },
            exitTransition = { detailPushExitTransition() },
            popEnterTransition = { detailPopEnterTransition() },
            popExitTransition = { detailPopExitTransition() }
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString(ProfileRoutes.ARG_USER_ID) ?: ""
            OtherUserProfileScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChat = { targetUserId ->
                    navController.navigate(ChatRoutes.chatDetail("chat_$targetUserId"))
                }
            )
        }

        composable(
            route = ProfileRoutes.QR_SCANNER,
            enterTransition = { modalSlideUpEnterTransition() },
            exitTransition = { modalSlideUpExitTransition() },
            popEnterTransition = { modalSlideUpEnterTransition() },
            popExitTransition = { modalSlideUpExitTransition() }
        ) {
            QRScannerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToUserProfile = { userId ->
                    navController.navigate(ProfileRoutes.otherUserProfile(userId))
                }
            )
        }
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideEnterTransition(
    slideRight: Boolean
): EnterTransition {
    return tabEnterTransition(slideRight)
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.slideExitTransition(
    slideRight: Boolean
): ExitTransition {
    return tabExitTransition(slideRight)
}

