package com.lusle.android.soon.adapter.holder

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.CompanyListAdapter
import com.lusle.android.soon.adapter.listener.OnItemClickListener
import com.lusle.android.soon.model.schema.Company

class CompanyResultViewHolder(
    view: View,
    onCompanyItemClickListener: OnItemClickListener
) : RecyclerView.ViewHolder(view) {

    private val companyListRecyclerView = view.findViewById<RecyclerView>(R.id.company_list_recyclerView)
    private val companyListAdapter = CompanyListAdapter(onCompanyItemClickListener)
    private val layoutManager = LinearLayoutManager(view.context)
    fun bind(companyList: ArrayList<Company>) {
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        companyListRecyclerView.layoutManager = layoutManager
        companyListAdapter.list.addAll(companyList)
        companyListRecyclerView.adapter = companyListAdapter
    }

    fun getCompanyItem(companyPosition: Int): Company {
        return companyListAdapter.getItem(companyPosition)
    }
}
