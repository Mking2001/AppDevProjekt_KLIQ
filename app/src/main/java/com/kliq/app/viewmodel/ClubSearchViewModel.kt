package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.RegionSearchResult
import com.kliq.app.data.model.SearchFilterType
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.ui.model.toHighContrastUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class ClubSearchViewModel @Inject constructor(
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubSearchUiState())
    val uiState: StateFlow<ClubSearchUiState> = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")
    private val _filterFlow = MutableStateFlow(SearchFilterType.ALL)
    private val _locationFlow = MutableStateFlow<Pair<Double?, Double?>>(Pair(null, null))

    init {
        observeSearchStreams()
    }

    private fun observeSearchStreams() {
        viewModelScope.launch {
            combine(
                _queryFlow.debounce(300L).distinctUntilChanged(),
                _filterFlow,
                _locationFlow
            ) { query, filter, location ->
                Triple(query, filter, location)
            }.flatMapLatest { (query, filter, location) ->
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val nameQuery = when (filter) {
                    SearchFilterType.NAME, SearchFilterType.ALL -> query
                    else -> ""
                }
                val regionFilter = when (filter) {
                    SearchFilterType.REGION -> query.ifBlank { _uiState.value.selectedRegion }
                    else -> _uiState.value.selectedRegion
                }
                val genreFilter = when (filter) {
                    SearchFilterType.GENRE -> query.ifBlank { _uiState.value.selectedGenre }
                    else -> _uiState.value.selectedGenre
                }

                combine(
                    clubRepository.searchClubsFiltered(
                        query = nameQuery,
                        regionFilter = regionFilter,
                        genreFilter = genreFilter
                    ),
                    clubRepository.searchRegionsAndCities(query)
                ) { clubs, regions ->
                    Pair(clubs, regions)
                }
            }
            .catch { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.localizedMessage ?: "Fehler bei der Suche"
                    )
                }
            }
            .collect { (clubs, regions) ->
                val lat = _uiState.value.userLatitude
                val lon = _uiState.value.userLongitude

                if (_queryFlow.value.length >= 3 && clubs.isEmpty()) {
                    executeExternalSearch(_queryFlow.value, lat, lon)
                } else {
                    val uiClubs = clubs.map { club -> club.toHighContrastUiState(lat, lon) }
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            clubResults = uiClubs,
                            regionResults = regions
                        )
                    }
                }
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        _queryFlow.value = newQuery
    }

    fun onFilterChanged(newFilter: SearchFilterType) {
        _uiState.update { it.copy(activeFilter = newFilter) }
        _filterFlow.value = newFilter
    }

    fun selectRegion(regionName: String?) {
        _uiState.update {
            val newRegion = if (it.selectedRegion == regionName) null else regionName
            it.copy(selectedRegion = newRegion)
        }
        _queryFlow.value = _uiState.value.searchQuery
    }

    fun selectGenre(genre: String?) {
        _uiState.update {
            val newGenre = if (it.selectedGenre == genre) null else genre
            it.copy(selectedGenre = genre)
        }
        _queryFlow.value = _uiState.value.searchQuery
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedRegion = null,
                selectedGenre = null,
                errorMessage = null
            )
        }
        _queryFlow.value = ""
    }

    fun setUserLocation(latitude: Double, longitude: Double) {
        _uiState.update {
            it.copy(
                userLatitude = latitude,
                userLongitude = longitude,
                isGpsActive = true
            )
        }
        _locationFlow.value = Pair(latitude, longitude)
    }

    fun toggleFavorite(clubId: String, currentFavorite: Boolean) {
        viewModelScope.launch {
            clubRepository.toggleFavorite(clubId, currentFavorite)
        }
    }

    private fun executeExternalSearch(query: String, userLat: Double?, userLon: Double?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = clubRepository.searchExternalClubs(query, userLat, userLon)
            result.onSuccess { externalClubs ->
                val uiClubs = externalClubs.map { it.toHighContrastUiState(userLat, userLon) }
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        clubResults = uiClubs
                    )
                }
            }.onFailure { error ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage
                    )
                }
            }
        }
    }
}
