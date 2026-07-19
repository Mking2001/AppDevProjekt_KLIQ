package com.kliq.app.ui.screens.club

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.Club
import com.kliq.app.data.model.ClubAnalytics
import com.kliq.app.data.model.EventInfo
import com.kliq.app.data.model.OperatingHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
class ClubViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ClubUiState())
    val uiState: StateFlow<ClubUiState> = _uiState.asStateFlow()

    fun loadClubDetails(clubId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            // Simuliere Netzwerkladezeit (Mock-Daten für die Validierung)
            delay(1000)
            
            if (clubId.isEmpty()) {
                _uiState.update { 
                    it.copy(isLoading = false, errorMessage = "Club ID ist ungültig.") 
                }
                return@launch
            }

            // Mock Data
            val mockClub = Club(
                id = clubId,
                name = "Berghain / Panorama Bar",
                category = "Techno",
                rating = 4.8f,
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
                    allHours = mapOf(
                        "Freitag" to "23:59 - 12:00",
                        "Samstag" to "23:59 - Open End",
                        "Sonntag" to "Open End"
                    )
                ),
                activeEvent = EventInfo(
                    title = "Klubnacht",
                    description = "Ben Klock, Marcel Dettmann, Steffi & more.",
                    price = "25€",
                    time = "Heute ab 23:59 Uhr"
                )
            )

            _uiState.update {
                it.copy(isLoading = false, club = mockClub)
            }
        }
    }

    fun toggleFavorite() {
        _uiState.update { state ->
            val updatedClub = state.club?.copy(isFavorite = !state.club.isFavorite)
            state.copy(club = updatedClub)
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
