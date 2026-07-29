package com.kliq.app.data.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.kliq.app.data.model.ChatPushPayload
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushNotificationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) : PushNotificationRepository {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "kliq_push_notification_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val _incomingPushPayloads = MutableSharedFlow<ChatPushPayload>(extraBufferCapacity = 64)
    override val incomingPushPayloads: Flow<ChatPushPayload> = _incomingPushPayloads.asSharedFlow()

    private val _fcmToken = MutableStateFlow<String?>(getStoredTokenInternal())
    override val fcmToken: StateFlow<String?> = _fcmToken.asStateFlow()

    override suspend fun updateFcmToken(token: String) = withContext(ioDispatcher) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
        _fcmToken.value = token
    }

    override suspend fun getStoredFcmToken(): String? = withContext(ioDispatcher) {
        getStoredTokenInternal()
    }

    private fun getStoredTokenInternal(): String? {
        return prefs.getString(KEY_FCM_TOKEN, null)
    }

    override suspend fun onPushPayloadReceived(payload: ChatPushPayload) = withContext(ioDispatcher) {
        _incomingPushPayloads.emit(payload)
    }

    override suspend fun setDirectMessagesEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        prefs.edit().putBoolean(KEY_DM_ENABLED, enabled).apply()
    }

    override suspend fun setCityChatsEnabled(enabled: Boolean) = withContext(ioDispatcher) {
        prefs.edit().putBoolean(KEY_CITY_ENABLED, enabled).apply()
    }

    override fun isDirectMessagesEnabled(): Boolean {
        return prefs.getBoolean(KEY_DM_ENABLED, true)
    }

    override fun isCityChatsEnabled(): Boolean {
        return prefs.getBoolean(KEY_CITY_ENABLED, true)
    }

    companion object {
        private const val KEY_FCM_TOKEN = "fcm_push_token"
        private const val KEY_DM_ENABLED = "push_direct_messages_enabled"
        private const val KEY_CITY_ENABLED = "push_city_chats_enabled"
    }
}
