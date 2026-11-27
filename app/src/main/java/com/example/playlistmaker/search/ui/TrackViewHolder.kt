package com.example.playlistmaker.search.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val ivArtwork: ImageView = itemView.findViewById(R.id.iv_track_artwork)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tv_track_name)
    private val tvTrackSubtitle: TextView = itemView.findViewById(R.id.tv_track_subtitle)

    fun bind(track: Track) {
        tvTrackName.text = track.trackName ?: ""

        val artist = track.artistName ?: ""
        val time = formatTime(track.trackTimeMillis)
        tvTrackSubtitle.text = if (time.isNotEmpty()) "$artist • $time" else artist

        val cornerRadius = dpToPx(2f, itemView.context)
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(ivArtwork)
    }

    private fun formatTime(millis: Long?): String {
        return if (millis != null) {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
        } else ""
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }
}