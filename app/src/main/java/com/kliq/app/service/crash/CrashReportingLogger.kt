package com.kliq.app.service.crash

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Zentrale Logging- und Crash-Reporting-Abstraktion für die Kliq App.
 *
 * Verwaltet benutzerdefinierte Custom Keys (Screen-State, Navigationspfad, Anonymisierte Session-ID),
 * Breadcrumbs sowie das Weiterleiten gefangener nicht-fataler Exceptions an Firebase Crashlytics.
 */
object CrashReportingLogger {

    private val customKeysMap = mutableMapOf<String, String>()

    /**
     * Setzt einen benutzerdefinierten Schlüssel-Wert-Eintrag für Crashlytics.
     * Wendet automatisch den PiiSanitizer an.
     */
    fun setCustomKey(key: String, value: String) {
        val (cleanKey, cleanValue) = PiiSanitizer.sanitizeKeyValue(key, value)
        customKeysMap[cleanKey] = cleanValue

        try {
            FirebaseCrashlytics.getInstance().setCustomKey(cleanKey, cleanValue)
        } catch (e: Throwable) {
            // Fallback falls Firebase im Offline/Mock-Modus läuft
        }
        Timber.d("CustomKey set: %s = %s", cleanKey, cleanValue)
    }

    /**
     * Setzt die anonymisierte Benutzer-ID für Crash-Protokolle.
     */
    fun setAnonymizedUserId(userId: String?) {
        val anonId = PiiSanitizer.anonymizeUserId(userId)
        try {
            FirebaseCrashlytics.getInstance().setUserId(anonId)
        } catch (e: Throwable) {
            // Fallback
        }
        Timber.d("Anonymized UserId set: %s", anonId)
    }

    /**
     * Fügt eine Navigations- oder Zustands-Breadcrumb-Spur hinzu.
     */
    fun logBreadcrumb(message: String) {
        val cleanMsg = PiiSanitizer.sanitize(message)
        try {
            FirebaseCrashlytics.getInstance().log(cleanMsg)
        } catch (e: Throwable) {
            // Fallback
        }
        Timber.i("Breadcrumb: %s", cleanMsg)
    }

    /**
     * Protokolliert eine gefangene (nicht-fatale) Exception.
     */
    fun logNonFatalException(throwable: Throwable, message: String? = null) {
        val cleanMsg = PiiSanitizer.sanitize(message ?: throwable.localizedMessage ?: "Non-fatal exception")
        Timber.e(throwable, "NonFatalException: %s", cleanMsg)

        try {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log("NonFatal: $cleanMsg")
            crashlytics.recordException(throwable)
        } catch (e: Throwable) {
            // Fallback
        }
    }

    /**
     * Gibt den aktuellen In-Memory Status der gesetzten Custom Keys für Unit-Tests zurück.
     */
    fun getCustomKeys(): Map<String, String> = customKeysMap.toMap()

    /**
     * Debug-Trigger: Proviziert eine gefangene (nicht-fatale) Exception (z.B. simulierter Netzwerk-Timeout).
     */
    fun triggerTestNonFatalException(): Throwable {
        val timeoutException = java.net.SocketTimeoutException("Simulated network timeout during event query for user +491512345678")
        logNonFatalException(timeoutException, "Simulated Event API Timeout")
        return timeoutException
    }

    /**
     * Debug-Trigger: Proviziert einen gewollten Absturz (Fatal Crash) zur Verifizierung von Crashlytics Reports.
     */
    fun triggerTestFatalCrash() {
        setCustomKey("crash_trigger_source", "debug_settings_button")
        logBreadcrumb("Fatal crash triggered manually via debug controls")
        throw RuntimeException("Kliq Debug Test Fatal Crash - Provoked for Crashlytics Verification")
    }
}

