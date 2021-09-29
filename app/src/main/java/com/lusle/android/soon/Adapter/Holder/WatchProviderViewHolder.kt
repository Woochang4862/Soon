package com.lusle.android.soon.Adapter.Holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Model.Schema.WatchProvider
import com.lusle.android.soon.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class WatchProviderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val textView: TextView = itemView.findViewById(R.id.watch_provider_name)
    private val imageView: ImageView = itemView.findViewById(R.id.watch_provider_logo)
    private val errorImage: LottieAnimationView = itemView.findViewById(R.id.error_image)

    fun bind(watchProvider: WatchProvider) {
        textView.text = watchProvider.providerName
        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + watchProvider.logoPath)
                .fit()
                .centerInside()
                .into(imageView, object : Callback {

                    override fun onSuccess() {
                        errorImage.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                        if(errorImage.isAnimating) errorImage.cancelAnimation()
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        errorImage.visibility = View.VISIBLE
                        imageView.visibility = View.GONE
                        if(!errorImage.isAnimating) errorImage.playAnimation()
                    }
                })
    }

}
