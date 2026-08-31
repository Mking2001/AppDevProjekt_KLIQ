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

import com.kliq.app.data.model.OccupancyCategory
import com.kliq.app.data.model.OccupancyTrend

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
            _uiState.update { it.copy(isLoading = true, clubId = clubId, errorMessage = null) }

            kotlinx.coroutines.flow.combine(
                clubRepository.getClubById(clubId),
                clubRepository.getClubGenderRatio(clubId)
            ) { club, ratio ->
                val isOpen = club?.operatingHours?.isOpenNow ?: false
                val segments = ClubAnalyticsUiState.createSegments(ratio)
                val visitorCount = ratio.totalVisitorsCount
                val maxCapacity = 1500

                if (!isOpen) {

                    ClubAnalyticsUiState(
                        isLoading = false,
                        clubId = clubId,
                        genderRatio = ratio,
                        segments = segments,
                        totalLiveVisitors = 0,
                        maxCapacity = maxCapacity,
                        currentCapacityPercent = 0,
                        occupancyCategory = OccupancyCategory.SCHWACH,
                        occupancyTrend = OccupancyTrend.STABLE,
                        isLive = false,
                        formattedLastUpdated = "Geschlossen • Keine Live-Daten außerhalb der Öffnungszeiten",
                        errorMessage = null
                    )
                } else {

                    val capacityPercent = if (visitorCount > 0) {
                        ((visitorCount.toFloat() / maxCapacity) * 100).toInt().coerceIn(0, 100)
                    } else {
                        0
                    }
                    val category = OccupancyCategory.fromPercentage(capacityPercent)

                    ClubAnalyticsUiState(
                        isLoading = false,
                        clubId = clubId,
                        genderRatio = ratio,
                        segments = segments,
                        totalLiveVisitors = visitorCount,
                        maxCapacity = maxCapacity,
                        currentCapacityPercent = capacityPercent,
                        occupancyCategory = category,
                        occupancyTrend = if (visitorCount > 0) OccupancyTrend.RISING else OccupancyTrend.STABLE,
                        isLive = true,
                        formattedLastUpdated = if (visitorCount > 0) "LIVE • Vor wenigen Sekunden" else "LIVE • Geöffnet (0 Check-ins)",
                        errorMessage = null
                    )
                }
            }
            .catch { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.localizedMessage ?: "Fehler beim Laden der Analytics-Daten"
                    )
                }
            }
            .collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateVisitorStats(
        capacityPercent: Int,
        totalVisitors: Int,
        maxCapacity: Int = 1500,
        trend: OccupancyTrend = OccupancyTrend.STABLE
    ) {
        val category = OccupancyCategory.fromPercentage(capacityPercent)
        _uiState.update {
            it.copy(
                currentCapacityPercent = capacityPercent,
                totalLiveVisitors = totalVisitors,
                maxCapacity = maxCapacity,
                occupancyCategory = category,
                occupancyTrend = trend,
                isLive = true,
                formattedLastUpdated = "LIVE • Vor wenigen Sekunden"
            )
        }
    }

    fun refreshAnalytics(clubId: String) {
        observeClubAnalytics(clubId)
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
