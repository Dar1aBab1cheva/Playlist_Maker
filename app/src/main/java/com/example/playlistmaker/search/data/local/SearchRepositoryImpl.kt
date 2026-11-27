package com.example.playlistmaker.search.data.local

import com.example.playlistmaker.search.data.dto.SearchRequest
import com.example.playlistmaker.search.data.dto.TracksResponse
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.models.SearchResult
import com.example.playlistmaker.search.domain.models.Track

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val searchHistory: SearchHistory
) : SearchRepository {

    override fun searchTracks(expression: String): SearchResult {
        val response = try {
            networkClient.doRequest(SearchRequest(expression))
        } catch (e: Exception) {
            return SearchResult.NetworkError
        }

        return when (response.resultCode) {
            200 -> {
                if (response is TracksResponse) {
                    val tracks = response.results.map { dto ->
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
                    SearchResult.Success(tracks)
                } else {
                    SearchResult.ServerError
                }
            }
            else -> SearchResult.ServerError
        }
    }

    override fun getHistory(): List<Track> = searchHistory.read()

    override fun addToHistory(track: Track) = searchHistory.add(track)

    override fun clearHistory() = searchHistory.clear()
}