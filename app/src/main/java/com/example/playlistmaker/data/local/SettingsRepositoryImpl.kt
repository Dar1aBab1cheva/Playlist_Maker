package com.example.playlistmaker.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.domain.models.ThemeMode

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