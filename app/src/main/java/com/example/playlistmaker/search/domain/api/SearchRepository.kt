package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.SearchResult
import com.example.playlistmaker.search.domain.models.Track

interface SearchRepository {
    fun searchTracks(expression: String): SearchResult
    fun getHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()
}