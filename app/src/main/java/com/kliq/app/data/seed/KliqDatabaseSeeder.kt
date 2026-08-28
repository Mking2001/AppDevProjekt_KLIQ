package com.kliq.app.data.seed

import android.util.Log
import com.kliq.app.data.local.dao.ChatDao
import com.kliq.app.data.local.dao.ClubDao
import com.kliq.app.data.local.dao.ClubOfferDao
import com.kliq.app.data.local.dao.FeedDao
import com.kliq.app.data.local.dao.UserDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Befüllt die lokale Room-Datenbank beim ersten Start mit dem
 * Klagenfurt-Demonstrationsdatensatz aus [KlagenfurtSeedData].
 *
 * Der Seeder ist idempotent: Jeder Bereich wird nur geschrieben, wenn er leer ist.
 * Selbst erstellte Beiträge, gesetzte Favoriten und gesendete Nachrichten bleiben
 * dadurch über App-Starts hinweg erhalten.
 *
 * Sobald ein produktives Backend angebunden ist, entfällt der Aufruf in
 * `KliqApplication` ohne weitere Anpassungen an der Repository-Schicht.
 */
@Singleton
class KliqDatabaseSeeder @Inject constructor(
    private val clubDao: ClubDao,
    private val clubOfferDao: ClubOfferDao,
    private val chatDao: ChatDao,
    private val userDao: UserDao,
    private val feedDao: FeedDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Prüft alle Bereiche und schreibt fehlende Demo-Daten.
     * Fehler werden protokolliert und nicht weitergeworfen, damit ein
     * Seeding-Problem den App-Start nicht blockiert.
     */
    suspend fun seedIfEmpty() = withContext(ioDispatcher) {
        try {
            seedUsers()
            seedVenues()
            seedChats()
            seedFeed()
        } catch (e: Exception) {
            Log.e(TAG, "Seeding des Klagenfurt-Datensatzes fehlgeschlagen", e)
        }
    }

    private suspend fun seedUsers() {
        if (userDao.getUserByIdOneShot(KlagenfurtSeedData.CURRENT_USER_ID) != null) return

        KlagenfurtSeedData.users().forEach { userDao.insertUser(it) }
        Log.d(TAG, "Nutzerprofile eingefügt")
    }

    private suspend fun seedVenues() {
        if (clubDao.getAllClubs().first().isNotEmpty()) return

        val nowMs = System.currentTimeMillis()
        clubDao.insertClubs(KlagenfurtSeedData.clubs(nowMs))
        clubDao.insertEvents(KlagenfurtSeedData.events(nowMs))
        clubOfferDao.insertOffers(KlagenfurtSeedData.clubOffers(nowMs))
        Log.d(TAG, "Venues, Events und Aktionen eingefügt")
    }

    private suspend fun seedChats() {
        if (chatDao.getAllChats().first().isNotEmpty()) return

        val nowMs = System.currentTimeMillis()
        chatDao.insertChats(KlagenfurtSeedData.chats(nowMs))
        KlagenfurtSeedData.messages(nowMs).forEach { chatDao.insertMessage(it) }
        Log.d(TAG, "Chats und Nachrichtenverlauf eingefügt")
    }

    private suspend fun seedFeed() {
        // Clean out all legacy mock posts, comments, and stories
        feedDao.deleteMockStories()
        feedDao.deleteMockPosts()
        feedDao.deleteMockComments()
    }

    private companion object {
        const val TAG = "KliqDatabaseSeeder"
    }
}
