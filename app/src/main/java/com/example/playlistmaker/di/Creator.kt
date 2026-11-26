package com.example.playlistmaker.di

import android.app.Application
import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.data.local.SearchHistory
import com.example.playlistmaker.data.local.SearchRepositoryImpl
import com.example.playlistmaker.data.local.SettingsRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.SearchInteractor
import com.example.playlistmaker.domain.api.SearchRepository
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl

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
            getApp().getSharedPreferences(App.PREFS_NAME, Context.MODE_PRIVATE)
        )
        return SearchRepositoryImpl(RetrofitNetworkClient(), history)
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository())
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(
            getApp().getSharedPreferences(App.PREFS_NAME, Context.MODE_PRIVATE)
        )
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }
}