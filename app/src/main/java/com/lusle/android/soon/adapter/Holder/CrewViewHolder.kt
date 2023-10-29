package com.lusle.android.soon.adapter.Holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Model.Schema.Crew
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.CircleTransform
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class CrewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
    private val altText: TextView = itemView.findViewById(R.id.alt_text)
    private val name: TextView = itemView.findViewById(R.id.name)

    fun bind(crew: Crew) {
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + crew.profilePath)
                .centerCrop()
                .transform(CircleTransform())
                .fit()
                .into(profileImage, object : Callback {

                    override fun onSuccess() {
                        altText.visibility = View.GONE
                        profileImage.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        altText.visibility = View.VISIBLE
                        profileImage.visibility = View.GONE
                        altText.text = crew.name[0].toString()
                    }

                })
        name.text = crew.name
    }

}
