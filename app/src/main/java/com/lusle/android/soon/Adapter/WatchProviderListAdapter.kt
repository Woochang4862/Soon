package com.lusle.android.soon.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.Adapter.Holder.WatchProviderViewHolder
import com.lusle.android.soon.Model.Schema.WatchProvider
import com.lusle.android.soon.R

class WatchProviderListAdapter : RecyclerView.Adapter<WatchProviderViewHolder>() {

    val list:ArrayList<WatchProvider> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchProviderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_watch_provider, parent, false)
        return WatchProviderViewHolder(view)
    }

    override fun onBindViewHolder(holder: WatchProviderViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}