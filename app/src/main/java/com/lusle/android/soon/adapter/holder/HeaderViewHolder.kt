package com.lusle.android.soon.adapter.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.listener.OnCompanyBookMarkButtonClickListener

open class HeaderViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textView = itemView.findViewById<TextView>(R.id.header_text)

    open fun bind(text:String){
        textView.text = text
    }

}
