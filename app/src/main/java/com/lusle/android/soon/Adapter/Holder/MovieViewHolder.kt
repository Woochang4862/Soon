package com.lusle.android.soon.Adapter.Holder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class MovieViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {

    private val imageView = itemView.findViewById<ImageView>(R.id.movie_list_recyclerview_poster)
    private val error_image = itemView.findViewById<LottieAnimationView>(R.id.error_image)

    fun bind(movie: Movie?) {
        itemView.setOnClickListener { v: View? ->
            onItemClickListener?.onItemClick(v, layoutPosition)
        }
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + movie?.posterPath)
                .centerCrop()
                .fit()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        error_image.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                        if (error_image.isAnimating) error_image.cancelAnimation()
                    }

                    override fun onError(e: Exception) {
                        error_image.visibility = View.VISIBLE
                        imageView.visibility = View.GONE
                        if (!error_image.isAnimating) error_image.playAnimation()
                    }
                })
    }
}