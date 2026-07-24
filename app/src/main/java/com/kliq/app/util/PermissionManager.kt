package com.kliq.app.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.kliq.app.data.model.LocationPermissionState
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface defining location permission management and settings deep-linking.
 */
interface PermissionManager {
    /** Evaluates the current system location permission state. */
    fun checkLocationPermission(context: Context): LocationPermissionState

    /** Creates and launches an intent leading directly to the app's system settings page. */
    fun openAppSettings(context: Context)
}

/**
 * Default implementation of [PermissionManager] interacting with Android System APIs.
 */
@Singleton
class PermissionManagerImpl @Inject constructor() : PermissionManager {

    override fun checkLocationPermission(context: Context): LocationPermissionState {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return if (fineLocationGranted || coarseLocationGranted) {
            LocationPermissionState.Granted
        } else {
            LocationPermissionState.Denied
        }
    }

    override fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
