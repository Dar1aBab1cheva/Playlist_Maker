package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchInteractor {
    fun searchTracks(expression: String, consumer: SearchConsumer)

    interface SearchConsumer {
        fun consume(tracks: List<Track>)
    }
}