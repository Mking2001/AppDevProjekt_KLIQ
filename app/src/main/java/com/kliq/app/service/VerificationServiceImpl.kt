package com.kliq.app.service

import com.kliq.app.data.model.AntiSpamVerificationResult
import com.kliq.app.data.model.ReviewVerificationMethod
import com.kliq.app.data.repository.GeofenceRepository
import com.kliq.app.data.util.AntiSpamReviewValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VerificationServiceImpl @Inject constructor(
    private val geofenceRepository: GeofenceRepository,
    private val antiSpamValidator: AntiSpamReviewValidator,
    private val hapticFeedbackManager: com.kliq.app.util.HapticFeedbackManager? = null
) : VerificationService {

    override suspend fun verifyUserProximityOrQr(
        reviewerUserId: String,
        targetUserId: String,
        qrScanToken: String?
    ): AntiSpamVerificationResult {
        if (!qrScanToken.isNullOfBlankToken()) {
            val qrResult = verifyQrScanToken(reviewerUserId, targetUserId, qrScanToken!!)
            if (qrResult.isVerified) {
                hapticFeedbackManager?.performConfirm("Friend QR Code Verification")
                return qrResult
            }
        }
        return verifyGpsLocationMatch(reviewerUserId, targetUserId)
    }

    override suspend fun verifyGpsLocationMatch(
        reviewerUserId: String,
        targetUserId: String
    ): AntiSpamVerificationResult {
        val activeState = geofenceRepository.activeClubState.value
        val history = geofenceRepository.visitedHistory.value

        if (activeState.isInsideGeofence && activeState.activeClubId != null) {
            return AntiSpamVerificationResult(
                isVerified = true,
                method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
                confidenceScore = 1.0f,
                verificationDetails = "GPS-Geofence verifiziert: Beide Nutzer befinden sich am selben Standort (${activeState.activeClubName ?: activeState.activeClubId})"
            )
        }

        val verifiedVisits = history.filter { it.isVerifiedVisit }
        if (verifiedVisits.isNotEmpty()) {
            val latestVisit = verifiedVisits.first()
            return AntiSpamVerificationResult(
                isVerified = true,
                method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
                confidenceScore = 0.9f,
                verificationDetails = "GPS-Geofence-Historie verifiziert: Gemeinsamer Aufenthalt in ${latestVisit.clubName}"
            )
        }

        return AntiSpamVerificationResult(
            isVerified = false,
            method = ReviewVerificationMethod.UNVERIFIED,
            confidenceScore = 0.0f,
            verificationDetails = "Keine physische Nähe oder gemeinsame Geofence-Historie erkannt."
        )
    }

    override suspend fun verifyQrScanToken(
        reviewerUserId: String,
        targetUserId: String,
        qrToken: String
    ): AntiSpamVerificationResult {
        return antiSpamValidator.validateQrCodeScanToken(qrToken, targetUserId)
    }

    override fun observeVerificationStatus(
        reviewerUserId: String,
        targetUserId: String
    ): Flow<AntiSpamVerificationResult> {
        return combine(
            geofenceRepository.activeClubState,
            geofenceRepository.visitedHistory
        ) { activeState, history ->
            if (activeState.isInsideGeofence && activeState.activeClubId != null) {
                AntiSpamVerificationResult(
                    isVerified = true,
                    method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
                    confidenceScore = 1.0f,
                    verificationDetails = "Standort verifiziert via Geofence: ${activeState.activeClubName ?: activeState.activeClubId}"
                )
            } else if (history.any { it.isVerifiedVisit }) {
                val visit = history.first { it.isVerifiedVisit }
                AntiSpamVerificationResult(
                    isVerified = true,
                    method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
                    confidenceScore = 0.9f,
                    verificationDetails = "Geofence-Historie verifiziert: ${visit.clubName}"
                )
            } else {
                AntiSpamVerificationResult(
                    isVerified = false,
                    method = ReviewVerificationMethod.UNVERIFIED,
                    confidenceScore = 0.0f,
                    verificationDetails = "Bewertung gesperrt: Weder physische Nähe noch QR-Scan vorhanden."
                )
            }
        }
    }

    private fun String?.isNullOfBlankToken(): Boolean {
        return this.isNullOrBlank() || this == "null"
    }
}
