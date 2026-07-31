package com.kliq.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kliq.app.data.repository.ClubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for managing and aggregating club visitor analytics and gender ratios.
 */
@HiltViewModel
class ClubAnalyticsViewModel @Inject constructor(
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubAnalyticsUiState())
    val uiState: StateFlow<ClubAnalyticsUiState> = _uiState.asStateFlow()

    fun observeClubAnalytics(clubId: String) {
        if (clubId.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Ungültige Club ID"
                )
            }
            return
        }

        viewModelScope.launch {
            clubRepository.getClubGenderRatio(clubId)
                .onStart {
                    _uiState.update { it.copy(isLoading = true, clubId = clubId, errorMessage = null) }
                }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.localizedMessage ?: "Fehler beim Laden der Analytics-Daten"
                        )
                    }
                }
                .collect { ratio ->
                    val segments = ClubAnalyticsUiState.createSegments(ratio)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            clubId = clubId,
                            genderRatio = ratio,
                            segments = segments,
                            totalLiveVisitors = ratio.totalVisitorsCount,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun refreshAnalytics(clubId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                val ratio = clubRepository.calculateClubGenderRatio(clubId)
                val segments = ClubAnalyticsUiState.createSegments(ratio)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        clubId = clubId,
                        genderRatio = ratio,
                        segments = segments,
                        totalLiveVisitors = ratio.totalVisitorsCount,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Fehler beim Aktualisieren der Daten"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
