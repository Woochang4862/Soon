package com.lusle.android.soon.adapter.holder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.model.schema.Video
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.listener.OnItemClickListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ThumbnailViewHolder(itemView: View, private val onClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {

    private val baseUrl = "https://img.youtube.com/vi/"
    private val qualityName = arrayListOf(
        "/maxresdefault.jpg",
        "/sddefault.jpg",
        "/mqdefault.jpg",
        "/hqdefault.jpg",
        "/default.jpg"
    )

    var thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)

    fun bind(video: Video) {
        itemView.setOnClickListener {
            onClickListener.onItemClick(it, layoutPosition)
        }

        Picasso
            .get()
            .load(baseUrl + video.key + qualityName[0])
            .fit()
            .centerInside()
            .into(thumbnail, object : Callback {
                override fun onSuccess() {}
                override fun onError(e: Exception) {
                    Picasso.get().load(baseUrl + video.key + qualityName[1]).fit()
                        .centerInside().into(
                            thumbnail, object : Callback {
                                override fun onSuccess() {}
                                override fun onError(e: Exception) {
                                    Picasso.get()
                                        .load(baseUrl + video.key + qualityName[2])
                                        .fit().centerInside().into(
                                            thumbnail, object : Callback {
                                                override fun onSuccess() {}
                                                override fun onError(e: Exception) {
                                                    Picasso.get()
                                                        .load(baseUrl + video.key + qualityName[3])
                                                        .fit().centerInside().into(
                                                            thumbnail, object : Callback {
                                                                override fun onSuccess() {}
                                                                override fun onError(e: Exception) {
                                                                    Picasso.get()
                                                                        .load(baseUrl + video.key + qualityName[4])
                                                                        .fit().centerInside()
                                                                        .into(
                                                                            thumbnail
                                                                        )
                                                                }
                                                            })
                                                }
                                            })
                                }
                            })
                }
            })
    }
}