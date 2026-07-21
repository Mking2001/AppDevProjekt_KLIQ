package com.kliq.app.data.util

import com.kliq.app.data.model.AntiSpamVerificationResult
import com.kliq.app.data.model.ReviewVerificationMethod
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AntiSpamReviewValidator @Inject constructor() {

    fun validateGpsLocationMatch(
        userLat: Double,
        userLon: Double,
        targetLat: Double,
        targetLon: Double,
        allowedRadiusMeters: Double = 300.0
    ): AntiSpamVerificationResult {
        val distanceMeters = calculateDistanceMeters(userLat, userLon, targetLat, targetLon)
        val isMatch = distanceMeters <= allowedRadiusMeters

        return if (isMatch) {
            AntiSpamVerificationResult(
                isVerified = true,
                method = ReviewVerificationMethod.GPS_GEOFENCE_MATCH,
                confidenceScore = 1.0f - ((distanceMeters / allowedRadiusMeters) * 0.2f).toFloat(),
                verificationDetails = "GPS-Standort verifiziert (${distanceMeters.toInt()}m vom Ort entfernt)"
            )
        } else {
            AntiSpamVerificationResult(
                isVerified = false,
                method = ReviewVerificationMethod.UNVERIFIED,
                confidenceScore = 0.0f,
                verificationDetails = "Außerhalb des Geofencing-Radius (${distanceMeters.toInt()}m entfernt)"
            )
        }
    }

    fun validateQrCodeScanToken(
        qrToken: String,
        expectedEventIdOrClubId: String
    ): AntiSpamVerificationResult {
        val isValidToken = qrToken.isNotBlank() && (qrToken.contains(expectedEventIdOrClubId) || qrToken.startsWith("KLIQ_PASS_"))
        
        return if (isValidToken) {
            AntiSpamVerificationResult(
                isVerified = true,
                method = ReviewVerificationMethod.QR_CODE_SCAN,
                confidenceScore = 1.0f,
                verificationDetails = "QR-Code-Einlass-Pass verifiziert"
            )
        } else {
            AntiSpamVerificationResult(
                isVerified = false,
                method = ReviewVerificationMethod.UNVERIFIED,
                confidenceScore = 0.0f,
                verificationDetails = "Ungültiger QR-Code-Token"
            )
        }
    }

    fun isRatingValid(rating: Int): Boolean {
        return rating in 1..5
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}
