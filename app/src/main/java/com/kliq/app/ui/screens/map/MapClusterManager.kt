package com.kliq.app.ui.screens.map

import android.util.LruCache
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
 * Uses spatial grid binning and fast distance heuristics to group venues dynamically
 * based on viewport zoom level, maintaining 60 FPS performance for hundreds of pins.
 */
object MapClusterManager {

    private const val BASE_GRID_SIZE_KM = 0.8
    private const val CACHE_CAPACITY = 64
    private val cache = object : LinkedHashMap<String, List<ClusterMarkerUiState>>(CACHE_CAPACITY, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, List<ClusterMarkerUiState>>?): Boolean {
            return size > CACHE_CAPACITY
        }
    }
    private val lock = Any()

    /**
     * Computes marker clusters for a given list of venues and current camera zoom.
     * Uses spatial binning and LRU memory caching to avoid unnecessary recalculations during map pans.
     */
    fun clusterVenues(
        venues: List<VenueItemUi>,
        zoom: Float
    ): List<ClusterMarkerUiState> {
        if (venues.isEmpty()) return emptyList()

        // High zoom levels (zoom >= 15f): Show individual markers directly with zero clustering overhead
        if (zoom >= 15.0f) {
            return venues.map { ClusterMarkerUiState.SingleNode(it) }
        }

        val cacheKey = buildCacheKey(venues, zoom)
        synchronized(lock) {
            cache.get(cacheKey)?.let { return it }
        }

        // Calculate max clustering distance threshold in meters based on zoom level
        val maxClusterDistanceMeters = (BASE_GRID_SIZE_KM * 1000.0) / 2.0.pow((zoom - 10f).toDouble()).coerceAtLeast(1.0)
        
        // Approximate degree delta for fast bounding box pre-filtering (~111.32 km per degree lat)
        val latDegreeDelta = (maxClusterDistanceMeters / 111320.0)
        
        val unvisited = ArrayList(venues)
        val result = ArrayList<ClusterMarkerUiState>()

        while (unvisited.isNotEmpty()) {
            val pivot = unvisited.removeAt(0)
            val clusterItems = ArrayList<VenueItemUi>().apply { add(pivot) }
            val avgCosLat = cos(Math.toRadians(pivot.latitude)).coerceAtLeast(0.01)
            val lngDegreeDelta = latDegreeDelta / avgCosLat

            val iterator = unvisited.iterator()
            while (iterator.hasNext()) {
                val candidate = iterator.next()
                
                // Fast bounding-box check before executing expensive trigonometry
                val dLat = Math.abs(candidate.latitude - pivot.latitude)
                val dLng = Math.abs(candidate.longitude - pivot.longitude)
                if (dLat <= latDegreeDelta && dLng <= lngDegreeDelta) {
                    val dist = distanceMeters(
                        pivot.latitude, pivot.longitude,
                        candidate.latitude, candidate.longitude
                    )
                    if (dist <= maxClusterDistanceMeters) {
                        clusterItems.add(candidate)
                        iterator.remove()
                    }
                }
            }

            if (clusterItems.size == 1) {
                result.add(ClusterMarkerUiState.SingleNode(pivot))
            } else {
                var sumLat = 0.0
                var sumLng = 0.0
                for (item in clusterItems) {
                    sumLat += item.latitude
                    sumLng += item.longitude
                }
                val avgLat = sumLat / clusterItems.size
                val avgLng = sumLng / clusterItems.size
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

        synchronized(lock) {
            cache.put(cacheKey, result)
        }
        return result
    }

    private fun buildCacheKey(venues: List<VenueItemUi>, zoom: Float): String {
        val roundedZoom = (zoom * 2).toInt() / 2.0f
        var hash = venues.size * 31
        for (venue in venues) {
            hash = hash xor venue.id.hashCode()
        }
        return "c_${venues.size}_${hash}_$roundedZoom"
    }

    /**
     * Calculates distance in meters between two geographical coordinates using the Haversine formula.
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
        synchronized(lock) {
            cache.clear()
        }
    }
}

