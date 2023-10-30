package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.model.schema.UiModel
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.holder.CompanyResultViewHolder
import com.lusle.android.soon.adapter.holder.MovieViewHolder
import com.lusle.android.soon.adapter.holder.SectionHeaderViewHolder
import com.lusle.android.soon.adapter.listener.OnEmptyListener
import com.lusle.android.soon.adapter.listener.OnItemClickListener

//TODO:예외처리
class SearchResultAdapter(private val onMovieItemClickListener: OnItemClickListener, private val onSeeAllButtonClickListener: View.OnClickListener, private val onCompanyItemClickListener: OnItemClickListener, private val onEmptyListener: OnEmptyListener) : PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(diffMovieItemCallback) {

    private var companyResultViewHolder: CompanyResultViewHolder? = null

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.MovieModel -> R.layout.item_movie_recyclerview
            is UiModel.CompanyResult -> R.layout.section_company_result
            is UiModel.Header -> R.layout.header_section
            null -> throw IllegalStateException("Unknown view")
            else -> throw IllegalStateException("Unknown view")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_movie_recyclerview -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_movie_recyclerview, parent, false)
                return MovieViewHolder(view, onMovieItemClickListener)
            }

            R.layout.section_company_result -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.section_company_result, parent, false)
                companyResultViewHolder = CompanyResultViewHolder(view, onCompanyItemClickListener)
                return companyResultViewHolder!!
            }

            R.layout.header_section -> {
                SectionHeaderViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.header_section, parent, false),
                    onSeeAllButtonClickListener
                )
            }

            else -> throw IllegalStateException("Unknown View Type")
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

                is UiModel.CompanyResult -> {
                    val viewHolder = holder as CompanyResultViewHolder
                    viewHolder.bind(uiModel.companyList)
                }

                is UiModel.Header -> {
                    val viewHolder = holder as SectionHeaderViewHolder
                    viewHolder.bind(uiModel.title)
                }

                else -> {}
            }
        }
    }

    fun getCompanyItem(companyPosition: Int):Company? {
        companyResultViewHolder?.let{
            return it.getCompanyItem(companyPosition)
        } ?: run {
            return null
        }
    }

    fun onEmpty() {
        onEmptyListener.onEmpty()
    }

    fun onNotEmpty() {
        onEmptyListener.onNotEmpty()
    }

}
