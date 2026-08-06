package com.kliq.app.ui.navigation

/**
 * Encapsulates the specific category of screen transition animation
 * within the Kliq application navigation flow.
 *
 * Used by [NavigationViewModel] to communicate UI animation intent
 * while preserving clean MVVM separation.
 */
enum class ScreenTransitionType {
    /** Horizontal sliding and scaled fade for switching between primary bottom tabs */
    TabSwitch,

    /** Push forward transition into a detail screen (e.g. Chat Detail, User Profile) */
    DetailPush,

    /** Pop backward transition returning to a parent screen */
    DetailPop,

    /** Shared element zoom & vertical slide transition for Map to Club Analytics/Details */
    SharedElementExpand,

    /** Slide up overlay transition for modal screens (e.g. QR Scanner) */
    ModalSlideUp,

    /** Crossfade transition for splash and authentication screens */
    DefaultFade
}
