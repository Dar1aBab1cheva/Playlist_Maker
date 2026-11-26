package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.local.SettingsRepositoryImpl
import com.example.playlistmaker.di.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Читаем тему через Interactor (Clean Architecture)
        val themeMode = Creator.provideSettingsInteractor(this).getThemeMode()
        applyTheme(themeMode)
    }

    private fun applyTheme(themeMode: String) {
        when (themeMode) {
            SettingsRepositoryImpl.THEME_LIGHT ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            SettingsRepositoryImpl.THEME_DARK ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    companion object {
        const val PREFS_NAME = "app_prefs"
    }
}