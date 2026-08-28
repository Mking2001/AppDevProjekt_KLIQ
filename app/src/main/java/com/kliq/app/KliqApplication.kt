package com.kliq.app

import android.app.Application
import com.kliq.app.data.seed.KliqDatabaseSeeder
import com.kliq.app.service.notification.NotificationChannelManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class KliqApplication : Application() {

    @Inject
    lateinit var notificationChannelManager: NotificationChannelManager

    @Inject
    lateinit var databaseSeeder: KliqDatabaseSeeder

    /**
     * Anwendungsweiter Scope für Initialisierungsarbeiten, die den
     * Lebenszyklus einzelner Screens überdauern.
     */
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        notificationChannelManager.createNotificationChannels()

        applicationScope.launch {
            databaseSeeder.seedIfEmpty()
        }
    }
}
