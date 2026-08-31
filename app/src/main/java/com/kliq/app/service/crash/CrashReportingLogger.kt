package com.kliq.app.service.crash

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

object CrashReportingLogger {

    private val customKeysMap = mutableMapOf<String, String>()

    fun setCustomKey(key: String, value: String) {
        val (cleanKey, cleanValue) = PiiSanitizer.sanitizeKeyValue(key, value)
        customKeysMap[cleanKey] = cleanValue

        try {
            FirebaseCrashlytics.getInstance().setCustomKey(cleanKey, cleanValue)
        } catch (e: Throwable) {

        }
        Timber.d("CustomKey set: %s = %s", cleanKey, cleanValue)
    }

    fun setAnonymizedUserId(userId: String?) {
        val anonId = PiiSanitizer.anonymizeUserId(userId)
        try {
            FirebaseCrashlytics.getInstance().setUserId(anonId)
        } catch (e: Throwable) {

        }
        Timber.d("Anonymized UserId set: %s", anonId)
    }

    fun logBreadcrumb(message: String) {
        val cleanMsg = PiiSanitizer.sanitize(message)
        try {
            FirebaseCrashlytics.getInstance().log(cleanMsg)
        } catch (e: Throwable) {

        }
        Timber.i("Breadcrumb: %s", cleanMsg)
    }

    fun logNonFatalException(throwable: Throwable, message: String? = null) {
        val cleanMsg = PiiSanitizer.sanitize(message ?: throwable.localizedMessage ?: "Non-fatal exception")
        Timber.e(throwable, "NonFatalException: %s", cleanMsg)

        try {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log("NonFatal: $cleanMsg")
            crashlytics.recordException(throwable)
        } catch (e: Throwable) {

        }
    }

    fun getCustomKeys(): Map<String, String> = customKeysMap.toMap()

    fun triggerTestNonFatalException(): Throwable {
        val timeoutException = java.net.SocketTimeoutException("Simulated network timeout during event query for user +491512345678")
        logNonFatalException(timeoutException, "Simulated Event API Timeout")
        return timeoutException
    }

    fun triggerTestFatalCrash() {
        setCustomKey("crash_trigger_source", "debug_settings_button")
        logBreadcrumb("Fatal crash triggered manually via debug controls")
        throw RuntimeException("Kliq Debug Test Fatal Crash - Provoked for Crashlytics Verification")
    }
}
