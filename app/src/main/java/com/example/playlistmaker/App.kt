package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.model.ThemeMode

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)  // <-- Сохраняем Application в Creator

        migrateOldTheme()

        val themeMode = Creator.provideSettingsInteractor().getThemeMode()
        applyTheme(themeMode)
    }

    private fun migrateOldTheme() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val newThemeKey = "theme_key"
        val oldThemeKey = "dark_theme_key"

        if (!prefs.contains(newThemeKey) && prefs.contains(oldThemeKey)) {
            val wasDark = prefs.getBoolean(oldThemeKey, false)
            val newTheme = if (wasDark) ThemeMode.DARK else ThemeMode.LIGHT
            prefs.edit()
                .putString(newThemeKey, newTheme)
                .remove(oldThemeKey)
                .apply()
        }
    }

    private fun applyTheme(themeMode: String) {
        when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ThemeMode.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    companion object {
        const val PREFS_NAME = "app_prefs"
    }
}