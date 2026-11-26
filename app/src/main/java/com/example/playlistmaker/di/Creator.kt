package com.example.playlistmaker.di

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

    // --- SEARCH ---
    private fun getSearchRepository(context: Context): SearchRepository {
        val history = SearchHistory(
            context.getSharedPreferences(App.PREFS_NAME, Context.MODE_PRIVATE)
        )
        return SearchRepositoryImpl(RetrofitNetworkClient(), history)
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository(context))
    }

    // --- SETTINGS ---
    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            context.getSharedPreferences(App.PREFS_NAME, Context.MODE_PRIVATE)
        )
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }
}