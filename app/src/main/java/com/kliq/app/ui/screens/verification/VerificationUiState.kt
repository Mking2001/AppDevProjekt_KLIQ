package com.kliq.app.ui.screens.verification

/**
 * Zustandsmodell für den SMS-Verifizierungs-Flow.
 *
 * Bildet die vier möglichen Zustände der Verifizierung ab:
 * Wartend, Laden, Erfolg und Fehler mit konkreter Fehlermeldung.
 */
sealed interface VerificationUiState {

    /** Initialzustand — Nutzer hat noch keinen Code abgesendet. */
    data object Idle : VerificationUiState

    /** Verifizierung läuft gerade (Server-Anfrage aktiv). */
    data object Loading : VerificationUiState

    /** Code wurde erfolgreich verifiziert. */
    data object Success : VerificationUiState

    /** Verifizierung fehlgeschlagen mit konkreter Fehlermeldung für den Nutzer. */
    data class Error(val message: String) : VerificationUiState
}

/**
 * Zustand des Resend-Timers für erneutes Senden des Verifizierungscodes.
 *
 * @property secondsRemaining Verbleibende Sekunden bis der Code erneut gesendet werden kann.
 * @property canResend Ob der Nutzer gerade einen neuen Code anfordern kann.
 */
data class ResendTimerState(
    val secondsRemaining: Int = 0,
    val canResend: Boolean = true
)
