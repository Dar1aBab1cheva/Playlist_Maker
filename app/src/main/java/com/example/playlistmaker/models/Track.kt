package com.example.playlistmaker.models

import java.io.Serializable

data class Track(
    val trackId: Long,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String? // <-- НОВОЕ ПОЛЕ для 14 спринта
) : Serializable {

    // Обложка в высоком качестве для плеера
    fun getCoverArtwork(): String? =
        artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
}