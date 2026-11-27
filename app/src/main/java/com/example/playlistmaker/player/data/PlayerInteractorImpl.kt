package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.api.PlayerInteractor

class PlayerInteractorImpl(
    private val mediaPlayer: MediaPlayer
) : PlayerInteractor {

    override fun prepare(url: String) {
        // mediaPlayer.reset() — убран, потому что MediaPlayer всегда свежий (factory)
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getDuration(): Int {
        return mediaPlayer.duration
    }

    override fun setOnPreparedListener(listener: () -> Unit) {
        mediaPlayer.setOnPreparedListener { listener() }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer.setOnCompletionListener { listener() }
    }
}