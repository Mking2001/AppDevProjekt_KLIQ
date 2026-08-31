package com.kliq.app.ui.screens.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.EventRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.domain.CurrentUserProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class ExploreUiState(
    val searchQuery: String = "",
    val selectedCategory: Int? = null,
    val minRating: Float = 0f,
    val onlyFavorites: Boolean = false,
    val categories: List<String> = CATEGORIES,
    val discoverItems: List<DiscoverItemUi> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    companion object {
        val CATEGORIES = listOf("Trending", "Events", "Clubs", "Bars", "Pubs", "Lounges")
    }
}

data class DiscoverItemUi(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val rating: Float = 0f,
    val region: String = "",
    val imageUrl: String? = null,
    val creatorUserId: String? = null,
    val isFavorite: Boolean = false,
    val isOpenNow: Boolean = false,
    val flameCount: Int = 0,
    val isHypedToday: Boolean = false,
    val isEvent: Boolean = false
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val clubRepository: ClubRepository,
    private val feedRepository: com.kliq.app.data.repository.FeedRepository,
    private val eventRepository: EventRepository? = null,
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var allItems: List<DiscoverItemUi> = emptyList()
    private var blockedUserIds: Set<String> = emptySet()

    init {
        observeDiscoverContent()
        observeBlockedUsers()
    }

    private fun observeDiscoverContent() {
        viewModelScope.launch {
            val currentUserId = currentUserProvider.userId()
            combine(
                clubRepository.getAllClubs(),
                clubRepository.getHypedClubIdsToday(currentUserId),
                feedRepository.getPinnedEvents()
            ) { clubs, hypedIds, pinnedEvents ->
                val hypedSet = hypedIds.toSet()

                val clubItems = clubs.map { club ->
                    DiscoverItemUi(
                        id = club.id,
                        title = club.name,
                        subtitle = club.location.address.ifBlank { club.region },
                        category = normalizeCategory(club.category),
                        rating = club.averageRating.toFloat(),
                        region = club.region.ifBlank { club.location.address },
                        imageUrl = club.imageUrl.ifBlank { "https://images.unsplash.com/photo-1566737236500-c8ac43014a67?auto=format&fit=crop&w=1200&q=80" },
                        isFavorite = club.isFavorite,
                        isOpenNow = club.operatingHours.isOpenNow,
                        flameCount = club.flameCount,
                        isHypedToday = hypedSet.contains(club.id),
                        isEvent = club.category.contains("Event", ignoreCase = true)
                    )
                }

                val eventItems = pinnedEvents.filter { event -> clubs.none { it.id == event.id } }.map { post ->
                    DiscoverItemUi(
                        id = post.id,
                        title = post.clubName ?: "Community Event",
                        subtitle = post.contentText,
                        category = "Events",
                        rating = 5.0f,
                        region = post.locationAddress ?: "Klagenfurt",
                        imageUrl = post.imageUrl?.ifBlank { null },
                        creatorUserId = post.authorUserId,
                        isFavorite = false,
                        isOpenNow = true,
                        flameCount = post.flameCount,
                        isHypedToday = hypedSet.contains(post.id),
                        isEvent = true
                    )
                }

                clubItems + eventItems
            }
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Clubs & Events konnten nicht geladen werden: ${error.localizedMessage}"
                        )
                    }
                }
                .collect { items ->
                    allItems = items
                    _uiState.update { it.copy(isLoading = false) }
                    applyFilters()
                }
        }
    }

    private fun observeBlockedUsers() {
        viewModelScope.launch {
            userRepository.getBlockedUserIds(currentUserProvider.userId())
                .catch { }
                .collect { blockedList ->
                    blockedUserIds = blockedList.toSet()
                    applyFilters()
                }
        }
    }

    private fun normalizeCategory(rawCategory: String): String {
        val value = rawCategory.trim().lowercase(Locale.GERMAN)
        return when {
            value.contains("club") -> "Clubs"
            value.contains("bar") -> "Bars"
            value.contains("pub") -> "Pubs"
            value.contains("lounge") -> "Lounges"
            value.contains("restaurant") -> "Restaurants"
            else -> "Clubs"
        }
    }

    fun onSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onCategorySelected(index: Int) {
        _uiState.update { state ->
            state.copy(selectedCategory = if (state.selectedCategory == index) null else index)
        }
        applyFilters()
    }

    fun onMinRatingSelected(rating: Float) {
        _uiState.update { state ->
            state.copy(minRating = if (state.minRating == rating) 0f else rating)
        }
        applyFilters()
    }

    fun onToggleFavoritesFilter() {
        _uiState.update { it.copy(onlyFavorites = !it.onlyFavorites) }
        applyFilters()
    }

    fun onToggleFavorite(itemId: String) {
        val item = allItems.find { it.id == itemId && !it.isEvent } ?: return
        viewModelScope.launch {
            runCatching { clubRepository.toggleFavorite(itemId, item.isFavorite) }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorMessage = "Favorit konnte nicht gespeichert werden: ${error.localizedMessage}")
                    }
                }
        }
    }

    fun onToggleHype(itemId: String) {
        viewModelScope.launch {
            val userId = currentUserProvider.userId()
            clubRepository.toggleClubHype(itemId, userId)
            feedRepository.togglePostHype(itemId, userId)
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        val query = state.searchQuery.trim().lowercase(Locale.GERMAN)
        val selectedCategory = state.selectedCategory?.let { state.categories.getOrNull(it) }

        val filtered = allItems.filter { item ->
            val isBlocked = item.creatorUserId?.let { blockedUserIds.contains(it) } ?: false
            if (isBlocked) return@filter false
            if (state.onlyFavorites && !item.isFavorite) return@filter false

            val matchesQuery = query.isEmpty() ||
                    item.title.lowercase(Locale.GERMAN).contains(query) ||
                    item.subtitle.lowercase(Locale.GERMAN).contains(query) ||
                    item.region.lowercase(Locale.GERMAN).contains(query)

            val matchesRating = item.rating >= state.minRating
            val matchesCategory = selectedCategory == null ||
                    selectedCategory == "Trending" ||
                    item.category == selectedCategory

            matchesQuery && matchesRating && matchesCategory
        }

        val sorted = if (selectedCategory == "Trending" || selectedCategory == null) {
            filtered.sortedByDescending { it.flameCount }
        } else {
            filtered
        }

        _uiState.update { it.copy(discoverItems = sorted) }
    }

    fun onErrorMessageDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
