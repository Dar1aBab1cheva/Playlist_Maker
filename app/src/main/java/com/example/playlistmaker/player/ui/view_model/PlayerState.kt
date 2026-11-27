package com.example.playlistmaker.player.ui.view_model

data class PlayerState(
    val isPlaying: Boolean = false,
    val isPrepared: Boolean = false,
    val progress: String = "00:00",
    val duration: String = "00:00"
)