package com.example.playlistmaker.presentation.player

import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.models.Track
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var track: Track? = null

    private var mediaPlayer: MediaPlayer? = null
    private var playerState = STATE_DEFAULT

    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            val currentPosition = mediaPlayer?.currentPosition ?: 0
            binding.tvProgress.text = formatDuration(currentPosition.toLong())
            mainThreadHandler.postDelayed(this, 500)
        }
    }

    companion object {
        const val EXTRA_TRACK = "extra_track"

        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
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
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurface,
            typedValue,
            true
        )
        binding.ivBack.setColorFilter(typedValue.data)

        if (track != null) {
            fillTrackData(track!!)
            preparePlayer()
            binding.btnPlay.setOnClickListener { playbackControl() }
        } else {
            Toast.makeText(this, "Ошибка: трек не передан", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updatePlayerIcon()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacks(updateProgressRunnable)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun updatePlayerIcon() {
        val isPlaying = playerState == STATE_PLAYING
        binding.btnPlay.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
    }

    private fun fillTrackData(track: Track) {
        binding.tvTrackName.text = track.trackName.orEmpty()
        binding.tvArtistName.text = track.artistName.orEmpty()

        // Полная длительность трека (справа в строке "Длительность")
        binding.valueDuration.text = formatDuration(track.trackTimeMillis)

        // Прогресс начинается с 00:00
        binding.tvProgress.text = "00:00"

        binding.valueAlbum.text = track.collectionName.orEmpty()
        binding.labelAlbum.isVisible = !track.collectionName.isNullOrEmpty()
        binding.valueAlbum.isVisible = !track.collectionName.isNullOrEmpty()

        val year = track.releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4)
            ?: track.releaseDate.orEmpty()
        binding.valueYear.text = year
        binding.labelYear.isVisible = !track.releaseDate.isNullOrEmpty()
        binding.valueYear.isVisible = !track.releaseDate.isNullOrEmpty()

        binding.valueGenre.text = track.primaryGenreName.orEmpty()
        binding.labelGenre.isVisible = !track.primaryGenreName.isNullOrEmpty()
        binding.valueGenre.isVisible = !track.primaryGenreName.isNullOrEmpty()

        binding.valueCountry.text = track.country.orEmpty()
        binding.labelCountry.isVisible = !track.country.isNullOrEmpty()
        binding.labelCountry.isVisible = !track.country.isNullOrEmpty()

        Glide.with(this)
            .load(track.getCoverArtwork())
            .centerCrop()
            .placeholder(R.drawable.ic_player_placeholder)
            .error(R.drawable.ic_player_placeholder)
            .into(binding.ivCover)
    }

    // Ручное форматирование миллисекунд в mm:ss
    private fun formatDuration(millis: Long?): String {
        if (millis == null || millis <= 0) return "00:00"
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun preparePlayer() {
        val url = track?.previewUrl
        if (url.isNullOrEmpty()) {
            Toast.makeText(this, "Ссылка на превью отсутствует", Toast.LENGTH_SHORT).show()
            return
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                playerState = STATE_PREPARED
                updatePlayerIcon()
            }
            setOnCompletionListener {
                mainThreadHandler.removeCallbacks(updateProgressRunnable)
                mediaPlayer?.seekTo(0)
                playerState = STATE_PREPARED
                binding.tvProgress.text = "00:00"
                updatePlayerIcon()
            }
            prepareAsync()
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer?.start()
        playerState = STATE_PLAYING
        updatePlayerIcon()
        mainThreadHandler.post(updateProgressRunnable)
    }

    private fun pausePlayer() {
        if (playerState == STATE_PLAYING) {
            mediaPlayer?.pause()
            playerState = STATE_PAUSED
            updatePlayerIcon()
            mainThreadHandler.removeCallbacks(updateProgressRunnable)
        }
    }
}