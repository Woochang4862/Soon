package com.lusle.android.soon.Adapter.Holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Adapter.Listener.OnCompanyBookMarkButtonClickListener
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.CircleTransform
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class SearchCompanyViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener, private val onCompanyBookMarkButtonClickListener: OnCompanyBookMarkButtonClickListener.Adapter) : RecyclerView.ViewHolder(itemView) {
    private val logo: ImageView = itemView.findViewById(R.id.logo)
    private val textView: TextView = itemView.findViewById(R.id.company_recyclerView_textView)
    private val bookMarkButton: LottieAnimationView = itemView.findViewById(R.id.company_recyclerview_bookmark)
    private val errorImage: LottieAnimationView = itemView.findViewById(R.id.error_image)

    fun bind(company: Company?, tempFavorite:ArrayList<Company?>){
        itemView.setOnClickListener{
            onItemClickListener.onItemClick(it, layoutPosition)
        }
        company?.let {
            Picasso
                    .get()
                    .load("https://image.tmdb.org/t/p/w500" + it.logo_path)
                    .centerCrop()
                    .transform(CircleTransform())
                    .fit()
                    .into(logo, object : Callback{
                        override fun onSuccess() {
                            errorImage.visibility = View.GONE
                            logo.visibility = View.VISIBLE
                            if(errorImage.isAnimating) errorImage.cancelAnimation()
                        }

                        override fun onError(e: Exception?) {
                            e?.printStackTrace()
                            errorImage.visibility = View.VISIBLE
                            logo.visibility = View.GONE
                            if(!errorImage.isAnimating) errorImage.playAnimation()
                        }
                    })
            textView.text = it.name
            if (tempFavorite.contains(it)) bookMarkButton.progress = 1f else bookMarkButton.progress = 0f
            bookMarkButton.setOnClickListener { v: View ->
                if (!bookMarkButton.isAnimating) {
                    onCompanyBookMarkButtonClickListener.onCompanyBookMarkButtonClicked(v, it)
                }
            }
        }

    }

}