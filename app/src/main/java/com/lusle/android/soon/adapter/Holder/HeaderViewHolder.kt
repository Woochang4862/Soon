package com.lusle.android.soon.adapter.Holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.Listener.OnCompanyBookMarkButtonClickListener

open class HeaderViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textView = itemView.findViewById<TextView>(R.id.header_text)

    open fun bind(text:String){
        textView.text = text
    }

}
