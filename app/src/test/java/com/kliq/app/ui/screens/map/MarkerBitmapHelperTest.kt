package com.kliq.app.ui.screens.map

import com.google.android.gms.maps.model.BitmapDescriptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class MarkerBitmapHelperTest {

    @Before
    fun setUp() {
        MarkerBitmapHelper.clearCache()
        MarkerBitmapHelper.descriptorFactory = { mock(BitmapDescriptor::class.java) }
    }

    @Test
    fun testGetClubMarkerBitmap_returnsValidBitmapDescriptor() {
        val descriptor = MarkerBitmapHelper.getClubMarkerBitmap(category = "Club", hasActiveEvent = true)
        assertNotNull(descriptor)
        assertEquals(1, MarkerBitmapHelper.cacheSize())
    }

    @Test
    fun testGetClubMarkerBitmap_usesCacheForSameCategoryAndEventStatus() {
        val descriptor1 = MarkerBitmapHelper.getClubMarkerBitmap(category = "Club", hasActiveEvent = true)
        val descriptor2 = MarkerBitmapHelper.getClubMarkerBitmap(category = "Club", hasActiveEvent = true)

        assertSame(descriptor1, descriptor2)
        assertEquals(1, MarkerBitmapHelper.cacheSize())
    }

    @Test
    fun testGetUserMarkerBitmap_returnsValidBitmapDescriptor() {
        val descriptor = MarkerBitmapHelper.getUserMarkerBitmap(username = "Alex", isOnline = true)
        assertNotNull(descriptor)
        assertEquals(1, MarkerBitmapHelper.cacheSize())
    }

    @Test
    fun testGetUserMarkerBitmap_usesCacheForSameInitialAndOnlineStatus() {
        val descriptor1 = MarkerBitmapHelper.getUserMarkerBitmap(username = "Alex", isOnline = true)
        val descriptor2 = MarkerBitmapHelper.getUserMarkerBitmap(username = "Adam", isOnline = true)

        assertSame(descriptor1, descriptor2)
        assertEquals(1, MarkerBitmapHelper.cacheSize())
    }

    @Test
    fun testGetClusterMarkerBitmap_returnsValidBitmapDescriptor() {
        val descriptor = MarkerBitmapHelper.getClusterMarkerBitmap(count = 5, primaryCategory = "Club")
        assertNotNull(descriptor)
        assertEquals(1, MarkerBitmapHelper.cacheSize())
    }

    @Test
    fun testPrewarmCache_populatesCacheWithStandardVariants() {
        assertEquals(0, MarkerBitmapHelper.cacheSize())
        MarkerBitmapHelper.prewarmCache()
        assertTrue(MarkerBitmapHelper.cacheSize() >= 15)
    }

    @Test
    fun testClearCache_evictsCachedDescriptors() {
        MarkerBitmapHelper.getClubMarkerBitmap(category = "Bar", hasActiveEvent = false)
        assertEquals(1, MarkerBitmapHelper.cacheSize())

        MarkerBitmapHelper.clearCache()
        assertEquals(0, MarkerBitmapHelper.cacheSize())

        val descriptor2 = MarkerBitmapHelper.getClubMarkerBitmap(category = "Bar", hasActiveEvent = false)
        assertNotNull(descriptor2)
        assertEquals(1, MarkerBitmapHelper.cacheSize())
    }

    @Test
    fun testConcurrentAccess_isThreadSafe() {
        val executor = Executors.newFixedThreadPool(4)
        val categories = listOf("Club", "Bar", "Event", "Restaurant", "Lounge")

        for (i in 0 until 50) {
            executor.submit {
                val category = categories[i % categories.size]
                MarkerBitmapHelper.getClubMarkerBitmap(category, hasActiveEvent = (i % 2 == 0))
                MarkerBitmapHelper.getUserMarkerBitmap(username = "User$i", isOnline = (i % 2 == 0))
                MarkerBitmapHelper.getClusterMarkerBitmap(count = (i % 10) + 1, primaryCategory = category)
            }
        }

        executor.shutdown()
        val finished = executor.awaitTermination(5, TimeUnit.SECONDS)
        assertTrue(finished)
        assertTrue(MarkerBitmapHelper.cacheSize() > 0)
    }
}
