package com.kliq.app.data.local.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Contract for secure local session persistence.
 */
interface SessionStorage {
    fun saveSession(token: String, userId: String)
    fun getAuthToken(): String?
    fun getUserId(): String?
    fun isSessionActive(): Boolean
    fun clearSession()
}

/**
 * Platform-specific encrypted implementation using [EncryptedSharedPreferences]
 * backed by Android KeyStore [MasterKey] (AES256_GCM / AES256_SIV).
 */
@Singleton
class EncryptedSessionStorage @Inject constructor(
    @ApplicationContext private val context: Context
) : SessionStorage {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val sharedPreferences: SharedPreferences by lazy {
        try {
            EncryptedSharedPreferences.create(
                context,
                PREFS_FILENAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback for edge cases where KeyStore key fails to decrypt existing file
            context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE)
        }
    }

    override fun saveSession(token: String, userId: String) {
        sharedPreferences.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .putString(KEY_USER_ID, userId)
            .putBoolean(KEY_SESSION_ACTIVE, true)
            .apply()
    }

    override fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    override fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    override fun isSessionActive(): Boolean {
        return sharedPreferences.getBoolean(KEY_SESSION_ACTIVE, false)
    }

    override fun clearSession() {
        sharedPreferences.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_SESSION_ACTIVE)
            .apply()
    }

    companion object {
        private const val PREFS_FILENAME = "kliq_secure_session_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_SESSION_ACTIVE = "session_active"
    }
}
