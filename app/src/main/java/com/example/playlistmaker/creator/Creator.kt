package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.search.data.local.SearchHistory
import com.example.playlistmaker.search.data.local.SearchRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.search.domain.impl.SettingsInteractorImpl

object Creator {

    private var application: Application? = null

    fun init(app: Application) {
        application = app
    }

    private fun getApp(): Application {
        return application ?: throw IllegalStateException(
            "Creator not initialized. Call Creator.init() in App.onCreate()"
        )
    }

    private fun getSearchRepository(): SearchRepository {
        val history = SearchHistory(
            getApp().getSharedPreferences(App.Companion.PREFS_NAME, Context.MODE_PRIVATE)
        )
        return SearchRepositoryImpl(RetrofitNetworkClient(), history)
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository())
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(
            getApp().getSharedPreferences(App.Companion.PREFS_NAME, Context.MODE_PRIVATE)
        )
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }
}