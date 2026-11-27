package com.example.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.models.Track
import java.util.Locale

class PlayerViewModel(
    private val track: Track
) : ViewModel() {

    private val playerStateLiveData = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            val currentPosition = mediaPlayer?.currentPosition ?: 0
            val currentState = playerStateLiveData.value ?: PlayerState()
            if (currentState.isPlaying) {
                playerStateLiveData.postValue(
                    currentState.copy(progress = formatDuration(currentPosition.toLong()))
                )
                handler.postDelayed(this, 500)
            }
        }
    }

    init {
        val duration = formatDuration(track.trackTimeMillis)
        playerStateLiveData.value = PlayerState(duration = duration)
        preparePlayer()
    }

    private fun preparePlayer() {
        val url = track.previewUrl
        if (url.isNullOrEmpty()) return

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                val currentState = playerStateLiveData.value ?: PlayerState()
                playerStateLiveData.postValue(currentState.copy(isPrepared = true))
            }
            setOnCompletionListener {
                val currentState = playerStateLiveData.value ?: PlayerState()
                playerStateLiveData.postValue(
                    currentState.copy(isPlaying = false, progress = "00:00")
                )
            }
            prepareAsync()
        }
    }

    fun onPlayPauseClicked() {
        val currentState = playerStateLiveData.value ?: return
        if (!currentState.isPrepared) return

        if (currentState.isPlaying) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    fun onPause() {
        val currentState = playerStateLiveData.value ?: return
        if (currentState.isPlaying) {
            pausePlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        val currentState = playerStateLiveData.value ?: PlayerState()
        playerStateLiveData.postValue(currentState.copy(isPlaying = true))
        handler.post(updateProgressRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer?.pause()
        val currentState = playerStateLiveData.value ?: PlayerState()
        playerStateLiveData.postValue(currentState.copy(isPlaying = false))
        handler.removeCallbacks(updateProgressRunnable)
    }

    private fun formatDuration(millis: Long?): String {
        if (millis == null || millis <= 0) return "00:00"
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateProgressRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }
}