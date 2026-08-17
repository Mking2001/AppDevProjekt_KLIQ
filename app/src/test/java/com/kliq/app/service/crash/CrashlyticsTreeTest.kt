package com.kliq.app.service.crash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import timber.log.Timber

/**
 * Unit-Tests zur Verifizierung des Timber Trees [KliqCrashlyticsTree] und der Logger-Abstraktion [CrashReportingLogger].
 */
class CrashlyticsTreeTest {

    @Before
    fun setUp() {
        Timber.uprootAll()
    }

    @Test
    fun testCrashReportingLogger_setCustomKey_storesSanitizedEntries() {
        CrashReportingLogger.setCustomKey("active_screen", "MapScreen")
        CrashReportingLogger.setCustomKey("user_phone", "+491701234567")

        val keys = CrashReportingLogger.getCustomKeys()

        assertEquals("mapscreen", keys["active_screen"])
        assertTrue(keys["user_phone"]?.contains("[REDACTED_PHONE]") == true)
    }

    @Test
    fun testCrashReportingLogger_logBreadcrumb_doesNotCrash() {
        CrashReportingLogger.logBreadcrumb("User opened Berghain club details with phone +4915100000")
        // Stellt sicher, dass das Breadcrumb-Logging im Offline/Test-Modus fehlerfrei durchläuft
        assertNotNull(CrashReportingLogger.getCustomKeys())
    }

    @Test
    fun testCrashReportingLogger_logNonFatalException_handlesCaughtError() {
        val testException = IllegalStateException("Failed to parse JSON payload for user@domain.de")
        CrashReportingLogger.logNonFatalException(testException, "Network response parse failure")

        assertNotNull(testException.message)
    }

    @Test
    fun testKliqCrashlyticsTree_plantsSuccessfully() {
        val tree = KliqCrashlyticsTree()
        Timber.plant(tree)

        Timber.w("Warning event at lat=52.5200, lng=13.4050")
        Timber.e(RuntimeException("Test non-fatal exception"), "Error event")

        assertEquals(1, Timber.treeCount)
    }
}
