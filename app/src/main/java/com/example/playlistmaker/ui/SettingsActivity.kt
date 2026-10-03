package com.example.playlistmaker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    companion object {
        const val DARK_THEME_KEY = "dark_theme_key"
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

        loadThemeState()
        setupClickListeners()
    }

    private fun loadThemeState() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        binding.switchDarkTheme.isChecked = prefs.getBoolean(DARK_THEME_KEY, false)
    }

    private fun setupClickListeners() {
        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            prefs.edit().putBoolean(DARK_THEME_KEY, isChecked).apply()

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.itemShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Скачай приложение Playlist Maker!")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }

        binding.itemSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@playlistmaker.com")
                putExtra(Intent.EXTRA_SUBJECT, "Вопрос по приложению")
            }
            startActivity(emailIntent)
        }

        binding.itemAgreement.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://yandex.ru/legal/practicum_offer/")
            )
            startActivity(browserIntent)
        }
    }
}