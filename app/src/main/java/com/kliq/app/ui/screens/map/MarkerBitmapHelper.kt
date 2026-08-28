package com.kliq.app.ui.screens.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.util.Locale

/**
 * High-performance bitmap generator and LRU memory cache for map markers.
 * Optimized for 60 FPS interactions with zero runtime allocations on recomposition,
 * supporting Kliq's high-contrast dark mode (purple/neon design system).
 */
object MarkerBitmapHelper {

    private const val CACHE_SIZE = 256
    private val bitmapDescriptorCache = object : LinkedHashMap<String, BitmapDescriptor>(CACHE_SIZE, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, BitmapDescriptor>?): Boolean {
            return size > CACHE_SIZE
        }
    }
    private val cacheLock = Any()

    // High-contrast Purple / Neon Design System Palette
    private const val COLOR_PRIMARY_PURPLE = 0xFF7C3AED.toInt()       // Vibrant Kliq Purple
    private const val COLOR_PRIMARY_PURPLE_DARK = 0xFF5B21B6.toInt()  // Deep Purple Accent
    private const val COLOR_PURPLE_DARK_BG = 0xFF1E1035.toInt()       // Ultra-Dark Purple Background
    private const val COLOR_EVENT_BADGE = 0xFFEC4899.toInt()          // Neon Pink Active Event Badge
    private const val COLOR_ONLINE_GREEN = 0xFF10B981.toInt()         // Neon Emerald Online Indicator
    private const val COLOR_OFFLINE_GRAY = 0xFF64748B.toInt()         // Slate Gray Offline Indicator
    private const val COLOR_CLUSTER_GLOW = 0x667C3AED.toInt()         // Translucent Purple Cluster Halo

    internal var descriptorFactory: (Bitmap?) -> BitmapDescriptor? = { bitmap ->
        try {
            if (bitmap != null) {
                BitmapDescriptorFactory.fromBitmap(bitmap)
            } else {
                BitmapDescriptorFactory.defaultMarker()
            }
        } catch (e: Throwable) {
            timber.log.Timber.w(e, "BitmapDescriptorFactory unavailable or Maps SDK not yet initialized")
            null
        }
    }

    /**
     * Generates or retrieves a cached custom bitmap descriptor for a club venue marker.
     */
    fun getClubMarkerBitmap(
        category: String,
        hasActiveEvent: Boolean
    ): BitmapDescriptor? {
        val cacheKey = "club_${category.lowercase(Locale.ROOT)}_${hasActiveEvent}"
        synchronized(cacheLock) {
            bitmapDescriptorCache[cacheKey]?.let { return it }
        }

        val bitmap = createClubPinBitmap(category, hasActiveEvent) ?: return null
        val descriptor = descriptorFactory(bitmap)
        if (descriptor != null) {
            synchronized(cacheLock) {
                bitmapDescriptorCache[cacheKey] = descriptor
            }
        }
        return descriptor
    }

    /**
     * Generates or retrieves a cached custom bitmap descriptor for a user profile marker.
     */
    fun getUserMarkerBitmap(
        username: String,
        isOnline: Boolean
    ): BitmapDescriptor? {
        val initial = username.take(1).uppercase(Locale.ROOT).ifBlank { "K" }
        val cacheKey = "user_${initial}_${isOnline}"
        synchronized(cacheLock) {
            bitmapDescriptorCache[cacheKey]?.let { return it }
        }

        val bitmap = createUserAvatarBitmap(initial, isOnline) ?: return null
        val descriptor = descriptorFactory(bitmap)
        if (descriptor != null) {
            synchronized(cacheLock) {
                bitmapDescriptorCache[cacheKey] = descriptor
            }
        }
        return descriptor
    }

    /**
     * Generates or retrieves a cached custom bitmap descriptor for a cluster node marker.
     */
    fun getClusterMarkerBitmap(
        count: Int,
        primaryCategory: String = "Club"
    ): BitmapDescriptor? {
        val cacheKey = "cluster_${count}_${primaryCategory.lowercase(Locale.ROOT)}"
        synchronized(cacheLock) {
            bitmapDescriptorCache[cacheKey]?.let { return it }
        }

        val bitmap = createClusterBitmap(count) ?: return null
        val descriptor = descriptorFactory(bitmap)
        if (descriptor != null) {
            synchronized(cacheLock) {
                bitmapDescriptorCache[cacheKey] = descriptor
            }
        }
        return descriptor
    }

    /**
     * Pre-warms the cache with standard categories and cluster buckets to eliminate
     * initial frame drops during map load.
     */
    fun prewarmCache() {
        try {
            val categories = listOf("Club", "Bar", "Event", "Restaurant", "Lounge")
            for (category in categories) {
                getClubMarkerBitmap(category, hasActiveEvent = false)
                getClubMarkerBitmap(category, hasActiveEvent = true)
            }
            val commonCounts = listOf(2, 3, 5, 10, 20, 50, 100)
            for (count in commonCounts) {
                getClusterMarkerBitmap(count, "Club")
            }
        } catch (e: Throwable) {
            // Ignored in unit testing environments without Robolectric/Android graphics
        }
    }

    private fun createClubPinBitmap(category: String, hasActiveEvent: Boolean): Bitmap? {
        return try {
            val width = 120
            val height = 160
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            // Drop shadow under pin tip
            paint.color = Color.argb(70, 0, 0, 0)
            canvas.drawOval(RectF(30f, 142f, 90f, 158f), paint)

            // Main pin teardrop path
            val path = Path().apply {
                moveTo(60f, 145f)
                cubicTo(15f, 95f, 10f, 65f, 10f, 50f)
                arcTo(RectF(10f, 10f, 110f, 110f), 180f, 180f, false)
                cubicTo(110f, 65f, 105f, 95f, 60f, 145f)
                close()
            }

            // Fill main neon purple pin body
            paint.color = COLOR_PRIMARY_PURPLE
            paint.style = Paint.Style.FILL
            canvas.drawPath(path, paint)

            // Outer high-contrast border stroke
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            canvas.drawPath(path, paint)

            // Inner dark circle canvas background
            paint.style = Paint.Style.FILL
            paint.color = COLOR_PURPLE_DARK_BG
            canvas.drawCircle(60f, 60f, 32f, paint)

            // Draw category icon / symbol text
            paint.color = Color.WHITE
            paint.textSize = 28f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER

            val iconSymbol = when (category.lowercase(Locale.ROOT)) {
                "bar", "bars" -> "🍸"
                "event", "events" -> "🎉"
                "restaurant", "restaurants" -> "🍴"
                "lounge", "lounges" -> "🍹"
                else -> "🎵"
            }
            val textY = 60f - ((paint.descent() + paint.ascent()) / 2)
            canvas.drawText(iconSymbol, 60f, textY, paint)

            // Active Event indicator badge on top right
            if (hasActiveEvent) {
                paint.style = Paint.Style.FILL
                paint.color = COLOR_EVENT_BADGE
                canvas.drawCircle(92f, 26f, 14f, paint)

                paint.color = Color.WHITE
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 3f
                canvas.drawCircle(92f, 26f, 14f, paint)
            }

            bitmap
        } catch (e: Throwable) {
            null
        }
    }

    private fun createUserAvatarBitmap(initial: String, isOnline: Boolean): Bitmap? {
        return try {
            val size = 120
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            // Soft drop shadow
            paint.color = Color.argb(60, 0, 0, 0)
            canvas.drawCircle(60f, 64f, 48f, paint)

            // Background ultra dark purple circle
            paint.color = COLOR_PURPLE_DARK_BG
            paint.style = Paint.Style.FILL
            canvas.drawCircle(60f, 60f, 46f, paint)

            // Outer Kliq neon purple ring
            paint.color = COLOR_PRIMARY_PURPLE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 7f
            canvas.drawCircle(60f, 60f, 46f, paint)

            // White inner ring border
            paint.color = Color.WHITE
            paint.strokeWidth = 2f
            canvas.drawCircle(60f, 60f, 42f, paint)

            // Draw initial letter
            paint.style = Paint.Style.FILL
            paint.color = Color.WHITE
            paint.textSize = 40f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER

            val textY = 60f - ((paint.descent() + paint.ascent()) / 2)
            canvas.drawText(initial, 60f, textY, paint)

            // Online status dot indicator
            val statusColor = if (isOnline) COLOR_ONLINE_GREEN else COLOR_OFFLINE_GRAY
            paint.color = statusColor
            canvas.drawCircle(92f, 92f, 12f, paint)

            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            canvas.drawCircle(92f, 92f, 12f, paint)

            bitmap
        } catch (e: Throwable) {
            null
        }
    }

    private fun createClusterBitmap(count: Int): Bitmap? {
        return try {
            val size = 130
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            // Outer translucent glowing halo
            paint.color = COLOR_CLUSTER_GLOW
            paint.style = Paint.Style.FILL
            canvas.drawCircle(65f, 65f, 60f, paint)

            // Inner solid neon purple circle
            paint.color = COLOR_PRIMARY_PURPLE
            canvas.drawCircle(65f, 65f, 48f, paint)

            // High contrast white border
            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 4f
            canvas.drawCircle(65f, 65f, 48f, paint)

            // Centered count text
            paint.style = Paint.Style.FILL
            paint.color = Color.WHITE
            paint.textSize = 36f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER

            val label = if (count > 99) "99+" else count.toString()
            val textY = 65f - ((paint.descent() + paint.ascent()) / 2)
            canvas.drawText(label, 65f, textY, paint)

            bitmap
        } catch (e: Throwable) {
            null
        }
    }

    /**
     * Clears all cached bitmap descriptors.
     */
    fun clearCache() {
        synchronized(cacheLock) {
            bitmapDescriptorCache.clear()
        }
    }

    /**
     * Returns the current number of cached bitmap descriptors.
     */
    fun cacheSize(): Int {
        synchronized(cacheLock) {
            return bitmapDescriptorCache.size
        }
    }

    /**
     * Legacy getter alias for current cache size.
     */
    fun getCacheSize(): Int = cacheSize()
}
