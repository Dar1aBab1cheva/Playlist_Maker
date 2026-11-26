package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.App
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
    private fun getSearchRepository(): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository())
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