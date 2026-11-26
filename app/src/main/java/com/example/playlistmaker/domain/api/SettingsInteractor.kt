package com.example.playlistmaker.domain.api

interface SettingsInteractor {
    fun getThemeMode(): String
    fun saveThemeMode(themeMode: String)
}