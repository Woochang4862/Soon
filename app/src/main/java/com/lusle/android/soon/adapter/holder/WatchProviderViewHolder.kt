package com.lusle.android.soon.adapter.holder

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.model.schema.WatchProvider
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.listener.OnItemClickListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class WatchProviderViewHolder(itemView: View, private val onClickListener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {

    private val textView: TextView = itemView.findViewById(R.id.watch_provider_wrapper)
    private val imageView: ImageView = itemView.findViewById(R.id.watch_provider_logo)
    private val altTextView: TextView = itemView.findViewById(R.id.alt_text)

    fun bind(watchProvider: WatchProvider) {
        itemView.setOnClickListener {
            onClickListener.onItemClick(it, layoutPosition)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(watchProvider.link))
            itemView.context.startActivity(browserIntent)
        }
        watchProvider.price?.let {
            textView.text = it +' '+ watchProvider.presentationType
        } ?: run {
            textView.visibility = View.GONE
        }

        Picasso.get()
                .load("https://image.tmdb.org/t/p/original" + watchProvider.logoPath)
                .fit()
                .centerInside()
                .into(imageView, object : Callback {

                    override fun onSuccess() {
                        altTextView.visibility = View.GONE
                        imageView.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                        altTextView.visibility = View.VISIBLE
                        imageView.visibility = View.GONE
                        altTextView.text = watchProvider.providerName
                    }
                })
    }

}
