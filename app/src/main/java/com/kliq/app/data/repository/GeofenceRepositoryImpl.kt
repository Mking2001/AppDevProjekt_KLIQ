package com.kliq.app.data.repository

import com.kliq.app.data.model.ClubGeofenceState
import com.kliq.app.data.model.GeofenceTransitionType
import com.kliq.app.data.model.VisitedClubHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceRepositoryImpl @Inject constructor(
    private val clubRepository: ClubRepository
) : GeofenceRepository {

    private val _activeClubState = MutableStateFlow(ClubGeofenceState())
    override val activeClubState: StateFlow<ClubGeofenceState> = _activeClubState.asStateFlow()

    private val _visitedHistory = MutableStateFlow<List<VisitedClubHistory>>(emptyList())
    override val visitedHistory: StateFlow<List<VisitedClubHistory>> = _visitedHistory.asStateFlow()

    override suspend fun handleGeofenceTransition(clubId: String, transitionType: GeofenceTransitionType) {
        val now = System.currentTimeMillis()
        val club = clubRepository.getClubById(clubId).firstOrNull()
        val clubName = club?.name ?: "Club #$clubId"

        when (transitionType) {
            GeofenceTransitionType.ENTER -> {
                _activeClubState.update { currentState ->
                    currentState.copy(
                        activeClubId = clubId,
                        activeClubName = clubName,
                        isInsideGeofence = true,
                        entryTimestamp = now,
                        verifiedClubId = clubId
                    )
                }

                val newVisit = VisitedClubHistory(
                    clubId = clubId,
                    clubName = clubName,
                    entryTimestamp = now,
                    isVerifiedVisit = true
                )

                _visitedHistory.update { history ->
                    listOf(newVisit) + history.filterNot { it.clubId == clubId && it.exitTimestamp == null }
                }
            }

            GeofenceTransitionType.EXIT -> {
                val currentActiveId = _activeClubState.value.activeClubId
                if (currentActiveId == clubId) {
                    _activeClubState.update { currentState ->
                        currentState.copy(
                            activeClubId = null,
                            activeClubName = null,
                            isInsideGeofence = false,
                            entryTimestamp = null
                        )
                    }
                }

                _visitedHistory.update { history ->
                    history.map { visit ->
                        if (visit.clubId == clubId && visit.exitTimestamp == null) {
                            visit.copy(exitTimestamp = now)
                        } else {
                            visit
                        }
                    }
                }
            }

            GeofenceTransitionType.DWELL, GeofenceTransitionType.UNKNOWN -> {
                // Handle dwell or unknown transitions silently
            }
        }
    }

    override fun isClubGeofenceVerified(clubId: String): Boolean {
        val state = _activeClubState.value
        return state.isInsideGeofence && (state.activeClubId == clubId || state.verifiedClubId == clubId)
    }

    override suspend fun getVisitedHistoryForUser(): List<VisitedClubHistory> {
        return _visitedHistory.value
    }

    override suspend fun updateRegisteredGeofenceCount(count: Int) {
        _activeClubState.update { it.copy(activeGeofenceCount = count) }
    }

    override suspend fun resetGeofenceState() {
        _activeClubState.value = ClubGeofenceState()
        _visitedHistory.value = emptyList()
    }
}
