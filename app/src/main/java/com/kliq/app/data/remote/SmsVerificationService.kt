package com.kliq.app.data.remote

/**
 * Abstraktion für den SMS-Verifizierungsdienst.
 *
 * Definiert die beiden Kern-Operationen des OTP-Flows:
 * Code senden und Code verifizieren. Die Implementierung
 * kann gegen einen echten Provider (Firebase Auth, Twilio)
 * oder einen Mock-Service ausgetauscht werden.
 */
interface SmsVerificationService {

    /**
     * Sendet einen Verifizierungscode an die gegebene Telefonnummer.
     *
     * @param phoneNumber Vollständige Telefonnummer im E.164-Format (z.B. "+4917612345678").
     * @return [Result.success] wenn der Code erfolgreich gesendet wurde,
     *         [Result.failure] mit Fehlerdetails bei Problemen.
     */
    suspend fun sendVerificationCode(phoneNumber: String): Result<Unit>

    /**
     * Verifiziert den vom Nutzer eingegebenen Code gegen den zuvor gesendeten.
     *
     * @param phoneNumber Telefonnummer, an die der Code gesendet wurde.
     * @param code Der 6-stellige Verifizierungscode.
     * @return [Result.success] bei korrektem Code,
     *         [Result.failure] bei falschem oder abgelaufenem Code.
     */
    suspend fun verifyCode(phoneNumber: String, code: String): Result<Unit>
}
