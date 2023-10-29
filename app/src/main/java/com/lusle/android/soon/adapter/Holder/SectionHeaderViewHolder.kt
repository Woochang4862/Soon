package com.lusle.android.soon.adapter.Holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.R

class SectionHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textView = itemView.findViewById<TextView>(R.id.header_text)
    private val seeAllButton = itemView.findViewById<TextView>(R.id.see_all_button)

    fun bind(text:String){
        textView.text = text
        when(text) {
            "제작사 결과"->{
                seeAllButton.setOnClickListener {

                }
                seeAllButton.visibility = View.VISIBLE
            }
            else->{
                seeAllButton.visibility = View.GONE
            }
        }
    }

}
