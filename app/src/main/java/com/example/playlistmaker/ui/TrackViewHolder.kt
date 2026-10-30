package com.example.playlistmaker.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.models.Track

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val ivArtwork: ImageView = itemView.findViewById(R.id.iv_track_artwork)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tv_track_name)
    private val tvArtistName: TextView = itemView.findViewById(R.id.tv_artist_name)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tv_track_time)

    fun bind(track: Track) {
        // Заполняем текстовые поля
        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvTrackTime.text = track.trackTime

        // Загружаем обложку с помощью Glide
        val context = itemView.context
        val cornerRadius = dpToPx(8f, context) // Закругление углов 8dp

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .apply(RequestOptions().transform(RoundedCorners(cornerRadius)))
            .placeholder(R.drawable.ic_library) // Показывается во время загрузки
            .error(R.drawable.ic_library)       // Показывается при ошибке (нет интернета)
            .into(ivArtwork)
    }

    // Вспомогательная функция для перевода dp в px
    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}