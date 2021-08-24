package com.lusle.android.soon.Adapter.Holder

import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Adapter.Listener.OnBookButtonClickListener
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.Schema.Genre
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.Utils
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchMovieViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener?, private val onBookButtonClickListener: OnBookButtonClickListener?) : RecyclerView.ViewHolder(itemView) {

    private val imageView: ImageView = itemView.findViewById(R.id.movie_list_recyclerview_poster)
    private val errorImage: LottieAnimationView = itemView.findViewById(R.id.error_image)
    private val title: TextView = itemView.findViewById(R.id.movie_list_recyclerView_title)
    private val adult: TextView = itemView.findViewById(R.id.movie_list_recyclerview_adult)
    private val genre: TextView = itemView.findViewById(R.id.movie_list_recyclerview_genre)
    private val overview: TextView = itemView.findViewById(R.id.movie_list_recyclerview_overview)
    private val release: TextView = itemView.findViewById(R.id.movie_list_recyclerview_release)
    private val bookBtn: Button = itemView.findViewById(R.id.movie_list_recyclerview_d_day)

    fun bind(movie: Movie?, genres: MutableMap<Int, String>) {
        itemView.setOnClickListener { v: View? ->
            onItemClickListener?.onItemClick(v, layoutPosition)
        }
        movie?.let {
            Picasso
                    .get()
                    .load("https://image.tmdb.org/t/p/w500" + it.posterPath)
                    .centerCrop()
                    .fit()
                    .error(R.drawable.ic_broken_image)
                    .into(imageView, object : Callback {
                        override fun onSuccess() {
                            errorImage.visibility = View.GONE
                            imageView.visibility = View.VISIBLE
                            if (errorImage.isAnimating) errorImage.cancelAnimation()
                        }

                        override fun onError(e: Exception) {
                            errorImage.visibility = View.VISIBLE
                            imageView.visibility = View.GONE
                            if (!errorImage.isAnimating) errorImage.playAnimation()
                        }
                    })
            title.text = it.title
            adult.visibility = if (it.adult) View.VISIBLE else View.GONE

            val genreList = ArrayList<String?>()
            for (id in it.genreIds) {
                genreList.add(genres[id])
            }
            genre.text = TextUtils.join(",", genreList)

            if (it.overview.equals("", ignoreCase = true)) overview.visibility = View.GONE else if (it.overview.length <= 60) overview.text = it.overview else overview.text = it.overview.substring(0, 61)
            release.text = "개봉일 : " + it.releaseDate

            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val date = sdf.parse(it.releaseDate)
            date?.let { _date ->
                val releaseDate: Calendar = GregorianCalendar()
                releaseDate.time = _date
                val day = Utils.calDDay(releaseDate)
                if (day <= 0) {
                    bookBtn.isEnabled = false
                    bookBtn.text = "개봉함"
                } else {
                    bookBtn.isEnabled = true
                    bookBtn.text = "DAY - $day"
                    bookBtn.setOnClickListener { _: View? -> onBookButtonClickListener?.onBookButtonClicked(it) }
                }
            } ?: {
                bookBtn.text = "---"
                bookBtn.isEnabled = false
            }()

        }
    }
}