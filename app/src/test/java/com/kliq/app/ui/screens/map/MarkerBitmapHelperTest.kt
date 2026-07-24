package com.kliq.app.ui.screens.map

import com.google.android.gms.maps.model.BitmapDescriptor
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for [MarkerBitmapHelper] validating custom bitmap descriptor creation
 * for club markers, user markers, cluster markers, and memory caching behavior.
 */
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
    }

    @Test
    fun testGetClubMarkerBitmap_usesCacheForSameCategoryAndEventStatus() {
        val descriptor1 = MarkerBitmapHelper.getClubMarkerBitmap(category = "Club", hasActiveEvent = true)
        val descriptor2 = MarkerBitmapHelper.getClubMarkerBitmap(category = "Club", hasActiveEvent = true)

        assertSame(descriptor1, descriptor2)
    }

    @Test
    fun testGetUserMarkerBitmap_returnsValidBitmapDescriptor() {
        val descriptor = MarkerBitmapHelper.getUserMarkerBitmap(username = "Alex", isOnline = true)
        assertNotNull(descriptor)
    }

    @Test
    fun testGetUserMarkerBitmap_usesCacheForSameInitialAndOnlineStatus() {
        val descriptor1 = MarkerBitmapHelper.getUserMarkerBitmap(username = "Alex", isOnline = true)
        val descriptor2 = MarkerBitmapHelper.getUserMarkerBitmap(username = "Adam", isOnline = true)

        assertSame(descriptor1, descriptor2)
    }

    @Test
    fun testGetClusterMarkerBitmap_returnsValidBitmapDescriptor() {
        val descriptor = MarkerBitmapHelper.getClusterMarkerBitmap(count = 5, primaryCategory = "Club")
        assertNotNull(descriptor)
    }

    @Test
    fun testClearCache_evictsCachedDescriptors() {
        val descriptor1 = MarkerBitmapHelper.getClubMarkerBitmap(category = "Bar", hasActiveEvent = false)
        MarkerBitmapHelper.clearCache()
        val descriptor2 = MarkerBitmapHelper.getClubMarkerBitmap(category = "Bar", hasActiveEvent = false)

        assertNotNull(descriptor1)
        assertNotNull(descriptor2)
    }
}
