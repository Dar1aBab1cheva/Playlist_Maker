package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsRepository

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeMode(): String = repository.getThemeMode()

    override fun saveThemeMode(themeMode: String) = repository.saveThemeMode(themeMode)
}