package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.adapter.Holder.CollectionViewHolder
import com.lusle.android.soon.Model.Schema.BelongsToCollection
import com.lusle.android.soon.R
import java.util.*

class CollectionListAdapter : RecyclerView.Adapter<CollectionViewHolder>() {
    val list: ArrayList<BelongsToCollection> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CollectionViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_collection, viewGroup, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: CollectionViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(position: Int): BelongsToCollection {
        return list[position]
    }
}