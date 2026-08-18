package com.kliq.app

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.kliq.app.service.crash.CrashReportingLogger
import com.kliq.app.service.crash.KliqCrashlyticsTree
import com.kliq.app.service.notification.NotificationChannelManager
import com.kliq.app.ui.screens.map.MarkerBitmapHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

/**
 * Application class for Kliq.
 * Configures global notification channels, Coil ImageLoader memory cache bounds,
 * ComponentCallbacks2 memory trimming, and async Crashlytics / Timber error reporting.
 */
@HiltAndroidApp
class KliqApplication : Application(), ImageLoaderFactory, ComponentCallbacks2 {

    @Inject
    lateinit var notificationChannelManager: NotificationChannelManager

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var imageLoaderInstance: ImageLoader? = null

    override fun onCreate() {
        super.onCreate()
        notificationChannelManager.createNotificationChannels()
        registerComponentCallbacks(this)
        initCrashReportingAsync()
    }

    /**
     * Initialisiert Timber und das Crashlytics-Logging-Tree asynchron im Hintergrund,
     * ohne den Haupt-Thread oder die Kaltstart-Performance der App zu blockieren.
     */
    private fun initCrashReportingAsync() {
        applicationScope.launch {
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
            Timber.plant(KliqCrashlyticsTree())

            // Setze initiale anonymisierte Sitzungs- und System-Metadaten
            val sessionId = UUID.randomUUID().toString().take(8)
            CrashReportingLogger.setCustomKey("session_id", sessionId)
            CrashReportingLogger.setCustomKey("app_version", BuildConfig.VERSION_NAME)
            CrashReportingLogger.setCustomKey("build_type", BuildConfig.BUILD_TYPE)
            CrashReportingLogger.logBreadcrumb("App initialization completed")

            // Firebase Analytics Event
            val analytics = com.google.firebase.analytics.FirebaseAnalytics.getInstance(this@KliqApplication)
            val bundle = android.os.Bundle().apply {
                putString(com.google.firebase.analytics.FirebaseAnalytics.Param.ITEM_NAME, "app_startup")
                putString("session_id", sessionId)
            }
            analytics.logEvent(com.google.firebase.analytics.FirebaseAnalytics.Event.APP_OPEN, bundle)

            // Globales Topic "all" für Sofort-Pushs abonnieren
            try {
                com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("all")
                com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.d("Kliq FCM Token: %s", task.result)
                    }
                }
            } catch (e: Exception) {
                Timber.w(e, "Fehler beim Abonnieren des FCM-Topics")
            }
        }
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
