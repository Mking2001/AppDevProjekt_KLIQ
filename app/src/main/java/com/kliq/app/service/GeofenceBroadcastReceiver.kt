package com.kliq.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.kliq.app.R
import com.kliq.app.data.model.GeofenceTransitionType
import com.kliq.app.data.repository.GeofenceRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var geofenceRepository: GeofenceRepository

    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var hapticFeedbackManager: com.kliq.app.util.HapticFeedbackManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_GEOFENCE_EVENT && intent.action != GEOFENCE_TRANSITION_ACTION) {
            return
        }

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            Log.e(TAG, "GeofencingEvent is null")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, "Geofence event error: $errorMessage")
            return
        }

        val geofenceTransition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences ?: emptyList()

        if (triggeringGeofences.isEmpty()) {
            Log.w(TAG, "No triggering geofences in event")
            return
        }

        val pendingResult = goAsync()
        val dispatcher = if (::ioDispatcher.isInitialized) ioDispatcher else Dispatchers.IO

        CoroutineScope(dispatcher).launch {
            try {
                for (geofence in triggeringGeofences) {
                    val clubId = geofence.requestId
                    when (geofenceTransition) {
                        Geofence.GEOFENCE_TRANSITION_ENTER -> {
                            Log.d(TAG, "Geofence ENTER triggered for clubId: $clubId")
                            if (::hapticFeedbackManager.isInitialized) {
                                hapticFeedbackManager.performConfirm()
                            }
                            geofenceRepository.handleGeofenceTransition(clubId, GeofenceTransitionType.ENTER)
                            sendLocationVerificationNotification(context, clubId, isEntering = true)
                        }

                        Geofence.GEOFENCE_TRANSITION_EXIT -> {
                            Log.d(TAG, "Geofence EXIT triggered for clubId: $clubId")
                            geofenceRepository.handleGeofenceTransition(clubId, GeofenceTransitionType.EXIT)
                            sendLocationVerificationNotification(context, clubId, isEntering = false)
                        }

                        Geofence.GEOFENCE_TRANSITION_DWELL -> {
                            Log.d(TAG, "Geofence DWELL triggered for clubId: $clubId")
                            geofenceRepository.handleGeofenceTransition(clubId, GeofenceTransitionType.DWELL)
                        }

                        else -> {
                            Log.w(TAG, "Unknown geofence transition: $geofenceTransition")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing geofence transition", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun sendLocationVerificationNotification(context: Context, clubId: String, isEntering: Boolean) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Kliq Geofence Verification",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Benachrichtigungen bei Club-Eintritt zur Location-Verifizierung"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val title = if (isEntering) "Club-Status aktiviert! 📍" else "Club verlassen"
        val message = if (isEntering) {
            "Du bist im Club-Bereich. Verifiziertes Bewertungssystem & Besucht-Historie sind freigeschaltet."
        } else {
            "Geofence verlassen. Standort-Verifizierung wurde zurückgesetzt."
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_OFFSET + clubId.hashCode(), notification)
    }

    companion object {
        const val TAG = "GeofenceReceiver"
        const val ACTION_GEOFENCE_EVENT = "com.kliq.app.ACTION_GEOFENCE_EVENT"
        const val GEOFENCE_TRANSITION_ACTION = "com.kliq.app.GEOFENCE_TRANSITION_ACTION"
        const val CHANNEL_ID = "kliq_geofence_channel"
        private const val NOTIFICATION_ID_OFFSET = 46500
    }
}
