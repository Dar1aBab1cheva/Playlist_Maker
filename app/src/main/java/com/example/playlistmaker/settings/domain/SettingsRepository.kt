package com.example.playlistmaker.settings.domain

interface SettingsRepository {
    fun getThemeMode(): String
    fun saveThemeMode(themeMode: String)
}