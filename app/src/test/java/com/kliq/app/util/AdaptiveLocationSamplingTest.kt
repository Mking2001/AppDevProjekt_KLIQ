package com.kliq.app.util

import com.google.android.gms.location.Priority
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPowerPolicy
import com.kliq.app.data.model.LocationTrackingMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdaptiveLocationSamplingTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var controller: AdaptiveLocationController

    @Before
    fun setUp() {
        controller = AdaptiveLocationController(testDispatcher)
    }

    @Test
    fun testInitialState_isBalancedAmbientAndActive() {
        assertEquals(LocationTrackingMode.BALANCED_AMBIENT, controller.configuredMode.value)
        assertEquals(LocationTrackingMode.BALANCED_AMBIENT, controller.effectiveMode.value)
        assertEquals(LocationPowerPolicy.BalancedAmbient, controller.currentPolicy.value)
        assertFalse(controller.isStationary.value)
        assertFalse(controller.isBurstActive.value)
        assertEquals(0, controller.burstRemainingSeconds.value)
    }

    @Test
    fun testSetTrackingMode_updatesConfiguredAndEffectiveMode() {
        controller.setTrackingMode(LocationTrackingMode.HIGH_ACCURACY)
        assertEquals(LocationTrackingMode.HIGH_ACCURACY, controller.configuredMode.value)
        assertEquals(LocationTrackingMode.HIGH_ACCURACY, controller.effectiveMode.value)
        assertEquals(LocationPowerPolicy.HighAccuracy, controller.currentPolicy.value)

        controller.setTrackingMode(LocationTrackingMode.IDLE_PASSIVE)
        assertEquals(LocationTrackingMode.IDLE_PASSIVE, controller.configuredMode.value)
        assertEquals(LocationTrackingMode.IDLE_PASSIVE, controller.effectiveMode.value)
        assertEquals(LocationPowerPolicy.IdlePassive, controller.currentPolicy.value)
    }

    @Test
    fun testStationaryDetection_consecutiveLowSpeedFixes_triggersIdlePassiveMode() {
        val fix1 = LocationData(latitude = 52.52000, longitude = 13.40500, speed = 0.1f)
        val fix2 = LocationData(latitude = 52.52001, longitude = 13.40501, speed = 0.05f)
        val fix3 = LocationData(latitude = 52.52001, longitude = 13.40501, speed = 0.0f)

        controller.onLocationSampleReceived(fix1)
        assertFalse("Single fix should not mark device as stationary", controller.isStationary.value)

        controller.onLocationSampleReceived(fix2)
        controller.onLocationSampleReceived(fix3)

        assertTrue("Consecutive stationary fixes should activate stationary state", controller.isStationary.value)
        assertEquals("Stationary state in balanced mode should adaptively downgrade to IDLE_PASSIVE",
            LocationTrackingMode.IDLE_PASSIVE, controller.effectiveMode.value)
        assertEquals(LocationPowerPolicy.IdlePassive, controller.currentPolicy.value)
    }

    @Test
    fun testMovementResumed_afterStationary_restoresBalancedMode() {

        controller.setStationaryState(true)
        assertTrue(controller.isStationary.value)
        assertEquals(LocationTrackingMode.IDLE_PASSIVE, controller.effectiveMode.value)

        val startFix = LocationData(latitude = 52.5200, longitude = 13.4050, speed = 0.0f)
        val movingFix = LocationData(latitude = 52.5250, longitude = 13.4100, speed = 1.4f)

        controller.onLocationSampleReceived(startFix)
        controller.onLocationSampleReceived(movingFix)

        assertFalse("Moving fix should clear stationary flag", controller.isStationary.value)
        assertEquals("Balanced mode should be restored when movement resumes",
            LocationTrackingMode.BALANCED_AMBIENT, controller.effectiveMode.value)
        assertEquals(LocationPowerPolicy.BalancedAmbient, controller.currentPolicy.value)
    }

    @Test
    fun testHighAccuracyBurst_activatesImmediatelyAndCountsDown() = testScope.runTest {
        controller.requestHighAccuracyBurst(durationMs = 5_000L)
        testDispatcher.scheduler.runCurrent()

        assertTrue(controller.isBurstActive.value)
        assertEquals(LocationTrackingMode.HIGH_ACCURACY, controller.effectiveMode.value)
        assertEquals(LocationPowerPolicy.HighAccuracy, controller.currentPolicy.value)
        assertEquals(5, controller.burstRemainingSeconds.value)

        testDispatcher.scheduler.advanceTimeBy(2_000L)
        testDispatcher.scheduler.runCurrent()
        assertTrue(controller.burstRemainingSeconds.value in 1..4)
        assertTrue(controller.isBurstActive.value)

        testDispatcher.scheduler.advanceTimeBy(5_000L)
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(controller.isBurstActive.value)
        assertEquals(0, controller.burstRemainingSeconds.value)
        assertEquals(LocationTrackingMode.BALANCED_AMBIENT, controller.effectiveMode.value)
    }

    @Test
    fun testCancelBurstSession_immediatelyRestoresAmbientMode() = testScope.runTest {
        controller.requestHighAccuracyBurst(durationMs = 30_000L)
        assertTrue(controller.isBurstActive.value)

        controller.cancelBurstSession()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(controller.isBurstActive.value)
        assertEquals(0, controller.burstRemainingSeconds.value)
        assertEquals(LocationTrackingMode.BALANCED_AMBIENT, controller.effectiveMode.value)
    }

    @Test
    fun testLifecycleBackgroundState_downgradesToIdlePassive() {
        controller.setForegroundState(false)

        assertEquals("Background state should throttle to IDLE_PASSIVE",
            LocationTrackingMode.IDLE_PASSIVE, controller.effectiveMode.value)
        assertEquals(LocationPowerPolicy.IdlePassive, controller.currentPolicy.value)

        controller.setForegroundState(true)
        assertEquals("Returning to foreground should restore BALANCED_AMBIENT",
            LocationTrackingMode.BALANCED_AMBIENT, controller.effectiveMode.value)
    }

    @Test
    fun testLocationPowerPolicyParameters_forAllModes() {
        val highAccPolicy = LocationPowerPolicy.forMode(LocationTrackingMode.HIGH_ACCURACY)
        assertEquals(Priority.PRIORITY_HIGH_ACCURACY, highAccPolicy.priority)
        assertTrue(highAccPolicy.intervalMillis <= 10_000L)
        assertTrue(highAccPolicy.minDistanceDisplacementMeters <= 5.0f)

        val balancedPolicy = LocationPowerPolicy.forMode(LocationTrackingMode.BALANCED_AMBIENT)
        assertEquals(Priority.PRIORITY_BALANCED_POWER_ACCURACY, balancedPolicy.priority)
        assertTrue(balancedPolicy.intervalMillis >= 60_000L)
        assertTrue(balancedPolicy.minDistanceDisplacementMeters >= 50.0f)

        val idlePolicy = LocationPowerPolicy.forMode(LocationTrackingMode.IDLE_PASSIVE)
        assertEquals(Priority.PRIORITY_PASSIVE, idlePolicy.priority)
        assertTrue(idlePolicy.intervalMillis >= 120_000L)
        assertTrue(idlePolicy.minDistanceDisplacementMeters >= 100.0f)
    }
}
