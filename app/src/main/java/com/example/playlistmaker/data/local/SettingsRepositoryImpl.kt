package com.example.playlistmaker.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    companion object {
        const val THEME_KEY = "theme_key"
        // ВАЖНО: сначала объявляем конкретные значения, потом THEME_DEFAULT
        const val THEME_LIGHT = "Светлая"
        const val THEME_DARK = "Тёмная"
        const val THEME_SYSTEM = "Системная"
        const val THEME_DEFAULT = THEME_SYSTEM
    }

    override fun getThemeMode(): String {
        return sharedPreferences.getString(THEME_KEY, THEME_DEFAULT) ?: THEME_DEFAULT
    }

    override fun saveThemeMode(themeMode: String) {
        sharedPreferences.edit().putString(THEME_KEY, themeMode).apply()
    }
}