package com.kliq.app.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kliq.app.data.model.VisitedLog

@Entity(
    tableName = "visited_logs",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["clubId"])
    ]
)
data class VisitedLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val clubId: String,
    val clubName: String,
    val visitedAtTimestamp: Long,
    val isVerifiedByGps: Boolean
)

fun VisitedLogEntity.toDomain(): VisitedLog = VisitedLog(
    id = id,
    userId = userId,
    clubId = clubId,
    clubName = clubName,
    visitedAtTimestamp = visitedAtTimestamp,
    isVerifiedByGps = isVerifiedByGps
)

fun VisitedLog.toEntity(): VisitedLogEntity = VisitedLogEntity(
    id = id,
    userId = userId,
    clubId = clubId,
    clubName = clubName,
    visitedAtTimestamp = visitedAtTimestamp,
    isVerifiedByGps = isVerifiedByGps
)
