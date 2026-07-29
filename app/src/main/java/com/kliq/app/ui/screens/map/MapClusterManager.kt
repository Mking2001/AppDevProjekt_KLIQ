package com.kliq.app.ui.screens.map

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Represents the display state of a map marker item, which can be either
 * an individual venue node or a cluster of multiple nearby venues.
 */
sealed class ClusterMarkerUiState {
    abstract val id: String
    abstract val position: LatLng

    /**
     * Single venue node on the map.
     */
    data class SingleNode(
        val venue: VenueItemUi
    ) : ClusterMarkerUiState() {
        override val id: String get() = venue.id
        override val position: LatLng get() = LatLng(venue.latitude, venue.longitude)
    }

    /**
     * Grouped cluster node representing multiple venues in close visual proximity.
     */
    data class ClusterNode(
        val clusterId: String,
        val count: Int,
        val centerLat: Double,
        val centerLng: Double,
        val items: List<VenueItemUi>,
        val primaryCategory: String
    ) : ClusterMarkerUiState() {
        override val id: String get() = clusterId
        override val position: LatLng get() = LatLng(centerLat, centerLng)
    }
}

/**
 * Performance-optimized clustering manager for map markers.
 * Groups venues dynamically based on viewport zoom level and geographic distance.
 */
object MapClusterManager {

    private const val BASE_GRID_SIZE_KM = 0.8
    private val cache = mutableMapOf<String, List<ClusterMarkerUiState>>()

    /**
     * Computes marker clusters for a given list of venues and current camera zoom.
     * Uses internal memory caching to avoid unnecessary recalculations during small map pans.
     */
    fun clusterVenues(
        venues: List<VenueItemUi>,
        zoom: Float
    ): List<ClusterMarkerUiState> {
        if (venues.isEmpty()) return emptyList()

        // High zoom levels (zoom >= 15f): Show individual markers directly
        if (zoom >= 15.0f) {
            return venues.map { ClusterMarkerUiState.SingleNode(it) }
        }

        val cacheKey = buildCacheKey(venues, zoom)
        cache[cacheKey]?.let { return it }

        // Calculate max clustering distance threshold in meters based on zoom level
        val maxClusterDistanceMeters = (BASE_GRID_SIZE_KM * 1000.0) / 2.0.pow((zoom - 10f).toDouble()).coerceAtLeast(0.1)

        val unvisited = venues.toMutableList()
        val result = mutableListOf<ClusterMarkerUiState>()

        while (unvisited.isNotEmpty()) {
            val pivot = unvisited.removeAt(0)
            val clusterItems = mutableListOf(pivot)

            val iterator = unvisited.iterator()
            while (iterator.hasNext()) {
                val candidate = iterator.next()
                val dist = distanceMeters(
                    pivot.latitude, pivot.longitude,
                    candidate.latitude, candidate.longitude
                )
                if (dist <= maxClusterDistanceMeters) {
                    clusterItems.add(candidate)
                    iterator.remove()
                }
            }

            if (clusterItems.size == 1) {
                result.add(ClusterMarkerUiState.SingleNode(pivot))
            } else {
                val avgLat = clusterItems.sumOf { it.latitude } / clusterItems.size
                val avgLng = clusterItems.sumOf { it.longitude } / clusterItems.size
                val clusterId = "cluster_${pivot.id}_${clusterItems.size}"
                val primaryCategory = clusterItems.groupingBy { it.category }
                    .eachCount()
                    .maxByOrNull { it.value }?.key ?: "Club"

                result.add(
                    ClusterMarkerUiState.ClusterNode(
                        clusterId = clusterId,
                        count = clusterItems.size,
                        centerLat = avgLat,
                        centerLng = avgLng,
                        items = clusterItems,
                        primaryCategory = primaryCategory
                    )
                )
            }
        }

        cache[cacheKey] = result
        return result
    }

    private fun buildCacheKey(venues: List<VenueItemUi>, zoom: Float): String {
        val roundedZoom = (zoom * 2).toInt() / 2.0f
        val venueHash = venues.fold(0) { acc, venue -> acc xor venue.id.hashCode() }
        return "key_${venueHash}_$roundedZoom"
    }

    /**
     * Calculates distance in meters between two geographical coordinates.
     */
    fun calculateDistanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        return distanceMeters(lat1, lon1, lat2, lon2)
    }

    private fun distanceMeters(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }

    /**
     * Clears cached cluster calculations.
     */
    fun clearCache() {
        cache.clear()
    }
}
