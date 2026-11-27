package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.domain.model.ThemeMode
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        binding.toolbar.navigationIcon?.setTint(typedValue.data)

        // Подписка на тему
        viewModel.observeTheme().observe(this) { themeMode ->
            updateThemeUI(themeMode)
        }

        // Обработчики кликов
        binding.btnTheme.setOnClickListener { view ->
            showThemePopupMenu(view)
        }

        binding.btnShare.setOnClickListener {
            viewModel.onShareAppClicked()
        }

        binding.btnSupport.setOnClickListener {
            viewModel.onSupportClicked()
        }

        binding.btnAgreement.setOnClickListener {
            viewModel.onAgreementClicked()
        }
    }

    private fun showThemePopupMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_theme, popup.menu)

        val currentTheme = viewModel.observeTheme().value
        when (currentTheme) {
            ThemeMode.LIGHT -> popup.menu.findItem(R.id.menu_theme_light).isChecked = true
            ThemeMode.DARK -> popup.menu.findItem(R.id.menu_theme_dark).isChecked = true
            else -> popup.menu.findItem(R.id.menu_theme_system).isChecked = true
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val newTheme = when (menuItem.itemId) {
                R.id.menu_theme_light -> ThemeMode.LIGHT
                R.id.menu_theme_dark -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
            viewModel.onThemeSelected(newTheme)
            true
        }

        popup.show()
    }

    private fun updateThemeUI(themeMode: String) {
        when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ThemeMode.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}