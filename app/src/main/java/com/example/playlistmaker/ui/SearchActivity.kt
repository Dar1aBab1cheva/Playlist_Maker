package com.example.playlistmaker.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    // Глобальная переменная для хранения текста запроса
    private var searchQuery = ""

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge-to-Edge настройка отступов
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Настройка Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        setupSearchEditText()
        setupClearButton()
    }

    private fun setupSearchEditText() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Пусто
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Обновляем глобальную переменную
                searchQuery = s.toString()

                // Управляем видимостью кнопки очистки
                if (s.isNullOrEmpty()) {
                    binding.ivClearSearch.visibility = View.GONE
                } else {
                    binding.ivClearSearch.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Пусто
            }
        })
    }

    private fun setupClearButton() {
        binding.ivClearSearch.setOnClickListener {
            // Очищаем текст
            binding.etSearch.text.clear()
            // Прячем клавиатуру
            hideKeyboard(binding.etSearch)
            // Видимость кнопки скроется сама благодаря TextWatcher (текст стал пустым)
        }
    }

    // Метод для скрытия клавиатуры
    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Сохранение состояния при повороте экрана
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    // Восстановление состояния после поворота экрана
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "") ?: ""
        binding.etSearch.setText(searchQuery)
        // Ставим курсор в конец текста
        binding.etSearch.setSelection(searchQuery.length)
    }
}