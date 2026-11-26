package com.example.playlistmaker.data.local

import com.example.playlistmaker.data.dto.SearchRequest
import com.example.playlistmaker.data.dto.TracksResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.api.SearchRepository
import com.example.playlistmaker.domain.models.Track

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistory: SearchHistory
) : SearchRepository {

    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(SearchRequest(expression))

        if (response.resultCode == 200 && response is TracksResponse) {
            return response.results.map { dto ->
                Track(
                    trackId = dto.trackId,
                    trackName = dto.trackName,
                    artistName = dto.artistName,
                    trackTimeMillis = dto.trackTimeMillis,
                    artworkUrl100 = dto.artworkUrl100,
                    collectionName = dto.collectionName,
                    releaseDate = dto.releaseDate,
                    primaryGenreName = dto.primaryGenreName,
                    country = dto.country,
                    previewUrl = dto.previewUrl
                )
            }
        }
        return emptyList()
    }

    override fun getHistory(): List<Track> = searchHistory.read()

    override fun addToHistory(track: Track) = searchHistory.add(track)

    override fun clearHistory() = searchHistory.clear()
}