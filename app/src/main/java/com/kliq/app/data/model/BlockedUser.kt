package com.kliq.app.data.model

data class BlockedUser(
    val userId: String,
    val blockedUserId: String,
    val reason: String? = null,
    val blockedAtTimestampMs: Long = System.currentTimeMillis()
)
