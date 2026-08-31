package com.kliq.app.service.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kliq.app.data.model.ChatPushPayload
import com.kliq.app.data.model.PushNotificationType
import com.kliq.app.data.repository.PushNotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KliqFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var pushNotificationRepository: PushNotificationRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val payload = ChatPushPayload.fromMap(remoteMessage.data)
        if (payload.chatId.isNotBlank()) {
            val isDmEnabled = pushNotificationRepository.isDirectMessagesEnabled()
            val isCityEnabled = pushNotificationRepository.isCityChatsEnabled()

            val shouldShow = when (payload.notificationType) {
                PushNotificationType.DIRECT_MESSAGE -> isDmEnabled
                PushNotificationType.CITY_CHAT_MENTION -> isCityEnabled
            }

            if (shouldShow) {
                serviceScope.launch {
                    pushNotificationRepository.onPushPayloadReceived(payload)
                }
                notificationHelper.showChatNotification(payload)
            }
        } else {

            val title = remoteMessage.notification?.title
                ?: remoteMessage.data["title"]
                ?: remoteMessage.data["sender_name"]
                ?: "Kliq"
            val body = remoteMessage.notification?.body
                ?: remoteMessage.data["body"]
                ?: remoteMessage.data["message"]
                ?: remoteMessage.data["message_text"]
                ?: ""

            if (body.isNotBlank() || title.isNotBlank()) {
                notificationHelper.showGeneralNotification(title, body)
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            pushNotificationRepository.updateFcmToken(token)
        }
    }
}
