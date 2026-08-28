package com.kliq.app.domain

import com.kliq.app.data.repository.SessionRepository
import com.kliq.app.data.repository.UserRepository
import com.kliq.app.data.seed.KlagenfurtSeedData
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Liefert die Identität des angemeldeten Nutzers an die ViewModel-Schicht.
 *
 * Bündelt die bisher an mehreren Stellen verstreute Zeichenkette `"current_user"`
 * an einem Ort. Liegt keine aktive Sitzung vor, wird auf das lokale Demo-Profil
 * aus [KlagenfurtSeedData] zurückgefallen, damit die App auch ohne Anmeldung
 * vollständig bedienbar bleibt.
 */
@Singleton
class CurrentUserProvider @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) {

    /** ID des aktiven Nutzers, ohne Datenbankzugriff. */
    fun userId(): String {
        return sessionRepository.getUserId()?.takeIf { it.isNotBlank() }
            ?: KlagenfurtSeedData.CURRENT_USER_ID
    }

    /**
     * Anzeigename des aktiven Nutzers aus der lokalen Datenbank.
     * Fällt auf den Namen des Demo-Profils zurück, wenn kein Datensatz existiert.
     */
    suspend fun displayName(): String {
        val id = userId()
        return runCatching { userRepository.getUserById(id).first()?.username }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }
            ?: KlagenfurtSeedData.CURRENT_USER_NAME
    }
}
