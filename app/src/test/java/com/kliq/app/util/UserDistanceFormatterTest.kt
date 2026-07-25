package com.kliq.app.util

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

class UserDistanceFormatterTest {

    private lateinit var formatter: UserDistanceFormatter

    @Before
    fun setUp() {
        formatter = UserDistanceFormatter(Locale.US)
    }

    @Test
    fun formatDistance_under1000Meters_formatsAsMeters() {
        assertEquals("150 m", formatter.formatDistance(150.4))
        assertEquals("0 m", formatter.formatDistance(0.0))
        assertEquals("851 m", formatter.formatDistance(850.7))
    }

    @Test
    fun formatDistance_1000MetersAndAbove_formatsAsKilometers() {
        assertEquals("1.2 km", formatter.formatDistance(1200.0))
        assertEquals("15.4 km", formatter.formatDistance(15400.0))
        assertEquals("1.0 km", formatter.formatDistance(1000.0))
    }

    @Test
    fun formatDistance_nullOrInvalid_returnsFallbackText() {
        assertEquals("Entfernung unbekannt", formatter.formatDistance(null))
        assertEquals("Entfernung unbekannt", formatter.formatDistance(Double.NaN))
        assertEquals("Entfernung unbekannt", formatter.formatDistance(-5.0))
    }

    @Test
    fun formatDistance_customFallback_returnsCustomLabel() {
        val customLabel = "Standort offline"
        assertEquals(customLabel, formatter.formatDistance(null, fallbackLabel = customLabel))
    }

    @Test
    fun formatDistanceBadge_validDistance_includesLocationPinPrefix() {
        assertEquals("📍 150 m", formatter.formatDistanceBadge(150.0))
        assertEquals("📍 2.5 km", formatter.formatDistanceBadge(2500.0))
        assertEquals("Entfernung unbekannt", formatter.formatDistanceBadge(null))
    }

    @Test
    fun formatDistanceWithSuffix_validDistance_appendsSuffix() {
        assertEquals("150 m entfernt", formatter.formatDistanceWithSuffix(150.0))
        assertEquals("1.2 km entfernt", formatter.formatDistanceWithSuffix(1200.0))
        assertEquals("Entfernung unbekannt", formatter.formatDistanceWithSuffix(null))
    }
}
