package com.example.playlistmaker.domain.api

interface SettingsRepository {
    fun getThemeMode(): String
    fun saveThemeMode(themeMode: String)
}