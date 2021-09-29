package com.lusle.android.soon.Adapter.Holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Model.Schema.BelongsToCollection
import com.lusle.android.soon.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val textView: TextView = itemView.findViewById(R.id.collection_name)
    private val imageView: ImageView = itemView.findViewById(R.id.collection_poster)
    private val errorImage: LottieAnimationView = itemView.findViewById(R.id.error_image)

    fun bind(belongsToCollection: BelongsToCollection) {
        textView.text = belongsToCollection.name
        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + belongsToCollection.posterPath)
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
