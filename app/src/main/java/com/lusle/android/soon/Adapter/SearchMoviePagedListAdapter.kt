package com.lusle.android.soon.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import com.lusle.android.soon.Adapter.Holder.SearchMovieViewHolder
import com.lusle.android.soon.Adapter.Listener.OnBookButtonClickListener
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.R

class SearchMoviePagedListAdapter(private val genres: HashMap<Int, String>, private val onItemClickListener: OnItemClickListener, private val onEmptyListener: OnEmptyListener, private val onBookButtonClickListener: OnBookButtonClickListener) : PagedListAdapter<Movie, SearchMovieViewHolder>(diffMovieItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_search_recyclerview, parent, false)
        return SearchMovieViewHolder(view, onItemClickListener, onBookButtonClickListener)
    }

    override fun onBindViewHolder(holder: SearchMovieViewHolder, position: Int) {
        holder.bind(getItem(position), genres)
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