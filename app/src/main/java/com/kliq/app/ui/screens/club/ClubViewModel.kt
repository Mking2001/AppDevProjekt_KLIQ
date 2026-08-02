package com.kliq.app.ui.screens.club

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.ClubAnalytics
import com.kliq.app.data.model.Event
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.OperatingHours
import com.kliq.app.data.repository.ClubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClubUiState(
    val isLoading: Boolean = true,
    val club: Club? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ClubViewModel @Inject constructor(
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubUiState())
    val uiState: StateFlow<ClubUiState> = _uiState.asStateFlow()

    fun loadClubDetails(clubId: String) {
        if (clubId.isEmpty()) {
            _uiState.update { 
                it.copy(isLoading = false, errorMessage = "Club ID ist ungültig.") 
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            clubRepository.getClubById(clubId).collect { repositoryClub ->
                if (repositoryClub != null) {
                    _uiState.update {
                        it.copy(isLoading = false, club = repositoryClub)
                    }
                } else {
                    val fallbackClub = getFallbackClub(clubId)
                    _uiState.update {
                        it.copy(isLoading = false, club = fallbackClub)
                    }
                }
            }
        }
    }

    fun toggleFavorite() {
        val currentClub = _uiState.value.club ?: return
        val newFavoriteState = !currentClub.isFavorite
        _uiState.update { state ->
            state.copy(club = currentClub.copy(isFavorite = newFavoriteState))
        }
        viewModelScope.launch {
            clubRepository.toggleFavorite(currentClub.id, currentClub.isFavorite)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun getFallbackClub(clubId: String): Club {
        return Club(
            id = clubId,
            name = "Berghain / Panorama Bar",
            location = GpsLocation(52.5112, 13.4432, "Am Wriezener Bahnhof, 10243 Berlin"),
            geofenceRadiusMeters = 300.0,
            averageRating = 4.8,
            category = "Techno",
            imageUrl = "https://via.placeholder.com/600x400/120021/8F00FF?text=Berghain",
            region = "Berlin",
            isFavorite = false,
            analytics = ClubAnalytics(
                currentCapacityPercent = 85,
                malePercentage = 55,
                femalePercentage = 45,
                totalLiveVisitors = 1420
            ),
            operatingHours = OperatingHours(
                isOpenNow = true,
                todayHours = "23:59 - 12:00",
                weeklySchedule = mapOf(
                    "Freitag" to "23:59 - 12:00",
                    "Samstag" to "23:59 - Open End",
                    "Sonntag" to "Open End"
                )
            ),
            activeEvent = Event(
                id = "ev_klubnacht",
                clubId = clubId,
                title = "Klubnacht",
                description = "Ben Klock, Marcel Dettmann, Steffi & more.",
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis() + 43200000L,
                price = "25€"
            )
        )
    }
}
