package com.lusle.android.soon.adapter.Holder

import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.Listener.OnItemClickListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class CompanyViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
    private val textView: TextView = itemView.findViewById(R.id.company_name)
    private val imageView: ImageView = itemView.findViewById(R.id.company_logo)
    private val altText: TextView = itemView.findViewById(R.id.alt_text)

    fun bind(company: Company) {
        itemView.setOnClickListener { v: View? ->
            onItemClickListener.onItemClick(v, layoutPosition)
            Log.d("TAG", "bind: $company")
        }
        textView.text = company.name
        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + company.logo_path)
                .fit()
                .centerInside()
                .into(imageView, object : Callback {

                    override fun onSuccess() {
                        altText.visibility = GONE
                        imageView.visibility = VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        altText.visibility = VISIBLE
                        imageView.visibility = GONE
                        altText.text = company.name[0].toString()
                    }
                })
    }
}