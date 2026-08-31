package com.kliq.app.viewmodel

import com.kliq.app.ui.navigation.ChatRoutes
import com.kliq.app.ui.navigation.ClubRoutes
import com.kliq.app.ui.navigation.CoreRoutes
import com.kliq.app.ui.navigation.NavigationRoute
import com.kliq.app.ui.navigation.NavigationViewModel
import com.kliq.app.ui.navigation.ProfileRoutes
import com.kliq.app.ui.navigation.ScreenTransitionType
import com.kliq.app.testing.FakeChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [NavigationViewModel] transition state logic and MVVM decoupling.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NavigationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: NavigationViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NavigationViewModel(chatRepository = FakeChatRepository())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState_defaultTabSwitchType() {
        val state = viewModel.navigationState.value
        assertEquals(NavigationRoute.Home.route, state.currentRoute)
        assertEquals(ScreenTransitionType.TabSwitch, state.transitionType)
        assertFalse(state.isTransitioning)
        assertEquals(300, state.animationDurationMs)
    }

    @Test
    fun testOnTabSelected_updatesRouteAndTransitionType() {
        viewModel.onTabSelected(NavigationRoute.Map.route)
        val state = viewModel.navigationState.value

        assertEquals(NavigationRoute.Map.route, state.currentRoute)
        assertEquals(NavigationRoute.Home.route, state.previousRoute)
        assertEquals(ScreenTransitionType.TabSwitch, state.transitionType)
        assertTrue(state.isTransitioning)
    }

    @Test
    fun testDetermineTransitionType_sharedElementExpandForClubDetail() {
        val transitionType = viewModel.determineTransitionType(
            fromRoute = NavigationRoute.Map.route,
            toRoute = ClubRoutes.clubDetail("club_123")
        )
        assertEquals(ScreenTransitionType.SharedElementExpand, transitionType)
    }

    @Test
    fun testDetermineTransitionType_modalSlideUpForQRScanner() {
        val transitionType = viewModel.determineTransitionType(
            fromRoute = NavigationRoute.Profile.route,
            toRoute = ProfileRoutes.QR_SCANNER
        )
        assertEquals(ScreenTransitionType.ModalSlideUp, transitionType)
    }

    @Test
    fun testDetermineTransitionType_detailPushForChatDetail() {
        val transitionType = viewModel.determineTransitionType(
            fromRoute = ChatRoutes.CHAT_LIST,
            toRoute = ChatRoutes.chatDetail("chat_456")
        )
        assertEquals(ScreenTransitionType.DetailPush, transitionType)
    }

    @Test
    fun testDetermineTransitionType_detailPopWhenLeavingDetail() {
        val transitionType = viewModel.determineTransitionType(
            fromRoute = ChatRoutes.chatDetail("chat_456"),
            toRoute = ChatRoutes.CHAT_LIST
        )
        assertEquals(ScreenTransitionType.DetailPop, transitionType)
    }

    @Test
    fun testDetermineTransitionType_defaultFadeForSplash() {
        val transitionType = viewModel.determineTransitionType(
            fromRoute = CoreRoutes.SPLASH,
            toRoute = NavigationRoute.Home.route
        )
        assertEquals(ScreenTransitionType.DefaultFade, transitionType)
    }

    @Test
    fun testTransitionLifecycleEvents() {
        viewModel.onTransitionStart()
        assertTrue(viewModel.navigationState.value.isTransitioning)

        viewModel.onTransitionEnd()
        assertFalse(viewModel.navigationState.value.isTransitioning)
    }
}
