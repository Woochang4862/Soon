package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.model.schema.Video
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.holder.ThumbnailViewHolder
import com.lusle.android.soon.adapter.listener.OnItemClickListener

class VideoThumbnailAdapter(private var onItemClickListener: OnItemClickListener) : BaseRecyclerAdapter<RecyclerView.ViewHolder>() {

    var list: ArrayList<Video> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_thumbnail, parent, false)
        return ThumbnailViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ThumbnailViewHolder).bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(position: Int): Video {
        return list[position]
    }
}
