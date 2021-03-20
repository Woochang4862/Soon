package com.lusle.android.soon.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.Adapter.Holder.SectionHolder
import com.lusle.android.soon.Model.Schema.Section
import com.lusle.android.soon.R

class SectionListAdapter(private val list: List<Section>) : RecyclerView.Adapter<SectionHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_section_company_fragment, parent, false)
        return SectionHolder(view)
    }

    override fun onBindViewHolder(holder: SectionHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

}