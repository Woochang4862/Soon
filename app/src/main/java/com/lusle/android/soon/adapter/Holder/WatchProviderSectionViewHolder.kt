package com.lusle.android.soon.adapter.Holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.Model.Schema.WatchProvider
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.Listener.OnItemClickListener
import com.lusle.android.soon.adapter.WatchProviderListAdapter

class WatchProviderSectionViewHolder(itemView: View, onWatchProviderClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {

    private val rootView: View = itemView.findViewById(R.id.root_view)
    private val textView: TextView = itemView.findViewById(R.id.watch_provider_type)
    private val watchProviderRecyclerView:RecyclerView = itemView.findViewById(R.id.watch_provider_recyclerview)
    private val adapter: WatchProviderListAdapter = WatchProviderListAdapter(onWatchProviderClickListener)
    private val layoutManager: LinearLayoutManager = LinearLayoutManager(itemView.context)
    fun bind(watchProviderSection:Pair<String,ArrayList<WatchProvider>>) {
        if (watchProviderSection.second.isEmpty()){
            rootView.visibility = View.GONE
        } else {
            rootView.visibility = View.VISIBLE
            textView.text = watchProviderSection.first
            adapter.list.addAll(watchProviderSection.second.sortedBy {
                it.displayPriority
            })
            watchProviderRecyclerView.adapter = adapter
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            watchProviderRecyclerView.layoutManager = layoutManager
        }
    }

}
