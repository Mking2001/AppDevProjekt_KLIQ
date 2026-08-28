package com.kliq.app.service.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.kliq.app.MainActivity
import com.kliq.app.R
import com.kliq.app.data.model.ChatPushPayload
import com.kliq.app.data.model.PushNotificationType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val DEEP_LINK_SCHEME = "kliq"
        const val DEEP_LINK_HOST = "chat"

        fun createDeepLinkUri(chatId: String, senderId: String, type: String): Uri {
            return Uri.Builder()
                .scheme(DEEP_LINK_SCHEME)
                .authority(DEEP_LINK_HOST)
                .appendPath(chatId)
                .appendQueryParameter("senderId", senderId)
                .appendQueryParameter("type", type)
                .build()
        }
    }

    fun showChatNotification(payload: ChatPushPayload) {
        val channelId = when (payload.notificationType) {
            PushNotificationType.DIRECT_MESSAGE -> NotificationChannelManager.CHANNEL_DIRECT_MESSAGES
            PushNotificationType.CITY_CHAT_MENTION -> NotificationChannelManager.CHANNEL_CITY_CHATS
        }

        val deepLinkUri = createDeepLinkUri(
            chatId = payload.chatId,
            senderId = payload.senderId,
            type = payload.notificationType.rawType
        )

        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = deepLinkUri
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(ChatPushPayload.KEY_CHAT_ID, payload.chatId)
            putExtra(ChatPushPayload.KEY_SENDER_ID, payload.senderId)
            putExtra(ChatPushPayload.KEY_SENDER_NAME, payload.senderName)
            putExtra(ChatPushPayload.KEY_PREVIEW_TEXT, payload.previewText)
            putExtra(ChatPushPayload.KEY_NOTIFICATION_TYPE, payload.notificationType.rawType)
        }

        val notificationId = payload.chatId.hashCode()

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setColor(Color.parseColor("#9D4EDD"))
            .setContentTitle(payload.senderName)
            .setContentText(payload.previewText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(payload.previewText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Notification permission might not be granted by user yet
        }
    }

    /**
     * Zeigt eine allgemeine Benachrichtigung an (z. B. aus der Firebase-Konsole oder Broadcast-Nachrichten).
     */
    fun showGeneralNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_DIRECT_MESSAGES)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setColor(Color.parseColor("#9D4EDD"))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Notification permission might not be granted by user yet
        }
    }
}

