package com.kliq.app.data.util

import com.kliq.app.data.model.ReviewVerificationMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AntiSpamReviewValidatorTest {

    private val validator = AntiSpamReviewValidator()

    @Test
    fun testGpsGeofenceMatchValidation() {
        val clubLat = 52.5112
        val clubLon = 13.4432
        val radiusMeters = 300.0

        // User inside radius (approx 100m)
        val insideResult = validator.validateGpsLocationMatch(
            userLat = 52.5118,
            userLon = 13.4435,
            targetLat = clubLat,
            targetLon = clubLon,
            allowedRadiusMeters = radiusMeters
        )
        assertTrue(insideResult.isVerified)
        assertEquals(ReviewVerificationMethod.GPS_GEOFENCE_MATCH, insideResult.method)
        assertTrue(insideResult.confidenceScore > 0.8f)

        // User outside radius (approx 5km)
        val outsideResult = validator.validateGpsLocationMatch(
            userLat = 52.5400,
            userLon = 13.4000,
            targetLat = clubLat,
            targetLon = clubLon,
            allowedRadiusMeters = radiusMeters
        )
        assertFalse(outsideResult.isVerified)
        assertEquals(ReviewVerificationMethod.UNVERIFIED, outsideResult.method)
    }

    @Test
    fun testQrCodeScanTokenValidation() {
        val validResult = validator.validateQrCodeScanToken("KLIQ_PASS_BERLIN_2026", "club_berghain")
        assertTrue(validResult.isVerified)
        assertEquals(ReviewVerificationMethod.QR_CODE_SCAN, validResult.method)

        val invalidResult = validator.validateQrCodeScanToken("INVALID_TOKEN", "club_berghain")
        assertFalse(invalidResult.isVerified)
        assertEquals(ReviewVerificationMethod.UNVERIFIED, invalidResult.method)
    }

    @Test
    fun testRatingRangeValidation() {
        assertTrue(validator.isRatingValid(1))
        assertTrue(validator.isRatingValid(5))
        assertFalse(validator.isRatingValid(0))
        assertFalse(validator.isRatingValid(6))
    }
}
