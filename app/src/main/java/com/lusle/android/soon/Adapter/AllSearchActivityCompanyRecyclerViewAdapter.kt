package com.lusle.android.soon.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Adapter.Holder.SearchCompanyViewHolder
import com.lusle.android.soon.Adapter.Listener.OnCompanyBookMarkButtonClickListener
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.R
import java.util.*

class AllSearchActivityCompanyRecyclerViewAdapter(private val tempFavorite: ArrayList<Company?>, private val onItemClickListener: OnItemClickListener, private val onEmptyListener: OnEmptyListener, private val onCompanyBookMarkButtonClickListener: OnCompanyBookMarkButtonClickListener.View) : BaseRecyclerAdapter<SearchCompanyViewHolder>() {

    private var list: ArrayList<Company>? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SearchCompanyViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_compay_recyclerview, viewGroup, false)
        return SearchCompanyViewHolder(view, onItemClickListener, object :OnCompanyBookMarkButtonClickListener.Adapter {
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

    override fun onBindViewHolder(viewHolder: SearchCompanyViewHolder, i: Int) {
        Log.d("TAG", "왜 바인딩이 안됨?")
        viewHolder.bind(list?.get(i), tempFavorite)
    }

    override fun getItemCount(): Int {
        return list?.size ?:{0}()
    }

    override fun onEmpty() = onEmptyListener.onEmpty()

    override fun onNotEmpty() = onEmptyListener.onNotEmpty()

    fun setList(list: ArrayList<Company>?) {
        this.list = list
    }

    fun clear() {
        list?.clear() ?: {list = arrayListOf() }()
    }

    fun getItem(position: Int): Company? {
        if (position >= itemCount) return null
        return list?.get(position) ?: {null}()
    }
}