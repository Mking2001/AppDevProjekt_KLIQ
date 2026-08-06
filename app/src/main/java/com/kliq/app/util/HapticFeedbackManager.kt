package com.kliq.app.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
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
    fun performHapticFeedback(pattern: HapticFeedbackPattern)
    fun performConfirm()
    fun performReject()
    fun performLightClick()
    fun performHeavyClick()
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

    override fun performHapticFeedback(pattern: HapticFeedbackPattern) {
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

    override fun performConfirm() = performHapticFeedback(HapticFeedbackPattern.CONFIRM)
    override fun performReject() = performHapticFeedback(HapticFeedbackPattern.REJECT)
    override fun performLightClick() = performHapticFeedback(HapticFeedbackPattern.LIGHT_CLICK)
    override fun performHeavyClick() = performHapticFeedback(HapticFeedbackPattern.HEAVY_CLICK)
}
