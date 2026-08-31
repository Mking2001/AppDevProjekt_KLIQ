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

@Singleton
class KliqDatabaseSeeder @Inject constructor(
    private val clubDao: ClubDao,
    private val clubOfferDao: ClubOfferDao,
    private val chatDao: ChatDao,
    private val userDao: UserDao,
    private val feedDao: FeedDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

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

        val allOtherCityIds = listOf("pub_villach", "pub_graz", "pub_wien", "pub_salzburg", "pub_innsbruck", "pub_linz", "pub_klagenfurt")
            .filter { it != homeChat.id }
        allOtherCityIds.forEach { chatId ->
            chatDao.deleteChatById(chatId)
            chatDao.deleteMessagesForChat(chatId)
        }

        chatDao.deleteChatById("priv_lena")
        chatDao.deleteChatById("priv_david")
        chatDao.deleteMessagesForChat("priv_lena")
        chatDao.deleteMessagesForChat("priv_david")
        Log.d(TAG, "Stadt-Chat für Heimatstadt '$hometown' initialisiert und Phantom-Daten bereinigt")
    }

    private suspend fun seedFeed() {

        feedDao.deleteMockStories()
        feedDao.deleteMockPosts()
        feedDao.deleteMockComments()
    }

    private companion object {
        const val TAG = "KliqDatabaseSeeder"
    }
}
