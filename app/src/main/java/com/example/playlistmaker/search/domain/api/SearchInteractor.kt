package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchInteractor {
    fun searchTracks(expression: String, consumer: SearchConsumer)
    fun getHistory(): List<Track>
    fun addToHistory(track: Track)
    fun clearHistory()

    interface SearchConsumer {
        fun consume(tracks: List<Track>)
        fun consumeError()
    }
}