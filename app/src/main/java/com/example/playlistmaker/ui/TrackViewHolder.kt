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
    private val tvTrackSubtitle: TextView = itemView.findViewById(R.id.tv_track_subtitle)

    fun bind(track: Track) {
        // 1. Заполняем текстовые поля
        tvTrackName.text = track.trackName
        // Склеиваем исполнителя, точку и время в одну строку
        tvTrackSubtitle.text = "${track.artistName} • ${track.trackTime}"

        // 2. Загружаем обложку с помощью Glide
        val context = itemView.context
        // ИСПРАВЛЕНО: радиус теперь 2dp вместо 8dp
        val cornerRadius = dpToPx(2f, context)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .apply(RequestOptions().transform(RoundedCorners(cornerRadius)))
            // ИСПРАВЛЕНО: плейсхолдер - наша новая серая иконка
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(ivArtwork)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}