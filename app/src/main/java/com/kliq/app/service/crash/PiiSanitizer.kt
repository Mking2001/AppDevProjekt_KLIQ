package com.kliq.app.service.crash

import java.util.regex.Pattern

object PiiSanitizer {

    private val PHONE_PATTERN = Pattern.compile(
        "(\\+\\d{1,3}[-.\\s]?)?\\(?\\d{2,5}\\)?[-.\\s]?\\d{3,5}[-.\\s]?\\d{3,5}"
    )

    private val GPS_PATTERN = Pattern.compile(
        "(?i)(lat|latitude|lng|longitude|location|coords?)\\s*[:=]\\s*[-+]?\\d{1,3}\\.\\d{4,}"
    )

    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    )

    private val TOKEN_PATTERN = Pattern.compile(
        "(?i)(bearer|token|password|auth|secret)\\s*[:=]\\s*[^\\s,\\}\\]]+"
    )

    fun sanitize(rawMessage: String?): String {
        if (rawMessage.isNullOrBlank()) return ""

        var sanitized = rawMessage
        sanitized = EMAIL_PATTERN.matcher(sanitized).replaceAll("[REDACTED_EMAIL]")
        sanitized = GPS_PATTERN.matcher(sanitized).replaceAll("$1=[REDACTED_GPS]")
        sanitized = PHONE_PATTERN.matcher(sanitized).replaceAll("[REDACTED_PHONE]")
        sanitized = TOKEN_PATTERN.matcher(sanitized).replaceAll("$1=[REDACTED_TOKEN]")

        return sanitized
    }

    fun sanitizeKeyValue(key: String, value: String): Pair<String, String> {
        val cleanKey = key.trim().lowercase()
        val cleanValue = sanitize(value)
        return Pair(cleanKey, cleanValue)
    }

    fun anonymizeUserId(userId: String?): String {
        if (userId.isNullOrBlank()) return "anonymous"
        return "user_anon_${userId.hashCode() and 0x00FFFFFF}"
    }
}
