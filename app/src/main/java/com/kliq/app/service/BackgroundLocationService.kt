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
import com.kliq.app.MainActivity
import com.kliq.app.data.model.LocationData
import com.kliq.app.data.model.LocationPermissionState
import com.kliq.app.data.model.LocationPowerPolicy
import com.kliq.app.data.model.LocationTrackingMode
import com.kliq.app.data.repository.LocationRepository
import com.kliq.app.util.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Android Foreground Service handling battery-optimized background location tracking for Kliq.
 *
 * Coordinates adaptive sampling policies (High-Accuracy burst, Balanced Ambient, and Idle Passive),
 * dynamic FusedLocationProviderClient re-registration, and persistent High-Contrast notification updates.
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
    private var activePolicy: LocationPowerPolicy = LocationPowerPolicy.BalancedAmbient

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val lastLocation = result.lastLocation ?: return
                val isMockLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    lastLocation.isMock
                } else {
                    @Suppress("DEPRECATION")
                    lastLocation.isFromMockProvider
                }
                val locationData = LocationData(
                    latitude = lastLocation.latitude,
                    longitude = lastLocation.longitude,
                    accuracy = lastLocation.accuracy,
                    timestampMs = lastLocation.time,
                    speed = lastLocation.speed,
                    isMock = isMockLocation
                )

                serviceScope.launch {
                    locationRepository.recordLocationUpdate(locationData)
                }
            }
        }

        observePolicyChanges()
    }

    private fun observePolicyChanges() {
        locationRepository.powerPolicy
            .onEach { newPolicy ->
                if (isServiceRunning && newPolicy != activePolicy) {
                    activePolicy = newPolicy
                    applyAdaptiveLocationPolicy(newPolicy)
                    updateForegroundNotification()
                }
            }
            .launchIn(serviceScope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTrackingService()
            ACTION_STOP -> stopTrackingService()
            ACTION_UPDATE_POWER_POLICY -> {
                if (isServiceRunning) {
                    applyAdaptiveLocationPolicy(locationRepository.powerPolicy.value)
                    updateForegroundNotification()
                }
            }
            ACTION_REQUEST_BURST -> {
                val durationMs = intent.getLongExtra(EXTRA_BURST_DURATION_MS, 30_000L)
                locationRepository.requestHighAccuracyBurst(durationMs)
            }
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

        activePolicy = locationRepository.powerPolicy.value
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

        applyAdaptiveLocationPolicy(activePolicy)
        isServiceRunning = true
    }

    @SuppressLint("MissingPermission")
    private fun applyAdaptiveLocationPolicy(policy: LocationPowerPolicy) {
        // Dynamically reconfigures location updates to match the current energy policy
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback)

            val locationRequest = LocationRequest.Builder(
                policy.priority,
                policy.intervalMillis
            ).apply {
                setMinUpdateIntervalMillis(policy.minUpdateIntervalMillis)
                setMinUpdateDistanceMeters(policy.minDistanceDisplacementMeters)
                setMaxUpdateDelayMillis(policy.maxUpdateDelayMillis)
            }.build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            stopTrackingService()
        }
    }

    private fun updateForegroundNotification() {
        if (!isServiceRunning) return
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, buildForegroundNotification())
        } catch (e: Exception) {
            // Ignore notification update failures during teardown
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

        val currentMode = locationRepository.trackingMode.value
        val (titleText, bodyText) = when (currentMode) {
            LocationTrackingMode.HIGH_ACCURACY -> {
                val burstRemaining = locationRepository.burstRemainingSeconds.value
                Pair(
                    "Kliq GPS-Präzision aktiv",
                    "High-Accuracy Verifizierung aktiv (${burstRemaining}s verbleibend)"
                )
            }
            LocationTrackingMode.BALANCED_AMBIENT -> {
                Pair(
                    "Kliq Live-Standort aktiv",
                    "Adaptives Tracking (>50m / 1-2 min) für Nightlife-Circle & Party-Map."
                )
            }
            LocationTrackingMode.IDLE_PASSIVE -> {
                Pair(
                    "Kliq Standby (Batteriesparmodus)",
                    "Drosselung aktiv: Geofence-basierte Trigger & passive Standortüberwachung."
                )
            }
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(titleText)
            .setContentText(bodyText)
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
        const val ACTION_UPDATE_POWER_POLICY = "com.kliq.app.service.ACTION_UPDATE_POWER_POLICY"
        const val ACTION_REQUEST_BURST = "com.kliq.app.service.ACTION_REQUEST_BURST"

        const val EXTRA_BURST_DURATION_MS = "extra_burst_duration_ms"

        private const val NOTIFICATION_ID = 4301
        private const val CHANNEL_ID = "kliq_location_channel"
        private const val CHANNEL_NAME = "Kliq Live Location Services"
    }
}
