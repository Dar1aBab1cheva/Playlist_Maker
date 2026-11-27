package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.settings.domain.model.ThemeMode

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    companion object {
        const val THEME_KEY = "theme_key"
    }

    override fun getThemeMode(): String {
        return sharedPreferences.getString(THEME_KEY, ThemeMode.DEFAULT) ?: ThemeMode.DEFAULT
    }

    override fun saveThemeMode(themeMode: String) {
        sharedPreferences.edit().putString(THEME_KEY, themeMode).apply()
    }
}