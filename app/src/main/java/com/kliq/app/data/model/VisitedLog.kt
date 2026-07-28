package com.kliq.app.data.model

/**
 * Domain model representing a historical club visit by a user.
 */
data class VisitedLog(
    val id: String,
    val userId: String,
    val clubId: String,
    val clubName: String,
    val visitedAtTimestamp: Long,
    val isVerifiedByGps: Boolean
)
