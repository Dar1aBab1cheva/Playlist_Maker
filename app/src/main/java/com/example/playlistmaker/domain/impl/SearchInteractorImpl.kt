package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.api.SearchRepository
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: SearchInteractor.SearchConsumer) {
        executor.execute {
            val tracks = repository.searchTracks(expression)
            consumer.consume(tracks)
        }
    }

    override fun getHistory(): List<Track> = repository.getHistory()

    override fun addToHistory(track: Track) = repository.addToHistory(track)

    override fun clearHistory() = repository.clearHistory()
}