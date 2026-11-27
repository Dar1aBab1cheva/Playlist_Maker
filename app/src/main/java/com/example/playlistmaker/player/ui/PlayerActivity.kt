package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.view_model.PlayerState
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var track: Track? = null
    private var viewModel: PlayerViewModel? = null

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

        binding.ivBack.setOnClickListener { finish() }

        val typedValue = TypedValue()
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurface,
            typedValue,
            true
        )
        binding.ivBack.setColorFilter(typedValue.data)

        val currentTrack = track
        if (currentTrack != null) {
            // Инициализация ViewModel через Koin с параметрами
            viewModel = getViewModel<PlayerViewModel> {
                parametersOf(currentTrack)
            }

            fillTrackData(currentTrack)

            viewModel?.observePlayerState()?.observe(this) { state ->
                render(state)
            }

            binding.btnPlay.setOnClickListener {
                viewModel?.onPlayPauseClicked()
            }
        } else {
            Toast.makeText(this, "Ошибка: трек не передан", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel?.onPause()
    }

    private fun render(state: PlayerState) {
        binding.btnPlay.setImageResource(
            if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
        binding.tvProgress.text = state.progress
        binding.valueDuration.text = state.duration
    }

    private fun fillTrackData(track: Track) {
        binding.tvTrackName.text = track.trackName.orEmpty()
        binding.tvArtistName.text = track.artistName.orEmpty()

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
}