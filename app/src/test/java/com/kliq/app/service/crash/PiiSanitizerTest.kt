package com.kliq.app.service.crash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit-Tests zur Verifizierung des PII-Datenschutzes (Personally Identifiable Information)
 * im [PiiSanitizer] (Kapitel 9.4).
 */
class PiiSanitizerTest {

    @Test
    fun testSanitize_phoneNumbers_redacted() {
        val rawMessage = "User registered with phone +491512345678 and alternative 0151-9876543"
        val sanitized = PiiSanitizer.sanitize(rawMessage)

        assertFalse(sanitized.contains("+491512345678"))
        assertFalse(sanitized.contains("0151-9876543"))
        assertTrue(sanitized.contains("[REDACTED_PHONE]"))
    }

    @Test
    fun testSanitize_gpsCoordinates_redacted() {
        val rawMessage = "Location event triggered at lat=52.520008, lng=13.404954"
        val sanitized = PiiSanitizer.sanitize(rawMessage)

        assertFalse(sanitized.contains("52.520008"))
        assertFalse(sanitized.contains("13.404954"))
        assertTrue(sanitized.contains("[REDACTED_GPS]"))
    }

    @Test
    fun testSanitize_emailAddresses_redacted() {
        val rawMessage = "Login failed for account alex.nightlife@kliq-app.de"
        val sanitized = PiiSanitizer.sanitize(rawMessage)

        assertFalse(sanitized.contains("alex.nightlife@kliq-app.de"))
        assertTrue(sanitized.contains("[REDACTED_EMAIL]"))
    }

    @Test
    fun testSanitize_authTokens_redacted() {
        val rawMessage = "HTTP 401 Unauthorized for token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val sanitized = PiiSanitizer.sanitize(rawMessage)

        assertFalse(sanitized.contains("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"))
        assertTrue(sanitized.contains("[REDACTED_TOKEN]"))
    }

    @Test
    fun testAnonymizeUserId_generatesConsistentHash() {
        val rawUserId = "user_kliq_12345_berlin"
        val anonId = PiiSanitizer.anonymizeUserId(rawUserId)

        assertFalse(anonId.contains(rawUserId))
        assertTrue(anonId.startsWith("user_anon_"))
        assertEquals(anonId, PiiSanitizer.anonymizeUserId(rawUserId))
    }

    @Test
    fun testSanitizeKeyValue_sanitizesKeyAndValue() {
        val (key, value) = PiiSanitizer.sanitizeKeyValue("CurrentScreen ", "User at lat=52.5112, lng=13.4430")

        assertEquals("currentscreen", key)
        assertTrue(value.contains("[REDACTED_GPS]"))
        assertFalse(value.contains("52.5112"))
    }
}
