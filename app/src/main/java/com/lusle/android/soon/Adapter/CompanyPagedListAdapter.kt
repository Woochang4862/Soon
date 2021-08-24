package com.lusle.android.soon.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Adapter.Holder.SearchCompanyViewHolder
import com.lusle.android.soon.Adapter.Listener.OnCompanyBookMarkButtonClickListener
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.R
import java.util.*

val diffCompanyItemCallback = object : DiffUtil.ItemCallback<Company>() {
    override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean = oldItem.name.trim() == newItem.name.trim()

}

class CompanyPagedListAdapter(private val tempFavorite: ArrayList<Company?>, private val onItemClickListener: OnItemClickListener, private val onEmptyListener: OnEmptyListener, private val onCompanyBookMarkButtonClickListener: OnCompanyBookMarkButtonClickListener.View) : PagedListAdapter<Company, SearchCompanyViewHolder>(diffCompanyItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchCompanyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_compay_recyclerview, parent, false)
        return SearchCompanyViewHolder(view, onItemClickListener, object : OnCompanyBookMarkButtonClickListener.Adapter {
            override fun onCompanyBookMarkButtonClicked(view: View, company: Company) {
                if (tempFavorite.contains(company)) { //삭제
                    tempFavorite.remove(company)
                    (view as LottieAnimationView).speed = -2f
                    view.playAnimation()
                } else { //추가
                    tempFavorite.add(company)
                    (view as LottieAnimationView).speed = 2f
                    view.playAnimation()
                }
                onCompanyBookMarkButtonClickListener.onCompanyBookMarkButtonClicked(tempFavorite)
            }
        })
    }

    override fun onBindViewHolder(holder: SearchCompanyViewHolder, position: Int) {
        holder.bind(getItem(position), tempFavorite)
    }

    public override fun getItem(position: Int): Company? {
        return super.getItem(position)
    }

    fun onEmpty() {
        onEmptyListener.onEmpty()
    }

    fun onNotEmpty() {
        onEmptyListener.onNotEmpty()
    }
}