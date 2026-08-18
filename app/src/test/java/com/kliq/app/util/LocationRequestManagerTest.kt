package com.kliq.app.util

import com.google.android.gms.location.Priority
import com.kliq.app.data.model.LocationTrackingMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit Test Suite for [LocationRequestManager] and Power-Policy enforcement (Step 9.7: GPS Battery Optimization).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LocationRequestManagerTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var adaptiveController: AdaptiveLocationController
    private lateinit var requestManager: LocationRequestManager

    @Before
    fun setUp() {
        adaptiveController = AdaptiveLocationController(testDispatcher)
        requestManager = LocationRequestManager(adaptiveController)
    }

    /**
     * Test: Wechselt die Location-Konfiguration korrekt von Balanced auf High-Accuracy,
     * wenn ein Geofence-Check oder Scan initiiert wird?
     */
    @Test
    fun testSwitchConfiguration_balancedToHighAccuracy_whenGeofenceCheckOrScanInitiated() {
        // Initialer Zustand: Balanced Ambient Mode (z.B. für Party-Map Übersicht)
        adaptiveController.setTrackingMode(LocationTrackingMode.BALANCED_AMBIENT)
        val initialRequest = requestManager.buildLocationRequest(inForeground = true)

        assertEquals("Initial priority must be BALANCED_POWER_ACCURACY",
            Priority.PRIORITY_BALANCED_POWER_ACCURACY, initialRequest.priority)
        assertEquals("Initial interval must be 60 seconds", 60_000L, initialRequest.intervalMillis)
        assertEquals("Initial min displacement must be 50 meters", 50.0f, initialRequest.minUpdateDistanceMeters, 0.01f)

        // Geofence-Validierung oder QR-Scan wird initiiert
        val burstRequest = requestManager.initiateVerificationBurst(durationMs = 20_000L)

        assertEquals("Burst priority must switch to HIGH_ACCURACY",
            Priority.PRIORITY_HIGH_ACCURACY, burstRequest.priority)
        assertEquals("Burst interval must be reduced to 8 seconds", 8_000L, burstRequest.intervalMillis)
        assertEquals("Burst fastest interval must be 3 seconds", 3_000L, burstRequest.minUpdateIntervalMillis)
        assertEquals("Burst min displacement must be tightened to 5 meters", 5.0f, burstRequest.minUpdateDistanceMeters, 0.01f)
        assertTrue("Burst session must be marked active in controller", adaptiveController.isBurstActive.value)
    }

    /**
     * Test: Werden die Update-Intervalle und Distanz-Filter (displacementFilter / distanceFilter)
     * entsprechend dem aktuellen App-Zustand (Foreground vs. Background) korrekt hochskaliert?
     */
    @Test
    fun testUpdateIntervalsAndDisplacementFilter_scaledProperly_forForegroundVsBackground() {
        // Vordergrund-Zustand (Foreground)
        val foregroundRequest = requestManager.buildLocationRequest(
            mode = LocationTrackingMode.BALANCED_AMBIENT,
            inForeground = true
        )
        assertEquals(60_000L, foregroundRequest.intervalMillis)
        assertEquals(30_000L, foregroundRequest.minUpdateIntervalMillis)
        assertEquals(50.0f, foregroundRequest.minUpdateDistanceMeters, 0.01f)
        assertEquals(Priority.PRIORITY_BALANCED_POWER_ACCURACY, foregroundRequest.priority)

        // Hintergrund-Zustand (Background / Inaktiv)
        val backgroundRequest = requestManager.buildLocationRequest(
            mode = LocationTrackingMode.BALANCED_AMBIENT,
            inForeground = false
        )
        assertTrue("Background interval must scale up to >= 5 minutes or PASSIVE_INTERVAL",
            backgroundRequest.intervalMillis >= 300_000L)
        assertEquals("Background min displacement filter must scale up to 100 meters",
            100.0f, backgroundRequest.minUpdateDistanceMeters, 0.01f)
        assertEquals("Background priority must drop to PASSIVE (Geofence-only triggers)",
            Priority.PRIORITY_PASSIVE, backgroundRequest.priority)
    }

    /**
     * Test: Stoppt der Listener alle aktiven GPS-Subskriptionen,
     * wenn die UI in den Hintergrund wechselt (onCleared / onPause)?
     */
    @Test
    fun testStopAllSubscriptions_onPauseOrCleared_stopsActiveGpsTracking() {
        // Subscription starten
        requestManager.startSubscription()
        requestManager.onResume()
        assertTrue("GPS subscription must be active when resumed", requestManager.isGpsActivelySubscribed())

        // UI wechselt in den Hintergrund (onPause)
        requestManager.onPause()
        assertFalse("Active GPS polling must be paused on background transition", requestManager.isGpsActivelySubscribed())
        assertEquals("Controller mode must throttle to IDLE_PASSIVE",
            LocationTrackingMode.IDLE_PASSIVE, adaptiveController.effectiveMode.value)

        // UI wird zerstört (onCleared)
        requestManager.initiateVerificationBurst(10_000L)
        assertTrue(adaptiveController.isBurstActive.value)

        requestManager.stopAllSubscriptions()
        assertFalse("GPS subscription must be fully stopped after cleanup", requestManager.isGpsActivelySubscribed())
        assertFalse("Burst session must be cancelled on cleanup", adaptiveController.isBurstActive.value)
    }
}
