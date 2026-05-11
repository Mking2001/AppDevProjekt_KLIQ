/**
 * AI-GENERATED CODE
 * Dieses ViewModel wurde vollständig durch KI generiert.
 * Erstellt im Rahmen von Schritt 4: Layout-Scaffolding der Haupt-Screens.
 * Datum: 2026-05-11
 */
package com.kliq.app.ui.screens.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ============================================================
// AI-generiert: UI-State für den Home-Feed-Screen.
// Folgt dem MVVM-Pattern mit immutablem State und
// unidirektionalem Datenfluss (UDF).
// ============================================================

/**
 * AI-generiert: Immutable UI State für den Home-Screen.
 * Bildet alle darstellungsrelevanten Zustände des Feeds ab.
 *
 * @param isLoading Ob der Feed gerade geladen wird.
 * @param isRefreshing Ob ein Pull-to-Refresh aktiv ist.
 * @param feedItems Platzhalter-Feed-Einträge für das Scaffolding.
 * @param storyItems Platzhalter-Story-Einträge für die Story-Row.
 * @param errorMessage Optionale Fehlermeldung.
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val feedItems: List<FeedItemUi> = emptyList(),
    val storyItems: List<StoryItemUi> = emptyList(),
    val errorMessage: String? = null
)

/**
 * AI-generiert: Platzhalter-Datenklasse für einen Feed-Eintrag.
 */
data class FeedItemUi(
    val id: String,
    val userName: String,
    val timeAgo: String,
    val contentText: String,
    val likeCount: Int = 0,
    val isLiked: Boolean = false
)

/**
 * AI-generiert: Platzhalter-Datenklasse für einen Story-Eintrag.
 */
data class StoryItemUi(
    val id: String,
    val userName: String,
    val hasUnseenStory: Boolean = true
)

/**
 * AI-generiert: ViewModel für den Home-Feed-Screen.
 * Verwaltet den Feed-State und stellt Stub-Methoden
 * für spätere Feature-Implementierung bereit.
 *
 * Folgt strikt dem MVVM-Pattern:
 * - Kein Compose/Android-Import
 * - Immutable State via StateFlow
 * - Intent-basierte Actions
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // AI-generiert: Platzhalter-Daten für das Layout-Scaffolding
        loadMockData()
    }

    /**
     * AI-generiert: Lädt Platzhalter-Daten für die visuelle Darstellung.
     * Wird in der finalen Implementierung durch echte Repository-Aufrufe ersetzt.
     */
    private fun loadMockData() {
        _uiState.update { state ->
            state.copy(
                storyItems = listOf(
                    StoryItemUi("1", "Anna"),
                    StoryItemUi("2", "Max"),
                    StoryItemUi("3", "Lisa"),
                    StoryItemUi("4", "Tom"),
                    StoryItemUi("5", "Sarah"),
                    StoryItemUi("6", "Jonas"),
                    StoryItemUi("7", "Mia", hasUnseenStory = false)
                ),
                feedItems = listOf(
                    FeedItemUi("1", "Anna M.", "Vor 15 Min.", "Heute Abend wird es episch! 🎉 Wer ist dabei?", likeCount = 42),
                    FeedItemUi("2", "Max K.", "Vor 1 Std.", "Bester Abend seit langem 🔥", likeCount = 128),
                    FeedItemUi("3", "Lisa W.", "Vor 2 Std.", "Neue Location entdeckt – absolute Empfehlung!", likeCount = 67),
                    FeedItemUi("4", "Tom S.", "Vor 3 Std.", "Wochenende kann kommen 🙌", likeCount = 23)
                )
            )
        }
    }

    /**
     * AI-generiert: Stub für Feed-Aktualisierung.
     * Wird mit echtem Pull-to-Refresh und Repository-Call implementiert.
     */
    fun refreshFeed() {
        _uiState.update { it.copy(isRefreshing = true) }
        // TODO: Repository-Aufruf implementieren
        _uiState.update { it.copy(isRefreshing = false) }
    }

    /**
     * AI-generiert: Stub für Like-Aktion auf einem Post.
     * @param postId ID des zu likenden Beitrags.
     */
    fun onLikePost(postId: String) {
        _uiState.update { state ->
            state.copy(
                feedItems = state.feedItems.map { item ->
                    if (item.id == postId) {
                        item.copy(
                            isLiked = !item.isLiked,
                            likeCount = if (item.isLiked) item.likeCount - 1 else item.likeCount + 1
                        )
                    } else item
                }
            )
        }
    }

    /**
     * AI-generiert: Stub für Post-Erstellung.
     * Öffnet in der finalen Version einen Post-Editor.
     */
    fun onCreatePost() {
        // TODO: Navigation zum Post-Editor
    }
}
