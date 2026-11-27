package com.example.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.SearchResult
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.SingleLiveEvent

class SearchViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    private val searchStateLiveData = MutableLiveData<SearchState>()
    fun observeSearchState(): LiveData<SearchState> = searchStateLiveData

    private val historyLiveData = MutableLiveData<List<Track>>()
    fun observeHistory(): LiveData<List<Track>> = historyLiveData

    private val showToastEvent = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToastEvent

    init {
        loadHistory()
    }

    fun searchTracks(query: String) {
        if (query.trim().isEmpty()) return
        searchStateLiveData.postValue(SearchState.Loading)

        searchInteractor.searchTracks(query.trim(), object : SearchInteractor.SearchConsumer {
            override fun consume(tracks: List<Track>) {
                if (tracks.isEmpty()) {
                    searchStateLiveData.postValue(SearchState.Empty)
                } else {
                    searchStateLiveData.postValue(SearchState.Content(tracks))
                }
            }

            override fun consumeError() {
                searchStateLiveData.postValue(SearchState.Error("Проблемы со связью"))
                showToastEvent.postValue("Проверьте подключение к интернету")
            }
        })
    }

    fun addToHistory(track: Track) {
        searchInteractor.addToHistory(track)
        loadHistory()
    }

    fun clearHistory() {
        searchInteractor.clearHistory()
        loadHistory()
    }

    private fun loadHistory() {
        historyLiveData.postValue(searchInteractor.getHistory())
    }
}