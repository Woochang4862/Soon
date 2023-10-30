package com.lusle.android.soon.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.adapter.listener.OnItemClickListener
import com.lusle.android.soon.model.schema.Genre
import com.lusle.android.soon.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class GenreViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
    private var genreIcon: ImageView = itemView.findViewById(R.id.genre_icon)
    private var genreText: TextView = itemView.findViewById(R.id.genre_text)
    private val error_image: LottieAnimationView = itemView.findViewById(R.id.error_image)

    fun bind(genre: Genre) {
        itemView.setOnClickListener { v: View? -> onItemClickListener?.onItemClick(v, layoutPosition) }
        val baseUrl = "https://image.tmdb.org/t/p/w500"
        Picasso.get()
                .load(baseUrl + genre.icon_path)
                .centerCrop()
                .fit()
                .into(genreIcon, object : Callback {
                    override fun onSuccess() {
                        error_image.visibility = View.GONE
                        genreIcon.visibility = View.VISIBLE
                        if(error_image.isAnimating) error_image.cancelAnimation()
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        error_image.visibility = View.VISIBLE
                        genreIcon.visibility = View.GONE
                        if(!error_image.isAnimating) error_image.playAnimation()
                    }
                })
        genreText.text = genre.name
    }
}