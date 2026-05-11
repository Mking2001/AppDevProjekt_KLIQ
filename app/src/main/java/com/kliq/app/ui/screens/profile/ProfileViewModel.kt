/**
 * AI-GENERATED CODE
 * Dieses ViewModel wurde vollständig durch KI generiert.
 * Erstellt im Rahmen von Schritt 4: Layout-Scaffolding der Haupt-Screens.
 * Datum: 2026-05-11
 */
package com.kliq.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ============================================================
// AI-generiert: UI-State für den Profile/Profil-Screen.
// Folgt dem MVVM-Pattern mit immutablem State und UDF.
// ============================================================

/**
 * AI-generiert: Immutable UI State für den Profile-Screen.
 *
 * @param displayName Anzeigename des Benutzers.
 * @param username Benutzername/Handle.
 * @param bio Kurzbeschreibung/Bio.
 * @param location Standort des Benutzers.
 * @param postsCount Anzahl der Beiträge.
 * @param followersCount Anzahl der Follower.
 * @param followingCount Anzahl der gefolgten Personen.
 * @param selectedTabIndex Ausgewählter Profil-Tab.
 * @param tabs Verfügbare Profil-Tabs.
 * @param isOwnProfile Ob es das eigene Profil ist.
 */
data class ProfileUiState(
    val displayName: String = "",
    val username: String = "",
    val bio: String = "",
    val location: String = "",
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val selectedTabIndex: Int = 0,
    val tabs: List<String> = emptyList(),
    val isOwnProfile: Boolean = true
)

/**
 * AI-generiert: ViewModel für den Profile/Profil-Screen.
 * Verwaltet Profildaten und Tab-Auswahl.
 *
 * Folgt strikt dem MVVM-Pattern:
 * - Kein Compose/Android-Import
 * - Immutable State via StateFlow
 */
@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    /**
     * AI-generiert: Lädt Platzhalter-Profildaten für die visuelle Darstellung.
     */
    private fun loadMockData() {
        _uiState.update { state ->
            state.copy(
                displayName = "Max Mustermann",
                username = "@maxmuster",
                bio = "Nightlife-Enthusiast 🌙 | Immer unterwegs | München 📍",
                location = "München, Deutschland",
                postsCount = 127,
                followersCount = 1842,
                followingCount = 394,
                tabs = listOf("Beiträge", "Events", "Über mich"),
                isOwnProfile = true
            )
        }
    }

    /**
     * AI-generiert: Stub für Tab-Auswahl im Profil.
     * @param index Index des ausgewählten Tabs.
     */
    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    /**
     * AI-generiert: Stub für "Profil bearbeiten"-Aktion.
     */
    fun onEditProfile() {
        // TODO: Navigation zum Profil-Editor
    }

    /**
     * AI-generiert: Stub für Follow/Unfollow-Toggle.
     * Nur relevant wenn es nicht das eigene Profil ist.
     */
    fun onFollowToggle() {
        // TODO: Follow/Unfollow-API-Aufruf
    }
}
