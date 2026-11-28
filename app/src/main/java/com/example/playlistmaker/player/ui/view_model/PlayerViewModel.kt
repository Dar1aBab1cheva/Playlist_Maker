package com.example.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val playerStateLiveData = MutableLiveData<PlayerState>()
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var progressJob: kotlinx.coroutines.Job? = null

    init {
        val duration = formatDuration(track.trackTimeMillis)
        playerStateLiveData.value = PlayerState(duration = duration)
        preparePlayer()
    }

    private fun preparePlayer() {
        val url = track.previewUrl
        if (url.isNullOrEmpty()) return

        playerInteractor.prepare(url)

        playerInteractor.setOnPreparedListener {
            val currentState = playerStateLiveData.value ?: PlayerState()
            playerStateLiveData.postValue(currentState.copy(isPrepared = true))
        }

        playerInteractor.setOnCompletionListener {
            val currentState = playerStateLiveData.value ?: PlayerState()
            playerStateLiveData.postValue(
                currentState.copy(isPlaying = false, progress = "00:00")
            )
            stopProgressUpdates()
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
        playerInteractor.start()
        val currentState = playerStateLiveData.value ?: PlayerState()
        playerStateLiveData.postValue(currentState.copy(isPlaying = true))
        startProgressUpdates()
    }

    private fun pausePlayer() {
        playerInteractor.pause()
        val currentState = playerStateLiveData.value ?: PlayerState()
        playerStateLiveData.postValue(currentState.copy(isPlaying = false))
        stopProgressUpdates()
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                val currentPosition = playerInteractor.getCurrentPosition()
                val currentState = playerStateLiveData.value ?: PlayerState()
                if (currentState.isPlaying) {
                    playerStateLiveData.postValue(
                        currentState.copy(progress = formatDuration(currentPosition.toLong()))
                    )
                }
                delay(500)
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
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
        stopProgressUpdates()
        playerInteractor.release()
    }
}