package com.kliq.app.data.model

enum class GeofenceTransitionType {
    ENTER,
    EXIT,
    DWELL,
    UNKNOWN
}

data class GeofenceTransitionEvent(
    val clubId: String,
    val transitionType: GeofenceTransitionType,
    val timestamp: Long = System.currentTimeMillis()
)

data class ClubGeofenceState(
    val activeClubId: String? = null,
    val activeClubName: String? = null,
    val isInsideGeofence: Boolean = false,
    val entryTimestamp: Long? = null,
    val activeGeofenceCount: Int = 0,
    val verifiedClubId: String? = null
)

data class VisitedClubHistory(
    val id: String = java.util.UUID.randomUUID().toString(),
    val clubId: String,
    val clubName: String,
    val entryTimestamp: Long,
    val exitTimestamp: Long? = null,
    val isVerifiedVisit: Boolean = true
)
