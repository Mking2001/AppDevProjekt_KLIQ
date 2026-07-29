package com.kliq.app.service.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kliq.app.data.model.ChatPushPayload
import com.kliq.app.data.repository.PushNotificationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PushTestReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PushTestReceiverEntryPoint {
        fun notificationHelper(): NotificationHelper
        fun pushNotificationRepository(): PushNotificationRepository
    }

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_SIMULATE_PUSH) {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                PushTestReceiverEntryPoint::class.java
            )
            val notificationHelper = entryPoint.notificationHelper()
            val pushNotificationRepository = entryPoint.pushNotificationRepository()

            val dataMap = mutableMapOf<String, String>()
            intent.extras?.keySet()?.forEach { key ->
                intent.getStringExtra(key)?.let { value ->
                    dataMap[key] = value
                }
            }

            val payload = ChatPushPayload.fromMap(dataMap)
            if (payload.chatId.isNotBlank()) {
                receiverScope.launch {
                    pushNotificationRepository.onPushPayloadReceived(payload)
                }
                notificationHelper.showChatNotification(payload)
            }
        }
    }

    companion object {
        const val ACTION_SIMULATE_PUSH = "com.kliq.app.ACTION_SIMULATE_PUSH"
    }
}
