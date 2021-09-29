package com.lusle.android.soon.Adapter.Holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Model.Schema.Cast
import com.lusle.android.soon.Model.Schema.Person
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.CircleTransform
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class CastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
    private val errorImage: LottieAnimationView = itemView.findViewById(R.id.error_image)
    private val name: TextView = itemView.findViewById(R.id.name)

    fun bind(cast: Cast) {
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + cast.profilePath)
                .centerCrop()
                .transform(CircleTransform())
                .fit()
                .into(profileImage, object : Callback {

                    override fun onSuccess() {
                        errorImage.visibility = View.GONE
                        profileImage.visibility = View.VISIBLE
                        if(errorImage.isAnimating) errorImage.cancelAnimation()
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        errorImage.visibility = View.VISIBLE
                        profileImage.visibility = View.GONE
                        if(!errorImage.isAnimating) errorImage.playAnimation()
                    }

                })
        name.text = cast.name
    }

}
