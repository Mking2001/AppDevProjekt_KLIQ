package com.kliq.app.data.model

/**
 * Domain-Modell für einen vom aktuellen Nutzer blockierten Benutzer.
 *
 * @param userId ID des ausführenden Benutzers.
 * @param blockedUserId ID des blockierten Benutzers.
 * @param reason Optionaler Grund für die Blockierung.
 * @param blockedAtTimestampMs Zeitpunkt der Blockierung in Millisekunden.
 */
data class BlockedUser(
    val userId: String,
    val blockedUserId: String,
    val reason: String? = null,
    val blockedAtTimestampMs: Long = System.currentTimeMillis()
)
