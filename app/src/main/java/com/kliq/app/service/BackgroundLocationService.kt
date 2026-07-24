package com.kliq.app.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kliq.app.MainActivity
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.data.repository.LocationRepository
import com.kliq.app.util.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Android Foreground Service handling background location tracking for Kliq.
 *
 * Implements an adaptive battery-saving interval algorithm (50m displacement filter, 1 to 5 min intervals)
 * and displays a persistent Kliq High-Contrast notification while active.
 */
@AndroidEntryPoint
class BackgroundLocationService : Service() {

    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var permissionManager: PermissionManager

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var isServiceRunning = false

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val lastLocation = result.lastLocation ?: return
                val locationData = LocationData(
                    latitude = lastLocation.latitude,
                    longitude = lastLocation.longitude,
                    accuracy = lastLocation.accuracy,
                    timestampMs = lastLocation.time,
                    speed = lastLocation.speed,
                    isMock = lastLocation.isFromMockProvider
                )

                serviceScope.launch {
                    locationRepository.recordLocationUpdate(locationData)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTrackingService()
            ACTION_STOP -> stopTrackingService()
        }
        return START_STICKY
    }

    private fun startTrackingService() {
        if (isServiceRunning) return

        val permissionState = permissionManager.checkBackgroundLocationPermission(this)
        if (permissionState != LocationPermissionState.Granted) {
            stopTrackingService()
            return
        }

        val notification = buildForegroundNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        requestAdaptiveLocationUpdates()
        isServiceRunning = true
    }

    @SuppressLint("MissingPermission")
    private fun requestAdaptiveLocationUpdates() {
        // Battery-optimized adaptive location request:
        // Update interval: 60 sec (1 min) to 300 sec (5 min) with min displacement of 50m
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            INTERVAL_ACTIVE_MS
        ).apply {
            setMinUpdateIntervalMillis(FASTEST_INTERVAL_MS)
            setMinUpdateDistanceMeters(MIN_DISTANCE_DISPLACEMENT_METERS)
            setMaxUpdateDelayMillis(MAX_UPDATE_DELAY_MS)
        }.build()

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            stopTrackingService()
        }
    }

    private fun stopTrackingService() {
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (e: Exception) {
            // Ignore clean teardown exceptions
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        isServiceRunning = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notification displayed while Kliq live background location is enabled."
                enableLights(true)
                lightColor = Color.parseColor("#7C4DFF")
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Kliq Live-Standort aktiv")
            .setContentText("Dein Standort wird für Nightlife-Features & Kliq-Circle aktualisiert.")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setColor(Color.parseColor("#7C4DFF"))
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "com.kliq.app.service.ACTION_START_LOCATION_TRACKING"
        const val ACTION_STOP = "com.kliq.app.service.ACTION_STOP_LOCATION_TRACKING"

        private const val NOTIFICATION_ID = 4301
        private const val CHANNEL_ID = "kliq_location_channel"
        private const val CHANNEL_NAME = "Kliq Live Location Services"

        private const val INTERVAL_ACTIVE_MS = 60_000L // 1 Minute
        private const val FASTEST_INTERVAL_MS = 30_000L // 30 Sekunden
        private const val MAX_UPDATE_DELAY_MS = 300_000L // 5 Minuten
        private const val MIN_DISTANCE_DISPLACEMENT_METERS = 50f // 50 Meter Adaptionsschwelle
    }
}
