package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.adapter.holder.MovieViewHolder
import com.lusle.android.soon.adapter.listener.OnEmptyListener
import com.lusle.android.soon.adapter.listener.OnItemClickListener
import com.lusle.android.soon.model.schema.Movie
import com.lusle.android.soon.R

class MovieListAdapter(private val onItemClickListener: OnItemClickListener, private val onEmptyListener: OnEmptyListener) : RecyclerView.Adapter<MovieViewHolder>() {

    var list: ArrayList<Movie> = ArrayList()

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_movie_recyclerview
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_recyclerview, parent, false)
        return MovieViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getItem(position: Int): Movie? {
        return list[position]
    }

    fun onEmpty() {
        onEmptyListener.onEmpty()
    }

    fun onNotEmpty() {
        onEmptyListener.onNotEmpty()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}