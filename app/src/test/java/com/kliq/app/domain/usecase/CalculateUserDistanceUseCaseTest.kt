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
    fun calculateDistanceMeters_nearRange_returnsAccurateMeters() {
        // ~50m apart (0.00045 degrees lat difference at Berlin latitude)
        val dist50m = useCase.calculateDistanceMeters(52.52000, 13.40500, 52.52045, 13.40500)
        assertNotNull(dist50m)
        assertEquals(50.0, dist50m!!, 5.0)

        // ~500m apart (0.0045 degrees lat difference)
        val dist500m = useCase.calculateDistanceMeters(52.52000, 13.40500, 52.52450, 13.40500)
        assertNotNull(dist500m)
        assertEquals(500.0, dist500m!!, 20.0)
    }

    @Test
    fun calculateDistanceMeters_farRange_returnsAccurateKilometers() {
        // Berlin (52.5200, 13.4050) to Munich (48.1351, 11.5820) ~ 504 km
        val dist500km = useCase.calculateDistanceMeters(52.5200, 13.4050, 48.1351, 11.5820)
        assertNotNull(dist500km)
        assertEquals(504000.0, dist500km!!, 10000.0)
    }

    @Test
    fun calculateDistanceMeters_antipodalPoints_returnsHalfEarthCircumference() {
        // North Pole to South Pole ~ 20,015 km
        val poleDistance = useCase.calculateDistanceMeters(90.0, 0.0, -90.0, 0.0)
        assertNotNull(poleDistance)
        assertEquals(20015000.0, poleDistance!!, 50000.0)

        // Equator opposite points: (0.0, 0.0) to (0.0, 180.0)
        val equatorAntipode = useCase.calculateDistanceMeters(0.0, 0.0, 0.0, 180.0)
        assertNotNull(equatorAntipode)
        assertEquals(20015000.0, equatorAntipode!!, 50000.0)
    }

    @Test
    fun calculateDistanceMeters_meridianCrossing_calculatesShortestPath() {
        // Crossing 180th meridian (International Date Line)
        // Point A: (0.0, 179.9), Point B: (0.0, -179.9) -> 0.2 degrees apart (~22.2 km)
        val distCrossing = useCase.calculateDistanceMeters(0.0, 179.9, 0.0, -179.9)
        assertNotNull(distCrossing)
        assertEquals(22239.0, distCrossing!!, 500.0)
    }

    @Test
    fun calculateDistanceMeters_invalidAndExtremeCoordinates_returnsNull() {
        assertNull(useCase.calculateDistanceMeters(90.0001, 13.4050, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(-90.1, 13.4050, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(52.5200, 180.0001, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(52.5200, -180.0001, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(Double.NaN, 13.4050, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(52.5200, Double.POSITIVE_INFINITY, 52.5200, 13.4050))
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
