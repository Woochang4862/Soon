package com.lusle.android.soon.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.Adapter.Holder.CastViewHolder
import com.lusle.android.soon.Model.Schema.Cast
import com.lusle.android.soon.R

class CastListAdapter : RecyclerView.Adapter<CastViewHolder>() {

    val list: ArrayList<Cast> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(list[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int = list.size


}