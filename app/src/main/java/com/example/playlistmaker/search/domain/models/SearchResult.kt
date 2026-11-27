package com.example.playlistmaker.search.domain.models

sealed class SearchResult {
    data class Success(val tracks: List<Track>) : SearchResult()
    object NetworkError : SearchResult()
    object ServerError : SearchResult()
}