package com.kliq.app.service.crash

import java.util.regex.Pattern

/**
 * Datenschutz-Hilfsklasse (PII Sanitizer) zur automatischen Bereinigung sensibler Nutzerdaten
 * (Personally Identifiable Information) aus Log-Nachrichten, Custom Keys und Crashlytics-Traces.
 */
object PiiSanitizer {

    // Regex zur Erkennung von Telefonnummern (z.B. +491512345678, 0151-2345678, etc.)
    private val PHONE_PATTERN = Pattern.compile(
        "(\\+\\d{1,3}[-.\\s]?)?\\(?\\d{2,5}\\)?[-.\\s]?\\d{3,5}[-.\\s]?\\d{3,5}"
    )

    // Regex zur Erkennung präziser GPS-Koordinaten (z.B. lat=52.520008, lng=13.404954 oder (52.5200, 13.4049))
    private val GPS_PATTERN = Pattern.compile(
        "(?i)(lat|latitude|lng|longitude|location|coords?)\\s*[:=]\\s*[-+]?\\d{1,3}\\.\\d{4,}"
    )

    // Regex zur Erkennung von E-Mail-Adressen
    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    )

    // Regex zur Erkennung von Auth-Tokens & Passwörtern
    private val TOKEN_PATTERN = Pattern.compile(
        "(?i)(bearer|token|password|auth|secret)\\s*[:=]\\s*[^\\s,\\}\\]]+"
    )

    /**
     * Sanitisiert eine Log-Nachricht und ersetzt alle gefundenen PII-Muster durch anonymisierte Platzhalter.
     *
     * @param rawMessage Die ursprüngliche Log-Nachricht.
     * @return Die bereinigte, DSGVO-konforme Nachricht.
     */
    fun sanitize(rawMessage: String?): String {
        if (rawMessage.isNullOrBlank()) return ""

        var sanitized = rawMessage
        sanitized = EMAIL_PATTERN.matcher(sanitized).replaceAll("[REDACTED_EMAIL]")
        sanitized = GPS_PATTERN.matcher(sanitized).replaceAll("$1=[REDACTED_GPS]")
        sanitized = PHONE_PATTERN.matcher(sanitized).replaceAll("[REDACTED_PHONE]")
        sanitized = TOKEN_PATTERN.matcher(sanitized).replaceAll("$1=[REDACTED_TOKEN]")

        return sanitized
    }

    /**
     * Sanitisiert einen benutzerdefinierten Schlüssel-Wert-Eintrag für Crashlytics.
     */
    fun sanitizeKeyValue(key: String, value: String): Pair<String, String> {
        val cleanKey = key.trim().lowercase()
        val cleanValue = sanitize(value)
        return Pair(cleanKey, cleanValue)
    }

    /**
     * Anonymisiert eine User-ID für Crash-Reports (z. B. durch Hash oder Prefix-Maskierung).
     */
    fun anonymizeUserId(userId: String?): String {
        if (userId.isNullOrBlank()) return "anonymous"
        return "user_anon_${userId.hashCode() and 0x00FFFFFF}"
    }
}
