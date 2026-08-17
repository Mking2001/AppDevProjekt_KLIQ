package com.kliq.app

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.kliq.app.service.notification.NotificationChannelManager
import com.kliq.app.ui.screens.map.MarkerBitmapHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for Kliq.
 * Configures global notification channels, Coil ImageLoader memory cache bounds,
 * and ComponentCallbacks2 memory trimming to prevent OOM and memory leaks.
 */
@HiltAndroidApp
class KliqApplication : Application(), ImageLoaderFactory, ComponentCallbacks2 {

    @Inject
    lateinit var notificationChannelManager: NotificationChannelManager

    private var imageLoaderInstance: ImageLoader? = null

    override fun onCreate() {
        super.onCreate()
        notificationChannelManager.createNotificationChannels()
        registerComponentCallbacks(this)
    }

    override fun newImageLoader(): ImageLoader {
        val loader = ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("kliq_image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024)
                    .build()
            }
            .crossfade(true)
            .build()
        imageLoaderInstance = loader
        return loader
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW ||
            level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND
        ) {
            imageLoaderInstance?.memoryCache?.clear()
            MarkerBitmapHelper.clearCache()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        imageLoaderInstance?.memoryCache?.clear()
        MarkerBitmapHelper.clearCache()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {}
}
