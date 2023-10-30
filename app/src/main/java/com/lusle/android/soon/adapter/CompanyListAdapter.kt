package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.adapter.holder.CompanyViewHolder
import com.lusle.android.soon.adapter.listener.OnEmptyListener
import com.lusle.android.soon.adapter.listener.OnItemClickListener
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.R
import java.util.*

class CompanyListAdapter(private val onItemClickListener: OnItemClickListener, private val onEmptyListener: OnEmptyListener? = null) : RecyclerView.Adapter<CompanyViewHolder>(){

    val list: ArrayList<Company> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CompanyViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_company, viewGroup, false)
        return CompanyViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(viewHolder: CompanyViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(position: Int): Company {
        return list[position]
    }

    fun onEmpty() = onEmptyListener?.onEmpty()

    fun onNotEmpty() = onEmptyListener?.onNotEmpty()

}