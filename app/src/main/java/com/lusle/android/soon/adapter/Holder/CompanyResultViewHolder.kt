package com.lusle.android.soon.adapter.Holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.CompanyListAdapter
import com.lusle.android.soon.adapter.Listener.OnItemClickListener

class CompanyResultViewHolder(
    view: View,
    private val onSeeAllButtonClickListener: View.OnClickListener,
    onCompanyItemClickListener: OnItemClickListener
) : RecyclerView.ViewHolder(view) {

    private val seeAllButton = view.findViewById<TextView>(R.id.see_all_button)
    private val companyListRecyclerView = view.findViewById<RecyclerView>(R.id.company_list_recyclerView)
    private val companyListAdapter = CompanyListAdapter(onCompanyItemClickListener)
    private val layoutManager = LinearLayoutManager(view.context)
    fun bind(companyList: ArrayList<Company>) {
        seeAllButton.setOnClickListener {
            onSeeAllButtonClickListener.onClick(it)
        }
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        companyListRecyclerView.layoutManager = layoutManager
        companyListAdapter.list.addAll(companyList)
        companyListRecyclerView.adapter = companyListAdapter
    }

    fun getCompanyItem(companyPosition: Int): Company {
        return companyListAdapter.getItem(companyPosition)
    }
}
