package com.kliq.app.domain.usecase

import com.kliq.app.data.model.Club
import com.kliq.app.data.model.ClubAnalytics
import com.kliq.app.data.model.GpsLocation
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.OperatingHours
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

    // =========================================================================
    // 1. DISTANCE CALCULATION TESTS (Haversine Formula & Geographic Boundaries)
    // =========================================================================

    @Test
    fun calculateDistanceMeters_knownLandmarkCoordinates_returnsAccurateDistance() {
        // Brandenburg Gate: 52.516275, 13.377704
        // Alexanderplatz: 52.521918, 13.413215
        val distMeters = useCase.calculateDistanceMeters(
            startLat = 52.516275,
            startLng = 13.377704,
            endLat = 52.521918,
            endLng = 13.413215
        )

        assertNotNull(distMeters)
        // Expected distance ~2480 meters (2.48 km) with 50m precision allowance
        assertEquals(2480.0, distMeters!!, 50.0)
    }

    @Test
    fun calculateDistanceMeters_identicalCoordinates_returnsZeroDistance() {
        val distMeters = useCase.calculateDistanceMeters(
            startLat = 52.5200,
            startLng = 13.4050,
            endLat = 52.5200,
            endLng = 13.4050
        )

        assertNotNull(distMeters)
        assertEquals(0.0, distMeters!!, 0.0001)
    }

    @Test
    fun calculateDistanceMeters_microDistancesUnderTenMeters_returnsAccurateDistance() {
        // Test 1.1 meter separation (~0.00001 degrees latitude difference)
        val dist1m = useCase.calculateDistanceMeters(52.520000, 13.405000, 52.520010, 13.405000)
        assertNotNull(dist1m)
        assertEquals(1.11, dist1m!!, 0.2)

        // Test 3.3 meter separation (~0.00003 degrees latitude difference)
        val dist3m = useCase.calculateDistanceMeters(52.520000, 13.405000, 52.520030, 13.405000)
        assertNotNull(dist3m)
        assertEquals(3.34, dist3m!!, 0.3)

        // Test 5.5 meter separation (~0.00005 degrees latitude difference)
        val dist5m = useCase.calculateDistanceMeters(52.520000, 13.405000, 52.520050, 13.405000)
        assertNotNull(dist5m)
        assertEquals(5.56, dist5m!!, 0.4)

        // Test 8.9 meter separation (~0.00008 degrees latitude difference)
        val dist8m = useCase.calculateDistanceMeters(52.520000, 13.405000, 52.520080, 13.405000)
        assertNotNull(dist8m)
        assertEquals(8.90, dist8m!!, 0.5)
    }

    @Test
    fun calculateDistanceMeters_intermediateDistances_returnsAccurateMeters() {
        // ~50m apart (0.00045 degrees lat difference at 52.52 N)
        val dist50m = useCase.calculateDistanceMeters(52.52000, 13.40500, 52.52045, 13.40500)
        assertNotNull(dist50m)
        assertEquals(50.0, dist50m!!, 5.0)

        // ~200m apart (0.0018 degrees lat difference)
        val dist200m = useCase.calculateDistanceMeters(52.52000, 13.40500, 52.52180, 13.40500)
        assertNotNull(dist200m)
        assertEquals(200.0, dist200m!!, 10.0)

        // ~500m apart (0.0045 degrees lat difference)
        val dist500m = useCase.calculateDistanceMeters(52.52000, 13.40500, 52.52450, 13.40500)
        assertNotNull(dist500m)
        assertEquals(500.0, dist500m!!, 20.0)
    }

    @Test
    fun calculateDistanceMeters_longContinentalDistances_returnsAccurateKilometers() {
        // Berlin (52.5200, 13.4050) to Munich (48.1351, 11.5820) ~ 504 km
        val distBerlinMunich = useCase.calculateDistanceMeters(52.5200, 13.4050, 48.1351, 11.5820)
        assertNotNull(distBerlinMunich)
        assertEquals(504000.0, distBerlinMunich!!, 5000.0)

        // Berlin (52.5200, 13.4050) to New York (40.7128, -74.0060) ~ 6385 km
        val distBerlinNewYork = useCase.calculateDistanceMeters(52.5200, 13.4050, 40.7128, -74.0060)
        assertNotNull(distBerlinNewYork)
        assertEquals(6385000.0, distBerlinNewYork!!, 20000.0)

        // Berlin (52.5200, 13.4050) to Tokyo (35.6762, 139.6503) ~ 8918 km
        val distBerlinTokyo = useCase.calculateDistanceMeters(52.5200, 13.4050, 35.6762, 139.6503)
        assertNotNull(distBerlinTokyo)
        assertEquals(8918000.0, distBerlinTokyo!!, 30000.0)
    }

    @Test
    fun calculateDistanceMeters_antipodalPoints_returnsHalfEarthCircumference() {
        // North Pole to South Pole: (90.0, 0.0) to (-90.0, 0.0) ~ 20,015 km
        val poleDistance = useCase.calculateDistanceMeters(90.0, 0.0, -90.0, 0.0)
        assertNotNull(poleDistance)
        assertEquals(20015000.0, poleDistance!!, 20000.0)

        // Equator opposite points: (0.0, 0.0) to (0.0, 180.0) ~ 20,015 km
        val equatorAntipode = useCase.calculateDistanceMeters(0.0, 0.0, 0.0, 180.0)
        assertNotNull(equatorAntipode)
        assertEquals(20015000.0, equatorAntipode!!, 20000.0)
    }

    @Test
    fun calculateDistanceMeters_meridianCrossing_calculatesShortestPath() {
        // International Date Line / 180th Meridian Crossing
        // Point A: (0.0, 179.9), Point B: (0.0, -179.9) -> 0.2 degrees difference (~22.2 km)
        val distCrossing = useCase.calculateDistanceMeters(0.0, 179.9, 0.0, -179.9)
        assertNotNull(distCrossing)
        assertEquals(22239.0, distCrossing!!, 500.0)
    }

    @Test
    fun calculateDistanceMeters_locationDataObjects_calculatesCorrectly() {
        val loc1 = LocationData(latitude = 52.516275, longitude = 13.377704)
        val loc2 = LocationData(latitude = 52.521918, longitude = 13.413215)

        val dist = useCase.calculateDistanceMeters(loc1, loc2)
        assertNotNull(dist)
        assertEquals(2480.0, dist!!, 50.0)
    }

    @Test
    fun calculateDistanceMeters_locationDataAndGpsLocation_calculatesCorrectly() {
        val loc1 = LocationData(latitude = 52.516275, longitude = 13.377704)
        val clubGps = GpsLocation(latitude = 52.521918, longitude = 13.413215, address = "Alexanderplatz")

        val dist = useCase.calculateDistanceMeters(loc1, clubGps)
        assertNotNull(dist)
        assertEquals(2480.0, dist!!, 50.0)
    }

    @Test
    fun calculateDistanceMeters_nullLocationObjects_returnsNull() {
        val loc1 = LocationData(latitude = 52.516275, longitude = 13.377704)
        assertNull(useCase.calculateDistanceMeters(loc1, null as LocationData?))
        assertNull(useCase.calculateDistanceMeters(null as LocationData?, loc1))
        assertNull(useCase.calculateDistanceMeters(loc1, null as GpsLocation?))
        assertNull(useCase.calculateDistanceMeters(null as LocationData?, null as GpsLocation?))
    }

    // =========================================================================
    // 2. GEOFENCE VERIFICATION TESTS (isWithinClubRadius & Tolerance Thresholds)
    // =========================================================================

    @Test
    fun isWithinClubRadius_userAtExactClubCenter_returnsTrue() {
        val clubLat = 52.5112
        val clubLng = 13.4432
        val radiusMeters = 200.0

        val isInside = useCase.isWithinClubRadius(
            userLat = clubLat,
            userLng = clubLng,
            clubLat = clubLat,
            clubLng = clubLng,
            radiusMeters = radiusMeters
        )

        assertTrue(isInside)
    }

    @Test
    fun isWithinClubRadius_userInsideRadius_returnsTrue() {
        val clubLat = 52.5112
        val clubLng = 13.4432
        val radiusMeters = 200.0

        // User ~50m away from club (0.00045 degrees lat diff)
        val userLat = 52.51165
        val userLng = 13.4432

        val isInside = useCase.isWithinClubRadius(
            userLat = userLat,
            userLng = userLng,
            clubLat = clubLat,
            clubLng = clubLng,
            radiusMeters = radiusMeters
        )

        assertTrue(isInside)
    }

    @Test
    fun isWithinClubRadius_userExactlyOnRadiusBoundary_returnsTrue() {
        val clubLat = 52.52000
        val clubLng = 13.40500
        // Calculate distance for ~200m offset
        val userLat = 52.52180
        val userLng = 13.40500
        val exactDist = useCase.calculateDistanceMeters(userLat, userLng, clubLat, clubLng)!!

        val isInside = useCase.isWithinClubRadius(
            userLat = userLat,
            userLng = userLng,
            clubLat = clubLat,
            clubLng = clubLng,
            radiusMeters = exactDist,
            toleranceMeters = 0.0
        )

        assertTrue(isInside)
    }

    @Test
    fun isWithinClubRadius_userOutsideRadiusWithoutTolerance_returnsFalse() {
        val clubLat = 52.5112
        val clubLng = 13.4432
        val radiusMeters = 200.0

        // User ~300m away (0.0027 degrees lat diff)
        val userLat = 52.5139
        val userLng = 13.4432

        val isInside = useCase.isWithinClubRadius(
            userLat = userLat,
            userLng = userLng,
            clubLat = clubLat,
            clubLng = clubLng,
            radiusMeters = radiusMeters,
            toleranceMeters = 0.0
        )

        assertFalse(isInside)
    }

    @Test
    fun isWithinClubRadius_userWithinToleranceMargin_returnsTrue() {
        val clubLat = 52.52000
        val clubLng = 13.40500
        val radiusMeters = 200.0
        val toleranceMeters = 15.0

        // User at ~205m distance (0.00185 degrees lat diff)
        val userLat = 52.52185
        val userLng = 13.40500

        val rawDist = useCase.calculateDistanceMeters(userLat, userLng, clubLat, clubLng)!!
        assertTrue(rawDist > radiusMeters)
        assertTrue(rawDist <= (radiusMeters + toleranceMeters))

        val isInsideWithTolerance = useCase.isWithinClubRadius(
            userLat = userLat,
            userLng = userLng,
            clubLat = clubLat,
            clubLng = clubLng,
            radiusMeters = radiusMeters,
            toleranceMeters = toleranceMeters
        )

        assertTrue(isInsideWithTolerance)
    }

    @Test
    fun isWithinClubRadius_userExceedingToleranceMargin_returnsFalse() {
        val clubLat = 52.52000
        val clubLng = 13.40500
        val radiusMeters = 200.0
        val toleranceMeters = 15.0

        // User at ~225m distance (0.00202 degrees lat diff)
        val userLat = 52.52202
        val userLng = 13.40500

        val rawDist = useCase.calculateDistanceMeters(userLat, userLng, clubLat, clubLng)!!
        assertTrue(rawDist > (radiusMeters + toleranceMeters))

        val isInside = useCase.isWithinClubRadius(
            userLat = userLat,
            userLng = userLng,
            clubLat = clubLat,
            clubLng = clubLng,
            radiusMeters = radiusMeters,
            toleranceMeters = toleranceMeters
        )

        assertFalse(isInside)
    }

    @Test
    fun isWithinClubRadius_withClubEntityAndLocationData_evaluatesCorrectly() {
        val club = Club(
            id = "club_matrix",
            name = "Matrix Club Berlin",
            location = GpsLocation(latitude = 52.5065, longitude = 13.4490, address = "Warschauer Str."),
            geofenceRadiusMeters = 250.0,
            averageRating = 4.5,
            operatingHours = OperatingHours(isOpenNow = true, todayHours = "22:00 - 06:00"),
            analytics = ClubAnalytics()
        )

        // User near club (~100m away)
        val userNear = LocationData(latitude = 52.5074, longitude = 13.4490)
        assertTrue(useCase.isWithinClubRadius(userNear, club))

        // User far from club (~2 km away)
        val userFar = LocationData(latitude = 52.5240, longitude = 13.4490)
        assertFalse(useCase.isWithinClubRadius(userFar, club))

        // Null checks
        assertFalse(useCase.isWithinClubRadius(null, club))
        assertFalse(useCase.isWithinClubRadius(userNear, null as Club?))
    }

    @Test
    fun isWithinClubRadius_withGpsLocationOverload_evaluatesCorrectly() {
        val clubGps = GpsLocation(latitude = 52.5065, longitude = 13.4490, address = "Warschauer Str.")
        val userLoc = LocationData(latitude = 52.5074, longitude = 13.4490)

        assertTrue(useCase.isWithinClubRadius(userLoc, clubGps, radiusMeters = 300.0))
        assertFalse(useCase.isWithinClubRadius(userLoc, clubGps, radiusMeters = 50.0))
        assertFalse(useCase.isWithinClubRadius(null, clubGps, radiusMeters = 300.0))
        assertFalse(useCase.isWithinClubRadius(userLoc, null as GpsLocation?, radiusMeters = 300.0))
    }

    // =========================================================================
    // 3. EDGE CASES & NUMERICAL ERROR HANDLING
    // =========================================================================

    @Test
    fun calculateDistanceMeters_latitudeOutOfRange_returnsNull() {
        assertNull(useCase.calculateDistanceMeters(90.0001, 13.4050, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(-90.0001, 13.4050, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(52.5200, 13.4050, 91.0, 13.4050))
        assertNull(useCase.calculateDistanceMeters(52.5200, 13.4050, -95.0, 13.4050))
    }

    @Test
    fun calculateDistanceMeters_longitudeOutOfRange_returnsNull() {
        assertNull(useCase.calculateDistanceMeters(52.5200, 180.0001, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(52.5200, -180.0001, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(52.5200, 13.4050, 52.5200, 180.5))
        assertNull(useCase.calculateDistanceMeters(52.5200, 13.4050, 52.5200, -181.0))
    }

    @Test
    fun calculateDistanceMeters_nanAndInfiniteCoordinates_returnsNull() {
        assertNull(useCase.calculateDistanceMeters(Double.NaN, 13.4050, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(52.5200, Double.NaN, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(52.5200, 13.4050, Double.NaN, 13.4050))
        assertNull(useCase.calculateDistanceMeters(52.5200, 13.4050, 52.5200, Double.NaN))

        assertNull(useCase.calculateDistanceMeters(Double.POSITIVE_INFINITY, 13.4050, 52.5200, 13.4050))
        assertNull(useCase.calculateDistanceMeters(52.5200, Double.NEGATIVE_INFINITY, 52.5200, 13.4050))
    }

    @Test
    fun calculateDistanceMeters_exactBoundaryCoordinates_calculatesSuccessfully() {
        // Valid exact boundaries: Latitude [-90.0, 90.0], Longitude [-180.0, 180.0]
        val distBoundary = useCase.calculateDistanceMeters(-90.0, -180.0, 90.0, 180.0)
        assertNotNull(distBoundary)
        assertEquals(20015000.0, distBoundary!!, 20000.0)
    }

    @Test
    fun isWithinClubRadius_nullCoordinates_returnsFalse() {
        assertFalse(useCase.isWithinClubRadius(null, 13.4050, 52.5200, 13.4050, 200.0))
        assertFalse(useCase.isWithinClubRadius(52.5200, null, 52.5200, 13.4050, 200.0))
        assertFalse(useCase.isWithinClubRadius(52.5200, 13.4050, null, 13.4050, 200.0))
        assertFalse(useCase.isWithinClubRadius(52.5200, 13.4050, 52.5200, null, 200.0))
    }

    @Test
    fun isWithinClubRadius_invalidCoordinates_returnsFalseSafely() {
        assertFalse(useCase.isWithinClubRadius(95.0, 13.4050, 52.5200, 13.4050, 200.0))
        assertFalse(useCase.isWithinClubRadius(52.5200, 190.0, 52.5200, 13.4050, 200.0))
        assertFalse(useCase.isWithinClubRadius(Double.NaN, 13.4050, 52.5200, 13.4050, 200.0))
        assertFalse(useCase.isWithinClubRadius(52.5200, Double.POSITIVE_INFINITY, 52.5200, 13.4050, 200.0))
    }

    @Test
    fun isWithinClubRadius_negativeRadiusOrTolerance_returnsFalse() {
        // Negative radius is invalid
        assertFalse(useCase.isWithinClubRadius(52.5200, 13.4050, 52.5200, 13.4050, radiusMeters = -10.0))
        // Negative tolerance is invalid
        assertFalse(useCase.isWithinClubRadius(52.5200, 13.4050, 52.5200, 13.4050, radiusMeters = 100.0, toleranceMeters = -5.0))
    }

    @Test
    fun isWithinClubRadius_zeroRadius_matchesOnlyAtExactCenter() {
        // At exact center (distance == 0.0), zero radius should match
        assertTrue(useCase.isWithinClubRadius(52.5200, 13.4050, 52.5200, 13.4050, radiusMeters = 0.0))
        // At even 1 meter away, zero radius should return false
        assertFalse(useCase.isWithinClubRadius(52.52001, 13.4050, 52.5200, 13.4050, radiusMeters = 0.0))
    }

    @Test
    fun calculateUserDistance_validCoordinates_returnsValidResult() {
        val result = useCase.calculateUserDistance(
            targetUserId = "user_789",
            currentUserLat = 52.5200,
            currentUserLng = 13.4050,
            targetUserLat = 52.5210,
            targetUserLng = 13.4060
        )

        assertEquals("user_789", result.userId)
        assertTrue(result.isValid)
        assertNotNull(result.rawDistanceMeters)
        assertTrue(result.rawDistanceMeters!! > 0)
    }

    @Test
    fun calculateUserDistance_missingOrInvalidCoordinates_returnsInvalidResult() {
        val missingLat = useCase.calculateUserDistance(
            targetUserId = "user_456",
            currentUserLat = null,
            currentUserLng = 13.4050,
            targetUserLat = 52.5210,
            targetUserLng = 13.4060
        )
        assertEquals("user_456", missingLat.userId)
        assertFalse(missingLat.isValid)
        assertNull(missingLat.rawDistanceMeters)

        val invalidLat = useCase.calculateUserDistance(
            targetUserId = "user_456",
            currentUserLat = 95.0,
            currentUserLng = 13.4050,
            targetUserLat = 52.5210,
            targetUserLng = 13.4060
        )
        assertFalse(invalidLat.isValid)
        assertNull(invalidLat.rawDistanceMeters)
    }

    @Test
    fun isValidCoordinate_boundaryAndInvalidValues() {
        assertTrue(useCase.isValidCoordinate(0.0, 0.0))
        assertTrue(useCase.isValidCoordinate(90.0, 180.0))
        assertTrue(useCase.isValidCoordinate(-90.0, -180.0))

        assertFalse(useCase.isValidCoordinate(90.0001, 0.0))
        assertFalse(useCase.isValidCoordinate(-90.0001, 0.0))
        assertFalse(useCase.isValidCoordinate(0.0, 180.0001))
        assertFalse(useCase.isValidCoordinate(0.0, -180.0001))
        assertFalse(useCase.isValidCoordinate(Double.NaN, 0.0))
        assertFalse(useCase.isValidCoordinate(0.0, Double.NaN))
        assertFalse(useCase.isValidCoordinate(Double.POSITIVE_INFINITY, 0.0))
        assertFalse(useCase.isValidCoordinate(0.0, Double.NEGATIVE_INFINITY))
    }

    // =========================================================================
    // 4. PERFORMANCE BENCHMARK & PRECISION ASSERTIONS (< 50ms for 1,000 runs)
    // =========================================================================

    @Test
    fun calculateDistanceMeters_performanceBenchmark_executesOneThousandIterationsUnderFiftyMilliseconds() {
        val baseLat = 52.52000
        val baseLng = 13.40500
        val iterations = 1000

        // Warmup JIT compiler
        for (i in 0 until 100) {
            useCase.calculateDistanceMeters(baseLat, baseLng, baseLat + (i * 0.0001), baseLng + (i * 0.0001))
        }

        val startTime = System.nanoTime()
        for (i in 0 until iterations) {
            val targetLat = baseLat + (i % 100) * 0.0001
            val targetLng = baseLng + (i % 100) * 0.0001
            val distance = useCase.calculateDistanceMeters(baseLat, baseLng, targetLat, targetLng)
            assertNotNull("Distance must not be null for valid coordinate sample at index $i", distance)
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000.0

        assertTrue(
            "Benchmark failure: 1,000 distance calculations took ${elapsedMs}ms, exceeding 50ms performance budget",
            elapsedMs < 50.0
        )
    }

    @Test
    fun isWithinClubRadius_performanceBenchmark_executesOneThousandGeofenceChecksUnderFiftyMilliseconds() {
        val clubLat = 52.52000
        val clubLng = 13.40500
        val radiusMeters = 200.0
        val toleranceMeters = 10.0
        val iterations = 1000

        // Warmup
        for (i in 0 until 100) {
            useCase.isWithinClubRadius(clubLat + (i * 0.00005), clubLng, clubLat, clubLng, radiusMeters, toleranceMeters)
        }

        val startTime = System.nanoTime()
        var insideCount = 0
        for (i in 0 until iterations) {
            val userLat = clubLat + ((i % 50) - 25) * 0.0001
            val userLng = clubLng + ((i % 50) - 25) * 0.0001
            if (useCase.isWithinClubRadius(userLat, userLng, clubLat, clubLng, radiusMeters, toleranceMeters)) {
                insideCount++
            }
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000.0

        assertTrue("Expected at least some points to fall inside geofence", insideCount > 0)
        assertTrue(
            "Benchmark failure: 1,000 geofence radius checks took ${elapsedMs}ms, exceeding 50ms performance budget",
            elapsedMs < 50.0
        )
    }

    @Test
    fun isWithinClubRadius_precisionTolerance_providesClearAssertionMessages() {
        val clubLat = 52.52000
        val clubLng = 13.40500
        val radiusMeters = 150.0
        val toleranceMeters = 10.0

        // Point inside radius
        val insideLat = 52.52050 // ~55m away
        val insideLng = 13.40500
        val insideDist = useCase.calculateDistanceMeters(insideLat, insideLng, clubLat, clubLng)
        assertNotNull(insideDist)
        assertTrue(
            "Geofence assertion failed: User at ($insideLat, $insideLng) with distance ${insideDist}m must be within radius ${radiusMeters}m",
            useCase.isWithinClubRadius(insideLat, insideLng, clubLat, clubLng, radiusMeters, toleranceMeters)
        )

        // Point far outside radius
        val outsideLat = 52.53000 // ~1.1km away
        val outsideLng = 13.40500
        val outsideDist = useCase.calculateDistanceMeters(outsideLat, outsideLng, clubLat, clubLng)
        assertNotNull(outsideDist)
        assertFalse(
            "Geofence assertion failed: User at ($outsideLat, $outsideLng) with distance ${outsideDist}m must not be within radius ${radiusMeters}m",
            useCase.isWithinClubRadius(outsideLat, outsideLng, clubLat, clubLng, radiusMeters, toleranceMeters)
        )
    }
}
