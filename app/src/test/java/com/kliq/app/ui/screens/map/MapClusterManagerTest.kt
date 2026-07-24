package com.kliq.app.ui.screens.map

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [MapClusterManager] validating distance calculation,
 * zoom-dependent clustering algorithm, and caching behavior.
 */
class MapClusterManagerTest {

    private val venue1 = VenueItemUi(
        id = "club_1",
        name = "Club Alpha",
        category = "Club",
        distance = "0.2 km",
        latitude = 52.5200,
        longitude = 13.4050
    )

    private val venue2 = VenueItemUi(
        id = "club_2",
        name = "Club Beta",
        category = "Club",
        distance = "0.3 km",
        latitude = 52.5210,
        longitude = 13.4060
    )

    private val venueFar = VenueItemUi(
        id = "club_3",
        name = "Club Gamma",
        category = "Club",
        distance = "15.0 km",
        latitude = 52.6000,
        longitude = 13.5500
    )

    @Before
    fun setUp() {
        MapClusterManager.clearCache()
    }

    @Test
    fun testClusterVenuesAtHighZoom_returnsSingleNodesOnly() {
        val venues = listOf(venue1, venue2, venueFar)
        val clusters = MapClusterManager.clusterVenues(venues, zoom = 16.0f)

        assertEquals(3, clusters.size)
        assertTrue(clusters.all { it is ClusterMarkerUiState.SingleNode })
    }

    @Test
    fun testClusterVenuesAtLowZoom_groupsNearbyVenuesIntoClusterNode() {
        val venues = listOf(venue1, venue2, venueFar)
        val clusters = MapClusterManager.clusterVenues(venues, zoom = 11.0f)

        // venue1 and venue2 should be grouped together into a ClusterNode, venueFar should be SingleNode
        assertEquals(2, clusters.size)
        val clusterNode = clusters.filterIsInstance<ClusterMarkerUiState.ClusterNode>().firstOrNull()
        val singleNode = clusters.filterIsInstance<ClusterMarkerUiState.SingleNode>().firstOrNull()

        assertTrue(clusterNode != null)
        assertEquals(2, clusterNode?.count)
        assertEquals(venueFar.id, singleNode?.venue?.id)
    }

    @Test
    fun testCalculateDistanceMeters_returnsAccurateDistance() {
        val dist = MapClusterManager.calculateDistanceMeters(
            52.5200, 13.4050,
            52.5210, 13.4060
        )

        // Distance between (52.5200, 13.4050) and (52.5210, 13.4060) is approx 130-140 meters
        assertTrue(dist in 100.0..200.0)
    }

    @Test
    fun testEmptyVenueList_returnsEmptyClusters() {
        val clusters = MapClusterManager.clusterVenues(emptyList(), zoom = 12.0f)
        assertTrue(clusters.isEmpty())
    }
}
