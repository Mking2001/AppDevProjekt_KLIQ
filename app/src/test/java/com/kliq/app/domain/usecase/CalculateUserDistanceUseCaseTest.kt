package com.kliq.app.domain.usecase

import com.kliq.app.data.model.LocationData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculateUserDistanceUseCaseTest {

    private lateinit var useCase: CalculateUserDistanceUseCase

    @Before
    fun setUp() {
        useCase = CalculateUserDistanceUseCase()
    }

    @Test
    fun calculateDistanceMeters_knownCoordinates_returnsAccurateDistance() {
        // Brandenburg Gate: 52.516275, 13.377704
        // Alexanderplatz: 52.521918, 13.413215
        val distMeters = useCase.calculateDistanceMeters(
            startLat = 52.516275,
            startLng = 13.377704,
            endLat = 52.521918,
            endLng = 13.413215
        )

        assertNotNull(distMeters)
        // Distance is ~2480 meters (2.48 km)
        assertEquals(2480.0, distMeters!!, 100.0)
    }

    @Test
    fun calculateDistanceMeters_identicalCoordinates_returnsZero() {
        val distMeters = useCase.calculateDistanceMeters(
            startLat = 52.5200,
            startLng = 13.4050,
            endLat = 52.5200,
            endLng = 13.4050
        )

        assertNotNull(distMeters)
        assertEquals(0.0, distMeters!!, 0.001)
    }

    @Test
    fun calculateDistanceMeters_invalidCoordinates_returnsNull() {
        val result1 = useCase.calculateDistanceMeters(100.0, 13.4050, 52.5200, 13.4050)
        val result2 = useCase.calculateDistanceMeters(Double.NaN, 13.4050, 52.5200, 13.4050)
        val result3 = useCase.calculateDistanceMeters(52.5200, 200.0, 52.5200, 13.4050)

        assertNull(result1)
        assertNull(result2)
        assertNull(result3)
    }

    @Test
    fun calculateDistanceMeters_locationDataObjects_calculatesCorrectly() {
        val loc1 = LocationData(latitude = 52.516275, longitude = 13.377704)
        val loc2 = LocationData(latitude = 52.521918, longitude = 13.413215)

        val dist = useCase.calculateDistanceMeters(loc1, loc2)
        assertNotNull(dist)
        assertEquals(2480.0, dist!!, 100.0)
    }

    @Test
    fun calculateDistanceMeters_nullLocationData_returnsNull() {
        val loc1 = LocationData(latitude = 52.516275, longitude = 13.377704)
        assertNull(useCase.calculateDistanceMeters(loc1, null))
        assertNull(useCase.calculateDistanceMeters(null, loc1))
    }

    @Test
    fun calculateUserDistance_validCoordinates_returnsValidResult() {
        val result = useCase.calculateUserDistance(
            targetUserId = "user_123",
            currentUserLat = 52.5200,
            currentUserLng = 13.4050,
            targetUserLat = 52.5210,
            targetUserLng = 13.4060
        )

        assertEquals("user_123", result.userId)
        assertTrue(result.isValid)
        assertNotNull(result.rawDistanceMeters)
        assertTrue(result.rawDistanceMeters!! > 0)
    }

    @Test
    fun calculateUserDistance_missingCoordinates_returnsInvalidResult() {
        val result = useCase.calculateUserDistance(
            targetUserId = "user_456",
            currentUserLat = null,
            currentUserLng = 13.4050,
            targetUserLat = 52.5210,
            targetUserLng = 13.4060
        )

        assertEquals("user_456", result.userId)
        assertFalse(result.isValid)
        assertNull(result.rawDistanceMeters)
    }
}
