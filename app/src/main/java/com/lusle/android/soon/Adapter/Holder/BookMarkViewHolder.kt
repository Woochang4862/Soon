package com.lusle.android.soon.Adapter.Holder

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class BookMarkViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
    private val textView: TextView = itemView.findViewById(R.id.company_name)
    private val imageView: ImageView = itemView.findViewById(R.id.company_logo)
    private val error_image: LottieAnimationView = itemView.findViewById(R.id.error_image)

    fun bind(company: Company) {
        itemView.setOnClickListener { v: View? -> onItemClickListener?.onItemClick(v, layoutPosition) }
        textView.text = company.name
        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + company.logo_path)
                .fit()
                .centerInside()
                .into(imageView, object : Callback {

                    override fun onSuccess() {
                        error_image.visibility = GONE
                        imageView.visibility = VISIBLE
                        if(error_image.isAnimating) error_image.cancelAnimation()
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        error_image.visibility = VISIBLE
                        imageView.visibility = GONE
                        if(!error_image.isAnimating) error_image.playAnimation()
                    }
                })
    }
}