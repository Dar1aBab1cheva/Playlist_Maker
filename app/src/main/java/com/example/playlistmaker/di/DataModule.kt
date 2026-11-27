package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.App
import com.example.playlistmaker.player.data.PlayerInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.data.local.SearchHistory
import com.example.playlistmaker.search.data.local.SearchRepositoryImpl
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    // SharedPreferences
    single {
        androidContext().getSharedPreferences(App.PREFS_NAME, android.content.Context.MODE_PRIVATE)
    }

    // Gson
    single { Gson() }

    // Retrofit
    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ITunesApi
    single {
        get<Retrofit>().create(ITunesApi::class.java)
    }

    // MediaPlayer
    factory { MediaPlayer() }

    // NetworkClient
    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    // SearchHistory
    single {
        SearchHistory(get(), get())
    }

    // SearchRepository
    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    // SettingsRepository
    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    // ExternalNavigator
    single {
        ExternalNavigator(androidContext())
    }

    // PlayerInteractor
    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }
}