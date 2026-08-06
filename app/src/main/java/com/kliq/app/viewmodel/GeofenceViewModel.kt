package com.kliq.app.viewmodel

import androidx.lifecycle.viewModelScope
import com.kliq.app.data.model.GeofenceTransitionType
import com.kliq.app.data.model.VisitedClubHistory
import com.kliq.app.data.repository.ClubRepository
import com.kliq.app.data.repository.GeofenceRepository
import com.kliq.app.util.GeofenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GeofenceUiState(
    val activeClubId: String? = null,
    val activeClubName: String? = null,
    val isInsideGeofence: Boolean = false,
    val isReviewEnabled: Boolean = false,
    val verifiedClubId: String? = null,
    val entryTimestamp: Long? = null,
    val activeGeofenceCount: Int = 0,
    val registeredGeofencesCount: Int = 0,
    val visitedHistory: List<VisitedClubHistory> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class GeofenceViewModel @Inject constructor(
    private val geofenceRepository: GeofenceRepository,
    private val geofenceManager: GeofenceManager,
    private val clubRepository: ClubRepository,
    private val hapticFeedbackManager: com.kliq.app.util.HapticFeedbackManager? = null
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(GeofenceUiState())
    val uiState: StateFlow<GeofenceUiState> = _uiState.asStateFlow()

    init {
        observeGeofenceState()
    }

    private fun observeGeofenceState() {
        combine(
            geofenceRepository.activeClubState,
            geofenceRepository.visitedHistory
        ) { activeState, history ->
            _uiState.update { current ->
                current.copy(
                    activeClubId = activeState.activeClubId,
                    activeClubName = activeState.activeClubName,
                    isInsideGeofence = activeState.isInsideGeofence,
                    isReviewEnabled = activeState.isInsideGeofence && activeState.activeClubId != null,
                    verifiedClubId = activeState.verifiedClubId,
                    entryTimestamp = activeState.entryTimestamp,
                    activeGeofenceCount = activeState.activeGeofenceCount,
                    visitedHistory = history
                )
            }
        }.launchIn(viewModelScope)
    }

    fun syncGeofencesForLocation(userLat: Double, userLon: Double, maxGeofences: Int = 50) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val clubs = clubRepository.getAllClubs().firstOrNull() ?: emptyList()
            
            val result = geofenceManager.updateGeofencesForLocation(userLat, userLon, clubs, maxGeofences)
            result.onSuccess { count ->
                geofenceRepository.updateRegisteredGeofenceCount(count)
                _uiState.update { it.copy(isLoading = false, registeredGeofencesCount = count) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Geofence-Synchronisation fehlgeschlagen") }
            }
        }
    }

    fun simulateGeofenceEnter(clubId: String) {
        viewModelScope.launch {
            hapticFeedbackManager?.performConfirm("Simulated Geofence entry / Location match")
            geofenceRepository.handleGeofenceTransition(clubId, GeofenceTransitionType.ENTER)
        }
    }

    fun simulateGeofenceExit(clubId: String) {
        viewModelScope.launch {
            geofenceRepository.handleGeofenceTransition(clubId, GeofenceTransitionType.EXIT)
        }
    }

    fun clearAllGeofences() {
        viewModelScope.launch {
            geofenceManager.clearAllGeofences()
            geofenceRepository.resetGeofenceState()
            _uiState.update { current ->
                current.copy(
                    registeredGeofencesCount = 0,
                    activeGeofenceCount = 0,
                    isInsideGeofence = false,
                    isReviewEnabled = false,
                    activeClubId = null
                )
            }
        }
    }
}
