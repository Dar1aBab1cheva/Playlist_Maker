package com.example.playlistmaker.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var track: Track? = null

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        @Suppress("DEPRECATION")
        track = intent.getSerializableExtra(EXTRA_TRACK) as? Track

        // Настраиваем кнопку "Назад" (это ImageView, а не Toolbar)
        binding.ivBack.setOnClickListener { finish() }

        // Красим иконку назад в цвет темы
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true)
        binding.ivBack.setColorFilter(typedValue.data)

        if (track != null) {
            fillTrackData(track!!)
        } else {
            Toast.makeText(this, "Ошибка: трек не передан", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateIconsForTheme()
    }

    private fun updateIconsForTheme() {
        val isDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
        binding.btnPlay.setImageResource(if (isDark) R.drawable.ic_play_dm else R.drawable.ic_play_lm)
        binding.btnPlaylist.setImageResource(if (isDark) R.drawable.ic_add_sing_dm else R.drawable.ic_add_sing_lm)
        binding.btnFavorite.setImageResource(if (isDark) R.drawable.ic_like_void_dm else R.drawable.ic_like_void_lm)
    }

    private fun fillTrackData(track: Track) {
        binding.tvTrackName.text = track.trackName.orEmpty()
        binding.tvArtistName.text = track.artistName.orEmpty()

        // Форматирование длительности mm:ss
        track.trackTimeMillis?.let {
            val timeFormatted = SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
            binding.valueDuration.text = timeFormatted
            binding.tvProgress.text = timeFormatted
        } ?: run {
            binding.valueDuration.text = ""
            binding.tvProgress.text = ""
        }

        // Альбом: показываем только если есть
        if (!track.collectionName.isNullOrEmpty()) {
            binding.valueAlbum.text = track.collectionName
            binding.labelAlbum.visibility = View.VISIBLE
            binding.valueAlbum.visibility = View.VISIBLE
        } else {
            binding.labelAlbum.visibility = View.GONE
            binding.valueAlbum.visibility = View.GONE
        }

        // Год релиза: показываем только если есть (берём первые 4 символа)
        if (!track.releaseDate.isNullOrEmpty()) {
            val year = if (track.releaseDate.length >= 4) track.releaseDate.substring(0, 4) else track.releaseDate
            binding.valueYear.text = year
            binding.labelYear.visibility = View.VISIBLE
            binding.valueYear.visibility = View.VISIBLE
        } else {
            binding.labelYear.visibility = View.GONE
            binding.valueYear.visibility = View.GONE
        }

        // Жанр
        if (!track.primaryGenreName.isNullOrEmpty()) {
            binding.valueGenre.text = track.primaryGenreName
            binding.labelGenre.visibility = View.VISIBLE
            binding.valueGenre.visibility = View.VISIBLE
        } else {
            binding.labelGenre.visibility = View.GONE
            binding.valueGenre.visibility = View.GONE
        }

        // Страна
        if (!track.country.isNullOrEmpty()) {
            binding.valueCountry.text = track.country
            binding.labelCountry.visibility = View.VISIBLE
            binding.valueCountry.visibility = View.VISIBLE
        } else {
            binding.labelCountry.visibility = View.GONE
            binding.valueCountry.visibility = View.GONE
        }

        // Загрузка обложки 512x512 через Glide
        Glide.with(this)
            .load(track.getCoverArtwork())
            .centerCrop()
            .placeholder(R.drawable.ic_player_placeholder)
            .error(R.drawable.ic_player_placeholder)
            .into(binding.ivCover)
    }
}