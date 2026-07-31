package com.kliq.app.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.SystemClock
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class VoiceRecordingResult(
    val filePath: String,
    val durationMs: Long
)

@Singleton
class VoiceRecorderManager @Inject constructor() {

    private var recorder: MediaRecorder? = null
    private var outputFilePath: String? = null
    private var startTimeMs: Long = 0L
    var isRecording: Boolean = false
        private set

    fun startRecording(context: Context): Boolean {
        if (isRecording) return false

        val voiceDir = File(context.cacheDir, "chat_voice").apply { if (!exists()) mkdirs() }
        val file = File(voiceDir, "voice_${System.currentTimeMillis()}.m4a")
        outputFilePath = file.absolutePath

        return try {
            val newRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            newRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            recorder = newRecorder
            startTimeMs = SystemClock.elapsedRealtime()
            isRecording = true
            true
        } catch (e: Exception) {
            recorder?.release()
            recorder = null
            isRecording = false
            false
        }
    }

    fun stopRecording(): VoiceRecordingResult? {
        if (!isRecording || recorder == null) return null

        val duration = SystemClock.elapsedRealtime() - startTimeMs
        val path = outputFilePath

        return try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false

            if (path != null && File(path).exists() && duration >= 500) {
                VoiceRecordingResult(filePath = path, durationMs = duration)
            } else {
                if (path != null) File(path).delete()
                null
            }
        } catch (e: Exception) {
            recorder?.release()
            recorder = null
            isRecording = false
            if (path != null) File(path).delete()
            null
        }
    }

    fun cancelRecording() {
        if (!isRecording && recorder == null) return
        val path = outputFilePath
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            recorder?.release()
        } finally {
            recorder = null
            isRecording = false
            if (path != null) {
                val file = File(path)
                if (file.exists()) file.delete()
            }
        }
    }

    fun getMaxAmplitudeNormalized(): Float {
        if (!isRecording || recorder == null) return 0f
        return try {
            val maxAmp = recorder?.maxAmplitude ?: 0
            (maxAmp / 32767f).coerceIn(0f, 1f)
        } catch (e: Exception) {
            0f
        }
    }

    fun release() {
        cancelRecording()
    }
}
