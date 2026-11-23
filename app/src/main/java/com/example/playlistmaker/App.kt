package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        // Читаем сохранённую тему при запуске приложения
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        darkTheme = prefs.getBoolean(KEY_DARK_THEME, false)
        switchTheme(darkTheme)
    }

    // Меняем тему и сразу сохраняем выбор в SharedPreferences
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_THEME, darkThemeEnabled)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    companion object {
        const val PREFS_NAME = "app_prefs"
        const val KEY_DARK_THEME = "dark_theme_key"
    }
}