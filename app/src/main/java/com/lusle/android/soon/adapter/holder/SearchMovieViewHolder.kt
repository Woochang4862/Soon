package com.lusle.android.soon.adapter.holder

import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.adapter.listener.OnBookButtonClickListener
import com.lusle.android.soon.adapter.listener.OnItemClickListener
import com.lusle.android.soon.model.schema.Movie
import com.lusle.android.soon.model.source.RegionCodeRepository
import com.lusle.android.soon.R
import com.lusle.android.soon.util.Utils
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SearchMovieViewHolder(
    itemView: View,
    private val onItemClickListener: OnItemClickListener?,
    private val onBookButtonClickListener: OnBookButtonClickListener?
) : RecyclerView.ViewHolder(itemView) {

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
        movie?.let { movie ->
            Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + movie.posterPath)
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
            title.text = movie.title
            adult.visibility = if (movie.adult) View.VISIBLE else View.GONE

            val genreList = ArrayList<String?>()
            for (id in movie.genreIds) {
                genreList.add(genres[id])
            }
            genre.text = TextUtils.join(",", genreList)

            movie.overview?.let { _overview ->
                if (_overview.length <= 60)
                    overview.text = _overview
                else
                    overview.text = _overview.substring(0, 61)
            } ?: run {
                overview.visibility = View.GONE
            }
            release.text = "개봉일 : ${movie.releaseDate}"


            val sdf = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale(RegionCodeRepository(itemView.context).regionCode)
            )
            val date: Date?
            try {
                date = sdf.parse(movie.releaseDate)
                date?.let {
                    val releaseDateCalendar: Calendar = GregorianCalendar()
                    releaseDateCalendar.time = date
                    val day = Utils.calDDay(releaseDateCalendar)
                    if (day <= 0) {
                        bookBtn.isEnabled = false
                        bookBtn.text = "개봉함"
                    } else {
                        bookBtn.isEnabled = true
                        bookBtn.text = "DAY-$day"
                        bookBtn.setOnClickListener { _: View? ->
                            onBookButtonClickListener?.onBookButtonClicked(
                                movie
                            )
                        }
                    }
                } ?: run { throw Exception("function parse returns null") }
            } catch (e: Exception) {
                bookBtn.visibility = View.GONE
                e.printStackTrace()
            }
        }
    }
}