package com.kliq.app.service

import com.kliq.app.data.model.AntiSpamVerificationResult
import kotlinx.coroutines.flow.Flow

interface VerificationService {
    suspend fun verifyUserProximityOrQr(
        reviewerUserId: String,
        targetUserId: String,
        qrScanToken: String? = null
    ): AntiSpamVerificationResult

    suspend fun verifyGpsLocationMatch(
        reviewerUserId: String,
        targetUserId: String
    ): AntiSpamVerificationResult

    suspend fun verifyQrScanToken(
        reviewerUserId: String,
        targetUserId: String,
        qrToken: String
    ): AntiSpamVerificationResult

    fun observeVerificationStatus(
        reviewerUserId: String,
        targetUserId: String
    ): Flow<AntiSpamVerificationResult>
}
