package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.adapter.holder.HeaderViewHolder
import com.lusle.android.soon.adapter.holder.MovieViewHolder
import com.lusle.android.soon.adapter.listener.OnEmptyListener
import com.lusle.android.soon.adapter.listener.OnItemClickListener
import com.lusle.android.soon.model.schema.UiModel
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.holder.CompanyHeaderViewHolder
import com.lusle.android.soon.adapter.listener.OnCompanyBookMarkButtonClickListener

val diffMovieItemCallback = object : DiffUtil.ItemCallback<UiModel>() {
    override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        if (oldItem is UiModel.Header) return false
        return (oldItem as UiModel.MovieModel).movie.id == (newItem as UiModel.MovieModel).movie.id
    }

    override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        if (oldItem is UiModel.Header) return false
        return (oldItem as UiModel.MovieModel).movie.title.trim().equals((newItem as UiModel.MovieModel).movie.title.trim(), false)
    }

}

class MoviePagedListAdapter(private val onItemClickListener: OnItemClickListener, private val onEmptyListener: OnEmptyListener, private val onCompanyBookMarkButtonClickListener: OnCompanyBookMarkButtonClickListener?=null) : PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(diffMovieItemCallback) {

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is UiModel.MovieModel -> R.layout.item_movie_recyclerview
            is UiModel.Header -> R.layout.header_movie
            is UiModel.CompanyHeader -> R.layout.header_company
            null -> throw IllegalStateException("Unknown view")
            else -> throw IllegalStateException("Unknown view")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            R.layout.item_movie_recyclerview -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_recyclerview, parent, false)
                return MovieViewHolder(view, onItemClickListener)
            }
            R.layout.header_movie -> {
                HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_movie, parent, false))
            }
            else -> {
                CompanyHeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.header_company, parent, false), onCompanyBookMarkButtonClickListener)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.MovieModel -> {
                    val viewHolder = holder as MovieViewHolder
                    viewHolder.bind(uiModel.movie)
                }
                is UiModel.Header -> {
                    val viewHolder = holder as HeaderViewHolder
                    viewHolder.bind(uiModel.title)
                }
                is UiModel.CompanyHeader -> {
                    val viewHolder = holder as CompanyHeaderViewHolder
                    viewHolder.bind(uiModel.title, uiModel.isSubscribed)
                }

                else -> {}
            }
        }
    }

    fun onEmpty() {
        onEmptyListener.onEmpty()
    }

    fun onNotEmpty() {
        onEmptyListener.onNotEmpty()
    }
}