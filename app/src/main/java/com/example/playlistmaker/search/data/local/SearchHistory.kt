package com.example.playlistmaker.search.data.local

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()

    // Читаем историю из SharedPreferences
    fun read(): ArrayList<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return arrayListOf()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return gson.fromJson(json, type) ?: arrayListOf()
    }

    // Добавляем трек: убираем дубль, ставим наверх, режем до 10
    fun add(track: Track) {
        val list = read()
        list.removeAll { it.trackId == track.trackId }
        list.add(0, track)
        while (list.size > MAX_SIZE) list.removeAt(list.size - 1)
        save(list)
    }

    // Полная очистка истории
    fun clear() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    private fun save(list: List<Track>) {
        sharedPreferences.edit().putString(HISTORY_KEY, gson.toJson(list)).apply()
    }

    companion object {
        private const val HISTORY_KEY = "search_history_key"
        private const val MAX_SIZE = 10
    }
}