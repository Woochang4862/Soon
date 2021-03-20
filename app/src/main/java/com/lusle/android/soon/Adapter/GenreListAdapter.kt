package com.lusle.android.soon.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.lusle.android.soon.Adapter.Contract.FragmentGenreAdapterContract
import com.lusle.android.soon.Adapter.Holder.GenreViewHolder
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Genre
import com.lusle.android.soon.R
import java.util.*

class GenreListAdapter(private val onItemClickListener: OnItemClickListener, private val onEmptyListener: OnEmptyListener) : BaseRecyclerAdapter<GenreViewHolder?>(), FragmentGenreAdapterContract.View, FragmentGenreAdapterContract.Model {
    private var list: ArrayList<Genre>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_genre_recyclerview, parent, false)
        return GenreViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(list!![position])
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    override fun setList(mList: ArrayList<Genre>) {
        this.list = mList
    }

    override fun getItem(position: Int): Genre {
        return list!![position]
    }

    override fun onEmpty() {
        super.onEmpty()
        onEmptyListener.onEmpty()
    }

    override fun onNotEmpty() {
        super.onNotEmpty()
        onEmptyListener.onNotEmpty()
    }
}