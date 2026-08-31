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
        val nowMs = System.currentTimeMillis()
        val clubs = KlagenfurtSeedData.clubs(nowMs)
        clubDao.insertClubs(clubs)
        Log.d(TAG, "Venues initialisiert")
    }

    private suspend fun seedChats() {
        val nowMs = System.currentTimeMillis()
        val user = userDao.getUserByIdOneShot(KlagenfurtSeedData.CURRENT_USER_ID)
        val hometown = user?.hometown?.ifBlank { null } ?: KlagenfurtSeedData.CITY_NAME
        val homeChat = KlagenfurtSeedData.chatForCity(hometown, nowMs)
        chatDao.insertChat(homeChat)

        // Ensure user is not auto-joined to all other Austrian city chats
        val allOtherCityIds = listOf("pub_villach", "pub_graz", "pub_wien", "pub_salzburg", "pub_innsbruck", "pub_linz", "pub_klagenfurt")
            .filter { it != homeChat.id }
        allOtherCityIds.forEach { chatId ->
            chatDao.deleteChatById(chatId)
            chatDao.deleteMessagesForChat(chatId)
        }

        // Delete legacy phantom/mock chats and messages
        chatDao.deleteChatById("priv_lena")
        chatDao.deleteChatById("priv_david")
        chatDao.deleteMessagesForChat("priv_lena")
        chatDao.deleteMessagesForChat("priv_david")
        Log.d(TAG, "Stadt-Chat für Heimatstadt '$hometown' initialisiert und Phantom-Daten bereinigt")
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
