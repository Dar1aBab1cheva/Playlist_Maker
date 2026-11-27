package com.example.playlistmaker.di

import com.example.playlistmaker.App
import com.example.playlistmaker.search.data.local.SearchHistory
import com.example.playlistmaker.search.data.local.SearchRepositoryImpl
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.sharing.data.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.data.PlayerInteractorImpl

val dataModule = module {
    single {
        androidContext().getSharedPreferences(App.PREFS_NAME, android.content.Context.MODE_PRIVATE)
    }

    single {
        SearchHistory(get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient()
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single {
        ExternalNavigator(androidContext())
    }

    single<PlayerInteractor> {
        PlayerInteractorImpl()
    }
}