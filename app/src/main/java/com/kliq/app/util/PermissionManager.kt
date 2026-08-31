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

interface PermissionManager {

    fun checkLocationPermission(context: Context): LocationPermissionState

    fun checkBackgroundLocationPermission(context: Context): LocationPermissionState

    fun openAppSettings(context: Context)
}

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

    override fun checkBackgroundLocationPermission(context: Context): LocationPermissionState {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            return checkLocationPermission(context)
        }

        val hasForeground = checkLocationPermission(context) == LocationPermissionState.Granted
        if (!hasForeground) {
            return LocationPermissionState.Denied
        }

        val backgroundGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return if (backgroundGranted) {
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
