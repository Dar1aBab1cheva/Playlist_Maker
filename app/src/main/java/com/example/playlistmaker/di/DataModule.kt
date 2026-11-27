package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.App
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

    // SharedPreferences — single (тяжёлый объект, один на всё приложение)
    single {
        androidContext().getSharedPreferences(App.PREFS_NAME, android.content.Context.MODE_PRIVATE)
    }

    // Gson — factory (лёгкий объект)
    factory { Gson() }

    // Retrofit — single (тяжёлый объект, один на всё приложение)
    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // ITunesApi — single (создаётся из Retrofit, один на приложение)
    single {
        get<Retrofit>().create(ITunesApi::class.java)
    }

    // MediaPlayer — factory (новый экземпляр для каждого плеера)
    factory { MediaPlayer() }

    // NetworkClient — factory (лёгкий, не хранит состояние)
    factory<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    // SearchHistory — factory (лёгкий, не хранит состояние)
    factory {
        SearchHistory(get(), get())
    }

    // SearchRepository — factory (не хранит состояние, создаётся по требованию)
    factory<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    // SettingsRepository — factory (не хранит состояние)
    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    // ExternalNavigator — factory (лёгкий)
    factory {
        ExternalNavigator(androidContext())
    }
}