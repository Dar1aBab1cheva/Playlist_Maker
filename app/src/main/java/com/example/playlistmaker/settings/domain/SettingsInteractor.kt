package com.example.playlistmaker.settings.domain

interface SettingsInteractor {
    fun getThemeMode(): String
    fun saveThemeMode(themeMode: String)
}