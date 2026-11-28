package com.example.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun prepare(url: String)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
    fun getDuration(): Int
    fun setOnPreparedListener(listener: () -> Unit)
    fun setOnCompletionListener(listener: () -> Unit)
}