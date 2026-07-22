package com.kliq.app.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kliq.app.data.local.KliqDatabase
import com.kliq.app.data.local.dao.UserDao
import com.kliq.app.data.local.entities.UserEntity
import com.kliq.app.data.local.entities.UserPreferencesEntity
import com.kliq.app.data.remote.KliqApiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class UserRepositoryTest {

    private lateinit var db: KliqDatabase
    private lateinit var userDao: UserDao
    private lateinit var mockApiService: KliqApiService
    private lateinit var userRepository: UserRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, KliqDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
        mockApiService = mock(KliqApiService::class.java)
        userRepository = UserRepositoryImpl(userDao, mockApiService)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testUserSaveAndSingleSourceOfTruthFlow() = runTest {
        val user = UserEntity("usr_101", "techno_alex", "alex@kliq.de", "https://kliq.de/avatar.jpg", "Club Enthusiast")
        userRepository.saveUser(user)

        val retrievedUser = userRepository.getUserById("usr_101").first()
        assertNotNull(retrievedUser)
        assertEquals("usr_101", retrievedUser?.id)
        assertEquals("techno_alex", retrievedUser?.username)
    }

    @Test
    fun testSyncUserProfileUpdatesRoomDatabaseCache() = runTest {
        val remoteUser = UserEntity("usr_sync_202", "sync_user", "sync@kliq.de", null, "Bio")
        `when`(mockApiService.getUserProfile("usr_sync_202")).thenReturn(remoteUser)

        val syncResult = userRepository.syncUserProfile("usr_sync_202")
        assertTrue(syncResult.isSuccess)

        val cachedUser = userRepository.getUserById("usr_sync_202").first()
        assertNotNull(cachedUser)
        assertEquals("sync_user", cachedUser?.username)
    }

    @Test
    fun testUserPreferencesSaveAndRetrieval() = runTest {
        val user = UserEntity("usr_pref", "pref_user", "pref@kliq.de", null, null)
        userDao.insertUser(user)

        val prefs = UserPreferencesEntity("usr_pref", isDarkMode = true, searchRadiusKm = 25, pushNotificationsEnabled = true)
        userRepository.saveUserPreferences(prefs)

        val retrievedPrefs = userRepository.getUserPreferences("usr_pref").first()
        assertNotNull(retrievedPrefs)
        assertTrue(retrievedPrefs!!.isDarkMode)
        assertEquals(25, retrievedPrefs.searchRadiusKm)
    }
}
