package com.lusle.android.soon.adapter.holder

import android.os.SystemClock
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.model.schema.Movie
import com.lusle.android.soon.R
import com.lusle.android.soon.view.detail.DetailActivity
import com.lusle.android.soon.adapter.listener.OnItemClickListener
import com.skydoves.transformationlayout.TransformationLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class MovieViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {

    private var previousTime = SystemClock.elapsedRealtime()

    private val transformationLayout = itemView.findViewById<TransformationLayout>(R.id.transformation_layout)
    private val imageView = itemView.findViewById<ImageView>(R.id.movie_list_recyclerview_poster)
    private val altTextView = itemView.findViewById<TextView>(R.id.alt_text)

    fun bind(movie: Movie?) {
        itemView.setOnClickListener { v: View? ->
            onItemClickListener?.onItemClick(v, layoutPosition)
            val now = SystemClock.elapsedRealtime()
            if (now - previousTime >= transformationLayout.duration + 1000)
                DetailActivity.startActivity(itemView.context, transformationLayout, movie?.id)
            previousTime = now
        }
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + movie?.posterPath)
                .centerCrop()
                .fit()
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        altTextView.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception) {
                        altTextView.visibility = View.VISIBLE
                        imageView.visibility = View.GONE
                        altTextView.text = movie?.title
                    }
                })
    }
}