package com.lusle.android.soon.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.CircleTransform
import com.lusle.android.soon.adapter.Holder.CompanyAlarmViewHolder
import com.lusle.android.soon.adapter.Listener.OnCheckedChangeListener
import com.lusle.android.soon.adapter.Listener.OnItemClickListener
import com.squareup.picasso.Picasso

class CompanyAlarmSettingsAdapter(private val onCheckedChangeListener: OnCheckedChangeListener) : BaseRecyclerAdapter<RecyclerView.ViewHolder?>() {
    private var list: ArrayList<Company> = ArrayList()

    private var onItemClickListener: OnItemClickListener? = null
    var topics: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_company_alarm_recyclerview, parent, false)
        return CompanyAlarmViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val companyAlarmViewHolder = holder as CompanyAlarmViewHolder
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + list[position].logo_path)
                .centerCrop()
                .transform(CircleTransform())
                .fit()
                .error(R.drawable.ic_broken_image)
                .into(companyAlarmViewHolder.companyLogo)
        companyAlarmViewHolder.companyName.text = list[position].name
        try {
            companyAlarmViewHolder.companyAlarmActive.isEnabled = true
            companyAlarmViewHolder.companyAlarmActive.isChecked = false
            for (topic in topics) {
                Log.d("3", "onBindViewHolder: " + (topic.trim { it <= ' ' } == list[position].id.toString()))
                if (topic.trim { it <= ' ' } == list[position].id.toString()) {
                    Log.d(TAG, "onBindViewHolder: RUN!")
                    companyAlarmViewHolder.companyAlarmActive.isChecked = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            companyAlarmViewHolder.companyAlarmActive.isEnabled = false
        }
        companyAlarmViewHolder.companyAlarmActive.setOnCheckedChangeListener { view, isChecked ->
            onCheckedChangeListener.onCheckedChangeListener(view, isChecked, list[position])
        }
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