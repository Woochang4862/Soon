package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.model.schema.WatchProvider
import com.lusle.android.soon.model.schema.WatchProviderResult
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.holder.WatchProviderSectionViewHolder
import com.lusle.android.soon.adapter.listener.OnItemClickListener

class WatchProviderSectionListAdapter(watchProviderResult: WatchProviderResult, private val onWatchProviderClickListener:OnItemClickListener) : RecyclerView.Adapter<WatchProviderSectionViewHolder>() {

    private val watchProviderSections:ArrayList<Pair<String, ArrayList<WatchProvider>>> = convert(watchProviderResult)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchProviderSectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_watch_provider_section, parent, false)
        return WatchProviderSectionViewHolder(view, onWatchProviderClickListener)
    }

    override fun onBindViewHolder(holder: WatchProviderSectionViewHolder, position: Int) {
        holder.bind(watchProviderSections[position])
    }

    override fun getItemCount(): Int {
        return watchProviderSections.size
    }

    private fun convert(watchProviderResult:WatchProviderResult):ArrayList<Pair<String, ArrayList<WatchProvider>>>{
        val result = ArrayList<Pair<String, ArrayList<WatchProvider>>>()

        val temp = ArrayList<WatchProvider>()
        watchProviderResult.flatrate?.forEach {
            temp.add(it)
        }
        result.add(Pair("Stream", temp.clone() as ArrayList<WatchProvider>))
        temp.clear()

        watchProviderResult.buy?.forEach {
            temp.add(it)
        }
        result.add(Pair("Buy", temp.clone() as ArrayList<WatchProvider>))
        temp.clear()

        watchProviderResult.rent?.forEach {
            temp.add(it)
        }
        result.add(Pair("Rent", temp.clone() as ArrayList<WatchProvider>))

        return result
    }

    fun getWatchProviderItem(position: Int) {
        return
    }

}