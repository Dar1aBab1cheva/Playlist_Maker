package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeMode(): String = repository.getThemeMode()

    override fun saveThemeMode(themeMode: String) = repository.saveThemeMode(themeMode)
}