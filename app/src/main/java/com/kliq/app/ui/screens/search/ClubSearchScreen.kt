package com.kliq.app.ui.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kliq.app.ui.components.KliqScreenScaffold
import com.kliq.app.ui.components.search.ClubSearchBar
import com.kliq.app.ui.components.search.ClubSearchEmptyState
import com.kliq.app.ui.components.search.ClubSearchFilterBadges
import com.kliq.app.ui.components.search.ClubSearchLoadingState
import com.kliq.app.ui.components.search.ClubSearchResultList
import com.kliq.app.ui.navigation.TopBarMenuAction
import com.kliq.app.ui.navigation.TopBarUiState
import com.kliq.app.viewmodel.ClubSearchViewModel

/**
 * Haupt-Screen für die Suchfunktion von Clubs und Regionen (Kapitel 7.4).
 *
 * @param topBarState Aktueller TopBar UI-Zustand.
 * @param onToggleMenu Callback zum Umschalten des TopBar Menüs.
 * @param onDismissMenu Callback zum Schließen des Menüs.
 * @param onMenuAction Callback bei Menüauswahl.
 * @param onNavigateToClub Callback zur Navigation zur Club-Detailansicht.
 * @param viewModel Hilt-injiziertes [ClubSearchViewModel].
 */
@Composable
fun ClubSearchScreen(
    topBarState: TopBarUiState,
    onToggleMenu: () -> Unit,
    onDismissMenu: () -> Unit,
    onMenuAction: (TopBarMenuAction) -> Unit,
    onNavigateToClub: (String) -> Unit = {},
    viewModel: ClubSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    KliqScreenScaffold(
        title = "Clubs & Regionen Suche",
        isMenuExpanded = topBarState.isMenuExpanded,
        onToggleMenu = onToggleMenu,
        onDismissMenu = onDismissMenu,
        onMenuAction = onMenuAction
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live-Suchleiste
            ClubSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onQueryChanged(it) },
                onClearClick = { viewModel.clearSearch() }
            )

            // Filter Badges Row
            ClubSearchFilterBadges(
                activeFilter = uiState.activeFilter,
                onFilterSelected = { viewModel.onFilterChanged(it) }
            )

            // Zustandshandling: Loading, Empty oder Ergebnisse
            when {
                uiState.isLoading -> {
                    ClubSearchLoadingState()
                }

                uiState.clubResults.isEmpty() && uiState.regionResults.isEmpty() -> {
                    ClubSearchEmptyState(
                        title = if (uiState.searchQuery.isBlank()) "Suche starten" else "Keine Clubs in dieser Region gefunden",
                        description = if (uiState.searchQuery.isBlank()) 
                            "Gib den Namen eines Clubs, eine Stadt oder ein Musik-Genre ein."
                        else 
                            "Keine passenden Treffer für '${uiState.searchQuery}'. Versuche nach einer anderen Region oder einem Genre zu suchen."
                    )
                }

                else -> {
                    ClubSearchResultList(
                        clubResults = uiState.clubResults,
                        regionResults = uiState.regionResults,
                        onClubClick = onNavigateToClub,
                        onRegionClick = { regionName -> viewModel.selectRegion(regionName) },
                        onToggleFavorite = { clubId, currentFav -> viewModel.toggleFavorite(clubId, currentFav) }
                    )
                }
            }
        }
    }
}
