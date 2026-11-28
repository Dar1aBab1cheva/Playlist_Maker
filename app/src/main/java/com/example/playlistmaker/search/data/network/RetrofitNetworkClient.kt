package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.SearchRequest
import com.example.playlistmaker.search.data.dto.TracksResponse

class RetrofitNetworkClient(
    private val iTunesApi: ITunesApi
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is SearchRequest) {
            val response = iTunesApi.searchTracks(dto.expression).execute()
            val body = response.body() ?: Response()
            return body.apply { resultCode = response.code() }
        }
        return Response().apply { resultCode = 400 }
    }
}