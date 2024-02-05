package com.lusle.android.soon.adapter.holder

import android.app.Activity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.CompanyListAdapter
import com.lusle.android.soon.adapter.decoration.CompanyItemDecoration
import com.lusle.android.soon.adapter.listener.OnItemClickListener
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.util.TransformationCompat.getActivity

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
        if (companyListRecyclerView.itemDecorationCount == 0)
            itemView.context.getActivity()?.let {activity: Activity ->  companyListRecyclerView.addItemDecoration(CompanyItemDecoration(activity, -1, 9f, 0f))}
        companyListAdapter.list.addAll(companyList)
        companyListRecyclerView.adapter = companyListAdapter
    }

    fun getCompanyItem(companyPosition: Int): Company {
        return companyListAdapter.getItem(companyPosition)
    }
}
