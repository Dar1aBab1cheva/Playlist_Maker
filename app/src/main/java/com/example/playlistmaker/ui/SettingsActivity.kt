package com.example.playlistmaker.ui

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Кнопка "Назад"
        val backButton = findViewById<ImageView>(R.id.iv_back)
        backButton.setOnClickListener {
            finish() // Возврат на главный экран
        }
    }
}