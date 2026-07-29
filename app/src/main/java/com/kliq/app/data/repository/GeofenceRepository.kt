package com.kliq.app.data.repository

import com.kliq.app.data.model.ClubGeofenceState
import com.kliq.app.data.model.GeofenceTransitionType
import com.kliq.app.data.model.VisitedClubHistory
import kotlinx.coroutines.flow.StateFlow

interface GeofenceRepository {
    val activeClubState: StateFlow<ClubGeofenceState>
    val visitedHistory: StateFlow<List<VisitedClubHistory>>

    suspend fun handleGeofenceTransition(clubId: String, transitionType: GeofenceTransitionType)
    fun isClubGeofenceVerified(clubId: String): Boolean
    suspend fun getVisitedHistoryForUser(): List<VisitedClubHistory>
    suspend fun updateRegisteredGeofenceCount(count: Int)
    suspend fun resetGeofenceState()
}
