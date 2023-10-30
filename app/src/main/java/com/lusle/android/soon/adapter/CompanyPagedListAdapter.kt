package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.holder.CompanyViewHolder
import com.lusle.android.soon.adapter.listener.OnItemClickListener
import com.lusle.android.soon.model.schema.Company

val diffCompanyItemCallback = object : DiffUtil.ItemCallback<Company>() {
    override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean {
        return oldItem.name.trim().equals(newItem.name.trim(), false)
    }

}

class CompanyPagedListAdapter(private val onItemClickListener: OnItemClickListener) : PagingDataAdapter<Company, CompanyViewHolder>(diffCompanyItemCallback){
    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        getItem(position)?.let{holder.bind(it)}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        return CompanyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_company, parent, false),
            onItemClickListener
        )
    }

    fun getCompanyItem(position: Int):Company? {
        return getItem(position)
    }

}
