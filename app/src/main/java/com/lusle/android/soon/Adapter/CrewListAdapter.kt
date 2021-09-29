package com.lusle.android.soon.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.Adapter.Holder.CrewViewHolder
import com.lusle.android.soon.Model.Schema.Crew
import com.lusle.android.soon.R

class CrewListAdapter : RecyclerView.Adapter<CrewViewHolder>() {

    val list: ArrayList<Crew> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return CrewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CrewViewHolder, position: Int) {
        holder.bind(list[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int = list.size


}