package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Находим кнопки по id
        val searchButton = findViewById<MaterialButton>(R.id.btn_search)
        val mediaButton = findViewById<MaterialButton>(R.id.btn_media)
        val playlistButton = findViewById<MaterialButton>(R.id.btn_playlist)

        // 1. Анонимный класс (для "Поиск")
        searchButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали «Поиск»", Toast.LENGTH_SHORT).show()
            }
        })

        // 2. Лямбда (для "Медиатека")
        mediaButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали «Медиатека»", Toast.LENGTH_SHORT).show()
        }

        // 3. Лямбда (для "Плейлист")
        playlistButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали «Плейлист»", Toast.LENGTH_SHORT).show()
        }
    }
}