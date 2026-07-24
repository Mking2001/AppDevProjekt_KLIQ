package com.kliq.app.ui.screens.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.util.LruCache
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.util.Locale

/**
 * Utility helper providing custom bitmap generation and memory caching for map markers.
 * Handles rendering of distinct Kliq purple club pins, user avatar markers, and cluster nodes.
 */
object MarkerBitmapHelper {

    private const val CACHE_SIZE = 128
    private val bitmapDescriptorCache = LruCache<String, BitmapDescriptor>(CACHE_SIZE)

    private const val COLOR_PRIMARY_PURPLE = 0xFF6B46C1.toInt()
    private const val COLOR_PURPLE_DARK_BG = 0xFF2D1B4E.toInt()
    private const val COLOR_EVENT_BADGE = 0xFFEC4899.toInt()
    private const val COLOR_ONLINE_GREEN = 0xFF10B981.toInt()
    private const val COLOR_OFFLINE_GRAY = 0xFF9CA3AF.toInt()

    internal var descriptorFactory: (Bitmap) -> BitmapDescriptor = { bitmap ->
        try {
            BitmapDescriptorFactory.fromBitmap(bitmap)
        } catch (e: Throwable) {
            BitmapDescriptorFactory.defaultMarker()
        }
    }

    /**
     * Generates or retrieves a cached custom bitmap descriptor for a club venue marker.
     */
    fun getClubMarkerBitmap(
        category: String,
        hasActiveEvent: Boolean
    ): BitmapDescriptor {
        val cacheKey = "club_${category.lowercase(Locale.ROOT)}_${hasActiveEvent}"
        bitmapDescriptorCache.get(cacheKey)?.let { return it }

        val bitmap = createClubPinBitmap(category, hasActiveEvent)
        val descriptor = descriptorFactory(bitmap)
        bitmapDescriptorCache.put(cacheKey, descriptor)
        return descriptor
    }

    /**
     * Generates or retrieves a cached custom bitmap descriptor for a user profile marker.
     */
    fun getUserMarkerBitmap(
        username: String,
        isOnline: Boolean
    ): BitmapDescriptor {
        val initial = username.take(1).uppercase(Locale.ROOT).ifBlank { "K" }
        val cacheKey = "user_${initial}_${isOnline}"
        bitmapDescriptorCache.get(cacheKey)?.let { return it }

        val bitmap = createUserAvatarBitmap(initial, isOnline)
        val descriptor = descriptorFactory(bitmap)
        bitmapDescriptorCache.put(cacheKey, descriptor)
        return descriptor
    }

    /**
     * Generates or retrieves a cached custom bitmap descriptor for a cluster node marker.
     */
    fun getClusterMarkerBitmap(
        count: Int,
        primaryCategory: String = "Club"
    ): BitmapDescriptor {
        val cacheKey = "cluster_${count}_${primaryCategory.lowercase(Locale.ROOT)}"
        bitmapDescriptorCache.get(cacheKey)?.let { return it }

        val bitmap = createClusterBitmap(count)
        val descriptor = descriptorFactory(bitmap)
        bitmapDescriptorCache.put(cacheKey, descriptor)
        return descriptor
    }

    private fun createClubPinBitmap(category: String, hasActiveEvent: Boolean): Bitmap {
        val width = 120
        val height = 160
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Drop shadow under pin
        paint.color = Color.argb(60, 0, 0, 0)
        canvas.drawOval(RectF(30f, 142f, 90f, 158f), paint)

        // Main pin teardrop path
        val path = Path().apply {
            moveTo(60f, 145f)
            cubicTo(15f, 95f, 10f, 65f, 10f, 50f)
            arcTo(RectF(10f, 10f, 110f, 110f), 180f, 180f, false)
            cubicTo(110f, 65f, 105f, 95f, 60f, 145f)
            close()
        }

        // Fill main purple pin body
        paint.color = COLOR_PRIMARY_PURPLE
        paint.style = Paint.Style.FILL
        canvas.drawPath(path, paint)

        // Outer border stroke
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawPath(path, paint)

        // Inner circle canvas background
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
            else -> "🎵"
        }
        val textY = 60f - ((paint.descent() + paint.ascent()) / 2)
        canvas.drawText(iconSymbol, 60f, textY, paint)

        // Event indicator badge on top right
        if (hasActiveEvent) {
            paint.style = Paint.Style.FILL
            paint.color = COLOR_EVENT_BADGE
            canvas.drawCircle(92f, 26f, 14f, paint)

            paint.color = Color.WHITE
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            canvas.drawCircle(92f, 26f, 14f, paint)
        }

        return bitmap
    }

    private fun createUserAvatarBitmap(initial: String, isOnline: Boolean): Bitmap {
        val size = 120
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Shadow circle
        paint.color = Color.argb(50, 0, 0, 0)
        canvas.drawCircle(60f, 64f, 48f, paint)

        // Background dark purple circle
        paint.color = COLOR_PURPLE_DARK_BG
        paint.style = Paint.Style.FILL
        canvas.drawCircle(60f, 60f, 46f, paint)

        // Outer Kliq purple ring
        paint.color = COLOR_PRIMARY_PURPLE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 7f
        canvas.drawCircle(60f, 60f, 46f, paint)

        // White inner border
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

        return bitmap
    }

    private fun createClusterBitmap(count: Int): Bitmap {
        val size = 130
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Outer translucent ring
        paint.color = Color.argb(80, 107, 70, 193)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(65f, 65f, 60f, paint)

        // Inner solid circle
        paint.color = COLOR_PRIMARY_PURPLE
        canvas.drawCircle(65f, 65f, 48f, paint)

        // White border
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawCircle(65f, 65f, 48f, paint)

        // Text count
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = 36f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER

        val label = if (count > 99) "99+" else count.toString()
        val textY = 65f - ((paint.descent() + paint.ascent()) / 2)
        canvas.drawText(label, 65f, textY, paint)

        return bitmap
    }

    /**
     * Clears all cached bitmap descriptors.
     */
    fun clearCache() {
        bitmapDescriptorCache.evictAll()
    }
}
