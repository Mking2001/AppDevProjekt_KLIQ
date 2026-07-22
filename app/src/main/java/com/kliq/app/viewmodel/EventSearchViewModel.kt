package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.EventRepository
import com.kliq.app.ui.model.ClubHighContrastItemState
import com.kliq.app.ui.model.EventHighContrastItemState
import com.kliq.app.ui.model.toHighContrastUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventSearchUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val clubs: List<ClubHighContrastItemState> = emptyList(),
    val events: List<EventHighContrastItemState> = emptyList(),
    val userLatitude: Double? = null,
    val userLongitude: Double? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class EventSearchViewModel @Inject constructor(
    private val clubRepository: ClubRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventSearchUiState())
    val uiState: StateFlow<EventSearchUiState> = _uiState.asStateFlow()

    init {
        observeLocalData()
    }

    private fun observeLocalData() {
        viewModelScope.launch {
            combine(
                clubRepository.getAllClubs(),
                eventRepository.getAllEvents()
            ) { clubsList, eventsList ->
                val lat = _uiState.value.userLatitude
                val lon = _uiState.value.userLongitude

                val clubUiStates = clubsList.map { it.toHighContrastUiState(lat, lon) }
                val eventUiStates = eventsList.map { it.toHighContrastUiState() }

                Pair(clubUiStates, eventUiStates)
            }
            .catch { throwable ->
                _uiState.update { it.copy(errorMessage = throwable.localizedMessage) }
            }
            .collect { (clubUiStates, eventUiStates) ->
                _uiState.update {
                    it.copy(clubs = clubUiStates, events = eventUiStates)
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.length >= 2) {
            executeSearch(query)
        }
    }

    fun setUserLocation(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(userLatitude = latitude, userLongitude = longitude) }
    }

    fun toggleClubFavorite(clubId: String, currentFavorite: Boolean) {
        viewModelScope.launch {
            clubRepository.toggleFavorite(clubId, currentFavorite)
        }
    }

    private fun executeSearch(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val lat = _uiState.value.userLatitude
            val lon = _uiState.value.userLongitude

            val result = clubRepository.searchExternalClubs(query, lat, lon)
            result.onSuccess { externalClubs ->
                val highContrastClubs = externalClubs.map { it.toHighContrastUiState(lat, lon) }
                _uiState.update {
                    it.copy(isLoading = false, clubs = highContrastClubs)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.localizedMessage ?: "Suche fehlgeschlagen")
                }
            }
        }
    }
}
