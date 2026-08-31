package com.kliq.app.util

import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoicePlayerManager @Inject constructor() {

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    var currentPlayingMessageId: String? = null
        private set

    val isPlaying: Boolean
        get() = mediaPlayer?.isPlaying == true

    fun play(
        messageId: String,
        audioUrl: String,
        onProgressUpdate: (currentMs: Long, durationMs: Long) -> Unit,
        onCompletion: () -> Unit
    ) {
        if (currentPlayingMessageId == messageId && mediaPlayer != null) {
            if (mediaPlayer?.isPlaying == true) {
                pause()
                return
            } else {
                resume(onProgressUpdate, onCompletion)
                return
            }
        }

        stop()
        currentPlayingMessageId = messageId

        try {
            val player = MediaPlayer().apply {
                setDataSource(audioUrl)
                prepareAsync()
                setOnPreparedListener { mp ->
                    mp.start()
                    startProgressTracker(onProgressUpdate, onCompletion)
                }
                setOnCompletionListener {
                    stopProgressTracker()
                    onCompletion()
                    currentPlayingMessageId = null
                }
                setOnErrorListener { _, _, _ ->
                    stop()
                    true
                }
            }
            mediaPlayer = player
        } catch (e: Exception) {
            stop()
        }
    }

    fun pause() {
        stopProgressTracker()
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {

        }
    }

    fun resume(
        onProgressUpdate: (currentMs: Long, durationMs: Long) -> Unit,
        onCompletion: () -> Unit
    ) {
        try {
            if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
                mediaPlayer?.start()
                startProgressTracker(onProgressUpdate, onCompletion)
            }
        } catch (e: Exception) {
            stop()
        }
    }

    fun seekTo(positionMs: Long) {
        try {
            mediaPlayer?.seekTo(positionMs.toInt())
        } catch (e: Exception) {

        }
    }

    fun stop() {
        stopProgressTracker()
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (e: Exception) {

        } finally {
            mediaPlayer = null
            currentPlayingMessageId = null
        }
    }

    private fun startProgressTracker(
        onProgressUpdate: (currentMs: Long, durationMs: Long) -> Unit,
        onCompletion: () -> Unit
    ) {
        stopProgressTracker()
        progressJob = scope.launch {
            while (isActive && mediaPlayer != null) {
                try {
                    val player = mediaPlayer
                    if (player != null && player.isPlaying) {
                        val current = player.currentPosition.toLong()
                        val duration = player.duration.toLong()
                        onProgressUpdate(current, if (duration > 0) duration else 1L)
                    }
                } catch (e: Exception) {
                    break
                }
                delay(100)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stop()
    }
}
