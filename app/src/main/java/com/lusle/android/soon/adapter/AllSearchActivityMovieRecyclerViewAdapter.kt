package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.lusle.android.soon.adapter.Holder.SearchMovieViewHolder
import com.lusle.android.soon.adapter.Listener.OnBookButtonClickListener
import com.lusle.android.soon.adapter.Listener.OnEmptyListener
import com.lusle.android.soon.adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.R

class AllSearchActivityMovieRecyclerViewAdapter(private val genres: HashMap<Int, String>, private val onItemClickListener: OnItemClickListener, private val onEmptyListener: OnEmptyListener, private val onBookButtonClickListener: OnBookButtonClickListener) : BaseRecyclerAdapter<SearchMovieViewHolder>() {

    private var list: ArrayList<Movie>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchMovieViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_search_recyclerview, parent, false)
        return SearchMovieViewHolder(v, onItemClickListener, onBookButtonClickListener)
    }

    override fun onBindViewHolder(holder: SearchMovieViewHolder, position: Int) {
        holder.bind(list?.get(position),genres)
    }

    override fun getItemCount(): Int {
        return list?.size ?:{0}()
    }

    override fun onEmpty() = onEmptyListener.onEmpty()

    override fun onNotEmpty() = onEmptyListener.onNotEmpty()

    fun setList(list: ArrayList<Movie>?) {
        this.list = list
    }

    fun clear() {
        list?.clear() ?: {list = arrayListOf() }()
    }

    fun getItem(position: Int): Movie? {
        if (position >= itemCount) return null
        return list?.get(position) ?: {null}()
    }
}