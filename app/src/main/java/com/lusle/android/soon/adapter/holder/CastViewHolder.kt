package com.lusle.android.soon.adapter.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.model.schema.Cast
import com.lusle.android.soon.R
import com.lusle.android.soon.util.CircleTransform
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class CastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
    private val altText: TextView = itemView.findViewById(R.id.alt_text)
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
                        altText.visibility = View.GONE
                        profileImage.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        altText.visibility = View.VISIBLE
                        profileImage.visibility = View.GONE
                        altText.text = cast.name[0].toString()
                    }

                })
        name.text = cast.name
    }

}
