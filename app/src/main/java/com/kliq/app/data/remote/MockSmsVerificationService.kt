package com.kliq.app.data.remote

import kotlinx.coroutines.delay

/**
 * Mock-Implementierung des [SmsVerificationService] für die lokale
 * Entwicklung und Unit-Tests. Simuliert Netzwerk-Latenz und validiert
 * gegen einen fest hinterlegten Test-Code.
 *
 * Akzeptierter Code: "123456"
 * Simulierte Latenz: 1000ms pro Aufruf
 */
class MockSmsVerificationService : SmsVerificationService {

    companion object {
        const val VALID_TEST_CODE = "123456"
        private const val SIMULATED_DELAY_MS = 1000L
    }

    override suspend fun sendVerificationCode(phoneNumber: String): Result<Unit> {
        delay(SIMULATED_DELAY_MS)

        if (phoneNumber.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Telefonnummer darf nicht leer sein")
            )
        }

        // Mock: Code wird immer erfolgreich "gesendet"
        return Result.success(Unit)
    }

    override suspend fun verifyCode(phoneNumber: String, code: String): Result<Unit> {
        delay(SIMULATED_DELAY_MS)

        return if (code == VALID_TEST_CODE) {
            Result.success(Unit)
        } else {
            Result.failure(
                IllegalArgumentException("Ungültiger Code. Bitte überprüfe deine Eingabe.")
            )
        }
    }
}
