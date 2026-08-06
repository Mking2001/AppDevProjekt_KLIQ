package com.kliq.app.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class HapticFeedbackPattern {
    CONFIRM,
    REJECT,
    LIGHT_CLICK,
    HEAVY_CLICK
}

interface HapticFeedbackManager {
    fun performHapticFeedback(pattern: HapticFeedbackPattern, reason: String? = null)
    fun performConfirm(reason: String? = null)
    fun performReject(reason: String? = null)
    fun performLightClick(reason: String? = null)
    fun performHeavyClick(reason: String? = null)
    fun isHapticFeedbackEnabled(): Boolean
}

@Singleton
class HapticFeedbackManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : HapticFeedbackManager {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    override fun isHapticFeedbackEnabled(): Boolean {
        return try {
            @Suppress("DEPRECATION")
            val systemHaptic = Settings.System.getInt(
                context.contentResolver,
                Settings.System.HAPTIC_FEEDBACK_ENABLED,
                1
            ) != 0
            val hasVibratorHardware = vibrator?.hasVibrator() == true
            systemHaptic && hasVibratorHardware
        } catch (e: Exception) {
            true
        }
    }

    override fun performHapticFeedback(pattern: HapticFeedbackPattern, reason: String?) {
        val logDetails = if (!reason.isNullOrBlank()) " for $reason" else ""
        Log.d(TAG, "[HAPTIC] Triggered $pattern pattern$logDetails")

        if (!isHapticFeedbackEnabled()) return

        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = when (pattern) {
                HapticFeedbackPattern.CONFIRM -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                    } else {
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 40, 60, 70),
                            intArrayOf(0, 150, 0, 255),
                            -1
                        )
                    }
                }
                HapticFeedbackPattern.REJECT -> {
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 60, 40, 60),
                        intArrayOf(0, 200, 0, 200),
                        -1
                    )
                }
                HapticFeedbackPattern.LIGHT_CLICK -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                    } else {
                        VibrationEffect.createOneShot(20, 100)
                    }
                }
                HapticFeedbackPattern.HEAVY_CLICK -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
                    } else {
                        VibrationEffect.createOneShot(50, 255)
                    }
                }
            }
            vib.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            when (pattern) {
                HapticFeedbackPattern.CONFIRM -> vib.vibrate(longArrayOf(0, 40, 60, 70), -1)
                HapticFeedbackPattern.REJECT -> vib.vibrate(longArrayOf(0, 60, 40, 60), -1)
                HapticFeedbackPattern.LIGHT_CLICK -> vib.vibrate(20)
                HapticFeedbackPattern.HEAVY_CLICK -> vib.vibrate(50)
            }
        }
    }

    override fun performConfirm(reason: String?) = performHapticFeedback(HapticFeedbackPattern.CONFIRM, reason)
    override fun performReject(reason: String?) = performHapticFeedback(HapticFeedbackPattern.REJECT, reason)
    override fun performLightClick(reason: String?) = performHapticFeedback(HapticFeedbackPattern.LIGHT_CLICK, reason)
    override fun performHeavyClick(reason: String?) = performHapticFeedback(HapticFeedbackPattern.HEAVY_CLICK, reason)

    companion object {
        const val TAG = "HapticFeedbackManager"
    }
}
