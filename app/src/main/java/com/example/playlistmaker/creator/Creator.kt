package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.search.data.local.SearchHistory
import com.example.playlistmaker.search.data.local.SearchRepositoryImpl
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.search.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel

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

    // --- SEARCH ---
    private fun getSearchRepository(): SearchRepository {
        val history = SearchHistory(
            getApp().getSharedPreferences(App.PREFS_NAME, Context.MODE_PRIVATE)
        )
        return SearchRepositoryImpl(RetrofitNetworkClient(), history)
    }

    fun provideSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository())
    }

    // --- SETTINGS ---
    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(
            getApp().getSharedPreferences(App.PREFS_NAME, Context.MODE_PRIVATE)
        )
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    // --- SHARING ---
    private fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(ExternalNavigator(getApp()))
    }

    // --- SETTINGS VIEWMODEL FACTORY ---
    fun provideSettingsViewModelFactory(): ViewModelProvider.Factory {
        return viewModelFactory {
            initializer {
                SettingsViewModel(
                    provideSettingsInteractor(),
                    provideSharingInteractor()
                )
            }
        }
    }

    // --- SEARCH VIEWMODEL FACTORY ---
    fun provideSearchViewModelFactory(): ViewModelProvider.Factory {
        return viewModelFactory {
            initializer {
                SearchViewModel(provideSearchInteractor())
            }
        }
    }

    // --- PLAYER VIEWMODEL FACTORY ---
    fun providePlayerViewModelFactory(track: Track): ViewModelProvider.Factory {
        return viewModelFactory {
            initializer {
                PlayerViewModel(track)
            }
        }
    }
}