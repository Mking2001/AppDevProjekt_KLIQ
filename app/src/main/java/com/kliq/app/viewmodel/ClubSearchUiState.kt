package com.kliq.app.viewmodel

import com.kliq.app.data.model.RegionSearchResult
import com.kliq.app.data.model.SearchFilterType
import com.kliq.app.ui.model.ClubHighContrastItemState

data class ClubSearchUiState(
    val searchQuery: String = "",
    val activeFilter: SearchFilterType = SearchFilterType.ALL,
    val selectedRegion: String? = null,
    val selectedGenre: String? = null,
    val isLoading: Boolean = false,
    val clubResults: List<ClubHighContrastItemState> = emptyList(),
    val regionResults: List<RegionSearchResult> = emptyList(),
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val isGpsActive: Boolean = false,
    val errorMessage: String? = null,
    val recentSearchQueries: List<String> = emptyList()
)
