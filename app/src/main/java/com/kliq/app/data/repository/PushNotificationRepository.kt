package com.kliq.app.data.repository

import com.kliq.app.data.model.ChatPushPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PushNotificationRepository {
    val incomingPushPayloads: Flow<ChatPushPayload>
    val fcmToken: StateFlow<String?>

    suspend fun updateFcmToken(token: String)
    suspend fun getStoredFcmToken(): String?
    suspend fun onPushPayloadReceived(payload: ChatPushPayload)
    suspend fun setDirectMessagesEnabled(enabled: Boolean)
    suspend fun setCityChatsEnabled(enabled: Boolean)
    fun isDirectMessagesEnabled(): Boolean
    fun isCityChatsEnabled(): Boolean
}
