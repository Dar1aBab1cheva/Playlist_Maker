package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.api.SearchRepository
import com.example.playlistmaker.domain.models.SearchResult
import com.example.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: SearchInteractor.SearchConsumer) {
        executor.execute {
            when (val result = repository.searchTracks(expression)) {
                is SearchResult.Success -> consumer.consume(result.tracks)
                is SearchResult.NetworkError -> consumer.consumeError()
                is SearchResult.ServerError -> consumer.consumeError()
            }
        }
    }

    override fun getHistory(): List<Track> = repository.getHistory()

    override fun addToHistory(track: Track) = repository.addToHistory(track)

    override fun clearHistory() = repository.clearHistory()
}