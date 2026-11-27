package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val themeLiveData = MutableLiveData<String>()
    fun observeTheme(): LiveData<String> = themeLiveData

    init {
        themeLiveData.value = settingsInteractor.getThemeMode()
    }

    fun onThemeSelected(themeMode: String) {
        val currentTheme = themeLiveData.value
        if (themeMode != currentTheme) {
            settingsInteractor.saveThemeMode(themeMode)
            themeLiveData.postValue(themeMode)
        }
    }

    fun onShareAppClicked() {
        sharingInteractor.shareApp()
    }

    fun onSupportClicked() {
        sharingInteractor.openSupport()
    }

    fun onAgreementClicked() {
        sharingInteractor.openAgreement()
    }
}