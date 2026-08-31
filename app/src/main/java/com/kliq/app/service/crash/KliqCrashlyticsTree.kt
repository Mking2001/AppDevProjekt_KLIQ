package com.kliq.app.service.crash

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class KliqCrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        if (priority < Log.WARN) {
            return
        }

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

        }
    }
}
