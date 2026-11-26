package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.net.Uri
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
import com.example.playlistmaker.di.Creator
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.models.ThemeMode

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val settingsInteractor: SettingsInteractor by lazy {
        Creator.provideSettingsInteractor()
    }

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

        setupThemeButton()
        setupShareButton()
        setupSupportButton()
        setupAgreementButton()
    }

    private fun setupThemeButton() {
        binding.btnTheme.setOnClickListener { view ->
            showThemePopupMenu(view)
        }
    }

    private fun showThemePopupMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_theme, popup.menu)

        val currentTheme = settingsInteractor.getThemeMode()
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

            if (newTheme != currentTheme) {
                settingsInteractor.saveThemeMode(newTheme)
                applyTheme(newTheme)
            }
            true
        }

        popup.show()
    }

    private fun setupShareButton() {
        binding.btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.setting_share)))
        }
    }

    private fun setupSupportButton() {
        binding.btnSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
            }
            startActivity(Intent.createChooser(emailIntent, getString(R.string.setting_support)))
        }
    }

    private fun setupAgreementButton() {
        binding.btnAgreement.setOnClickListener {
            val url = getString(R.string.agreement_link)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun applyTheme(themeMode: String) {
        when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ThemeMode.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}