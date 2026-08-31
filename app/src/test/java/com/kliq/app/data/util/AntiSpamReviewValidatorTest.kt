package com.kliq.app.data.util

import com.kliq.app.data.model.ReviewVerificationMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AntiSpamReviewValidatorTest {

    private val validator = AntiSpamReviewValidator()

    @Test
    fun testGpsGeofenceMatchValidation_insideAndOutsideRadius() {
        val clubLat = 52.5112
        val clubLon = 13.4432
        val radiusMeters = 300.0

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

        val outsideResult = validator.validateGpsLocationMatch(
            userLat = 52.5400,
            userLon = 13.4000,
            targetLat = clubLat,
            targetLon = clubLon,
            allowedRadiusMeters = radiusMeters
        )
        assertFalse(outsideResult.isVerified)
        assertEquals(ReviewVerificationMethod.UNVERIFIED, outsideResult.method)
        assertEquals(0.0f, outsideResult.confidenceScore, 0.001f)
    }

    @Test
    fun testGpsGeofenceMatchValidation_boundaryAndExactCenter() {
        val clubLat = 52.5112
        val clubLon = 13.4432

        val centerResult = validator.validateGpsLocationMatch(
            userLat = clubLat,
            userLon = clubLon,
            targetLat = clubLat,
            targetLon = clubLon,
            allowedRadiusMeters = 200.0
        )
        assertTrue(centerResult.isVerified)
        assertEquals(1.0f, centerResult.confidenceScore, 0.01f)

        val tightRadiusResult = validator.validateGpsLocationMatch(
            userLat = 52.5118,
            userLon = 13.4435,
            targetLat = clubLat,
            targetLon = clubLon,
            allowedRadiusMeters = 50.0
        )
        assertFalse(tightRadiusResult.isVerified)
    }

    @Test
    fun testQrCodeScanTokenValidation() {
        val validResult = validator.validateQrCodeScanToken("KLIQ_PASS_BERLIN_2026", "club_berghain")
        assertTrue(validResult.isVerified)
        assertEquals(ReviewVerificationMethod.QR_CODE_SCAN, validResult.method)
        assertEquals(1.0f, validResult.confidenceScore, 0.001f)

        val validClubSpecificResult = validator.validateQrCodeScanToken("ENTRY_club_berghain_TICKET", "club_berghain")
        assertTrue(validClubSpecificResult.isVerified)

        val invalidResult = validator.validateQrCodeScanToken("INVALID_TOKEN", "club_berghain")
        assertFalse(invalidResult.isVerified)
        assertEquals(ReviewVerificationMethod.UNVERIFIED, invalidResult.method)

        val emptyTokenResult = validator.validateQrCodeScanToken("", "club_berghain")
        assertFalse(emptyTokenResult.isVerified)
    }

    @Test
    fun testRatingRangeValidation() {
        assertTrue(validator.isRatingValid(1))
        assertTrue(validator.isRatingValid(3))
        assertTrue(validator.isRatingValid(5))
        assertFalse(validator.isRatingValid(0))
        assertFalse(validator.isRatingValid(-1))
        assertFalse(validator.isRatingValid(6))
        assertFalse(validator.isRatingValid(10))
    }
}
