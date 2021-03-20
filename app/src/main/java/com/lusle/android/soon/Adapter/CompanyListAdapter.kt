package com.lusle.android.soon.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.Adapter.Contract.FragmentFavoriteCompanyAdapterContract
import com.lusle.android.soon.Adapter.Holder.BookMarkViewHolder
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.R
import java.util.*

class CompanyListAdapter(private val onItemClickListener: OnItemClickListener, private val onEmptyListener: OnEmptyListener) : RecyclerView.Adapter<BookMarkViewHolder>(), FragmentFavoriteCompanyAdapterContract.View, FragmentFavoriteCompanyAdapterContract.Model {

    val list: ArrayList<Company> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): BookMarkViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_company, viewGroup, false)
        return BookMarkViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(viewHolder: BookMarkViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Company {
        return list[position]
    }

    fun onEmpty() = onEmptyListener.onEmpty()

    fun onNotEmpty() = onEmptyListener.onNotEmpty()
}