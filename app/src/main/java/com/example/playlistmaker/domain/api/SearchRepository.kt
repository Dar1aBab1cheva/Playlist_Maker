package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchRepository {
    fun searchTracks(expression: String): List<Track>
    fun getHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
}