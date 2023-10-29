package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.adapter.Holder.WatchProviderViewHolder
import com.lusle.android.soon.Model.Schema.WatchProvider
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.VideoThumbnailAdapter.OnClickListener

class WatchProviderListAdapter(private val onClickListener: OnClickListener) : RecyclerView.Adapter<WatchProviderViewHolder>() {

    val list:ArrayList<WatchProvider> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchProviderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_watch_provider, parent, false)
        return WatchProviderViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: WatchProviderViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}