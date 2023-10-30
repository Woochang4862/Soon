package com.lusle.android.soon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.holder.CompanyAlarmViewHolder
import com.lusle.android.soon.adapter.listener.OnCheckedChangeListener
import com.lusle.android.soon.adapter.listener.OnItemClickListener

class CompanyAlarmSettingsAdapter(private val onCheckedChangeListener: OnCheckedChangeListener) : BaseRecyclerAdapter<RecyclerView.ViewHolder?>() {
    private var list: ArrayList<Company> = ArrayList()

    private var onItemClickListener: OnItemClickListener? = null
    var topics: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_company_alarm_recyclerview, parent, false)
        return CompanyAlarmViewHolder(v, onCheckedChangeListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CompanyAlarmViewHolder).bind(list[position])

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(list: ArrayList<Company>) {
        this.list = list
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    companion object {
        val TAG: String = CompanyAlarmSettingsAdapter::class.java.simpleName
    }
}