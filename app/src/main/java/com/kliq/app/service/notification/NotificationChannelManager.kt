package com.kliq.app.service.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationChannelManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_DIRECT_MESSAGES = "kliq_direct_messages_channel"
        const val CHANNEL_CITY_CHATS = "kliq_city_chats_channel"

        const val CHANNEL_DIRECT_MESSAGES_NAME = "Kliq Direct Messages"
        const val CHANNEL_CITY_CHATS_NAME = "Kliq City Chats"
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val directMessagesChannel = NotificationChannel(
                CHANNEL_DIRECT_MESSAGES,
                CHANNEL_DIRECT_MESSAGES_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Benachrichtigungen für 1-zu-1 Direktnachrichten in Kliq"
                enableLights(true)
                lightColor = Color.parseColor("#9D4EDD")
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setShowBadge(true)
            }

            val cityChatsChannel = NotificationChannel(
                CHANNEL_CITY_CHATS,
                CHANNEL_CITY_CHATS_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Benachrichtigungen für Erwähnungen und Aktivität in Stadt-Chats"
                enableLights(true)
                lightColor = Color.parseColor("#7B2CBF")
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(directMessagesChannel)
            notificationManager.createNotificationChannel(cityChatsChannel)
        }
    }
}
