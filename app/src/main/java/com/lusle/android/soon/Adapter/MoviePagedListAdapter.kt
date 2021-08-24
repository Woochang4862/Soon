package com.lusle.android.soon.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.lusle.android.soon.Adapter.Holder.MovieViewHolder
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.R

val diffMovieItemCallback = object : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem.name.trim() == newItem.name.trim()

}

class MoviePagedListAdapter(private val onItemClickListener: OnItemClickListener, private val onEmptyListener: OnEmptyListener) : PagedListAdapter<Movie, MovieViewHolder>(diffMovieItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_recyclerview, parent, false)
        return MovieViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    public override fun getItem(position: Int): Movie? {
        return super.getItem(position)
    }

    fun onEmpty() {
        onEmptyListener.onEmpty()
    }

    fun onNotEmpty() {
        onEmptyListener.onNotEmpty()
    }
}