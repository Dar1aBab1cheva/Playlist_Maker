package com.example.playlistmaker.ui

import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible // <--  импорт для .isVisible
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
        private const val TAG = "PlayerActivity"
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

        binding.ivBack.setOnClickListener { finish() }

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

    private fun fillTrackData(track: Track) {
        binding.tvTrackName.text = track.trackName.orEmpty()
        binding.tvArtistName.text = track.artistName.orEmpty()

        // Рекомендация 3: Упрощение форматирования времени через .orEmpty()
        val timeFormatted = track.trackTimeMillis?.let {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it)
        }.orEmpty()
        binding.valueDuration.text = timeFormatted
        binding.tvProgress.text = timeFormatted

        // Рекомендация 2: Использование View.isVisible вместо if/else
        binding.valueAlbum.text = track.collectionName.orEmpty()
        binding.labelAlbum.isVisible = !track.collectionName.isNullOrEmpty()
        binding.valueAlbum.isVisible = !track.collectionName.isNullOrEmpty()

        val year = track.releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4) ?: track.releaseDate.orEmpty()
        binding.valueYear.text = year
        binding.labelYear.isVisible = !track.releaseDate.isNullOrEmpty()
        binding.valueYear.isVisible = !track.releaseDate.isNullOrEmpty()

        binding.valueGenre.text = track.primaryGenreName.orEmpty()
        binding.labelGenre.isVisible = !track.primaryGenreName.isNullOrEmpty()
        binding.valueGenre.isVisible = !track.primaryGenreName.isNullOrEmpty()

        binding.valueCountry.text = track.country.orEmpty()
        binding.labelCountry.isVisible = !track.country.isNullOrEmpty()
        binding.valueCountry.isVisible = !track.country.isNullOrEmpty()

        Glide.with(this)
            .load(track.getCoverArtwork())
            .centerCrop()
            .placeholder(R.drawable.ic_player_placeholder)
            .error(R.drawable.ic_player_placeholder)
            .into(binding.ivCover)
    }
}