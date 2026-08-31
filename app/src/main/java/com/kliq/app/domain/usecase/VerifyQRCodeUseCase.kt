package com.kliq.app.domain.usecase

import com.kliq.app.data.repository.SocialRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.service.VerificationService
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

sealed class QRScanResult {
    data class Success(
        val targetUserId: String,
        val username: String,
        val message: String,
        val isAlreadyFriends: Boolean = false
    ) : QRScanResult()

    data class SelfScan(val message: String = "Du kannst deinen eigenen QR-Code nicht verifizieren.") : QRScanResult()
    data class InvalidCode(val message: String = "Ungültiger oder nicht erkannter Kliq QR-Code.") : QRScanResult()
    data class AlreadyFriends(
        val targetUserId: String,
        val username: String,
        val message: String = "Ihr seid bereits befreundet! Verifizierung aktualisiert."
    ) : QRScanResult()
    data class Error(val message: String) : QRScanResult()
}

@Singleton
class VerifyQRCodeUseCase @Inject constructor(
    private val socialRepository: SocialRepository,
    private val userRepository: UserRepository,
    private val verificationService: VerificationService
) {
    suspend operator fun invoke(currentUserId: String, rawPayload: String): QRScanResult {
        val extractedUserId = parsePayloadToUserId(rawPayload)
            ?: return QRScanResult.InvalidCode("Der gescannte QR-Code enthält keinen gültigen Kliq-Profil-Schlüssel.")

        if (extractedUserId.equals(currentUserId, ignoreCase = true)) {
            return QRScanResult.SelfScan("Du kannst deinen eigenen Profil-Code nicht scannen.")
        }

        val targetUser = try {
            userRepository.getUserById(extractedUserId).firstOrNull()
        } catch (_: Exception) {
            null
        }
        val displayUsername = targetUser?.username?.ifBlank { null } ?: "Nutzer_${extractedUserId.take(6)}"

        val alreadyFriends = socialRepository.isFriendOneShot(currentUserId, extractedUserId)

        verificationService.verifyQrScanToken(currentUserId, extractedUserId, rawPayload)

        return if (alreadyFriends) {
            QRScanResult.AlreadyFriends(
                targetUserId = extractedUserId,
                username = displayUsername,
                message = "Nutzer $displayUsername wurde verifiziert (Ihr seid bereits befreundet)."
            )
        } else {
            val friendResult = socialRepository.verifyAndAddFriend(currentUserId, extractedUserId)
            if (friendResult.isSuccess) {
                QRScanResult.Success(
                    targetUserId = extractedUserId,
                    username = displayUsername,
                    message = "Verifizierung erfolgreich! Freundesanfrage gesendet an $displayUsername."
                )
            } else {
                val errorMsg = friendResult.exceptionOrNull()?.localizedMessage ?: "Fehler bei der Verifizierung."
                QRScanResult.Error(errorMsg)
            }
        }
    }

    fun parsePayloadToUserId(rawPayload: String): String? {
        if (rawPayload.isBlank()) return null
        val trimmed = rawPayload.trim()

        if (trimmed.startsWith("kliq://user/verify/")) {
            val afterPrefix = trimmed.removePrefix("kliq://user/verify/")
            val userId = afterPrefix.substringBefore("?").substringBefore("/")
            if (userId.isNotBlank()) return userId
        }

        if (trimmed.startsWith("kliq://") && trimmed.contains("user=")) {
            val uriPart = trimmed.substringAfter("user=").substringBefore("&")
            if (uriPart.isNotBlank()) return uriPart
        }

        if (trimmed.startsWith("KLIQ_USER_")) {
            return trimmed.removePrefix("KLIQ_USER_")
        }

        if (trimmed.matches(Regex("^[a-zA-Z0-9_-]{4,64}$"))) {
            return trimmed
        }

        return null
    }
}
