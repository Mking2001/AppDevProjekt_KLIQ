package com.kliq.app.service.crash

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Benutzerdefinierter Timber [Tree] für Android, der Warnungen, Fehler und ungebundene Exceptions
 * automatisch durch den [PiiSanitizer] leitet und an Firebase Crashlytics weitergibt.
 */
class KliqCrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Nur WARN, ERROR und ASSERT Nachrichten an Crashlytics weiterleiten
        if (priority < Log.WARN) {
            return
        }

        // PII-Datenschutzprüfung & Maskierung
        val sanitizedMessage = PiiSanitizer.sanitize(message)
        val tagPrefix = if (tag != null) "[$tag] " else ""
        val formattedLog = "$tagPrefix$sanitizedMessage"

        try {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log(formattedLog)

            if (t != null) {
                crashlytics.recordException(t)
            }
        } catch (e: Throwable) {
            // Fallback falls Firebase SDK im Test/Offline-Modus nicht initialisiert ist
        }
    }
}
