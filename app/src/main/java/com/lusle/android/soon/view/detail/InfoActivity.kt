package com.lusle.android.soon.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.adapter.CollectionListAdapter
import com.lusle.android.soon.model.api.MovieApi
import com.lusle.android.soon.model.schema.MovieDetail
import com.lusle.android.soon.model.source.RegionCodeRepository
import com.lusle.android.soon.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.DecimalFormat

class InfoActivity : AppCompatActivity() {

    //주요정보
    private lateinit var voteAverageRatingBar: RatingBar
    private lateinit var voteCountTextView: TextView
    private lateinit var voteAverageLabelTextView: TextView
    private lateinit var voteAverageTextView: TextView
    private lateinit var releaseDateTextView: TextView
    private lateinit var runtimeTextView: TextView
    private lateinit var overviewTextView: TextView
    private lateinit var collectionRecyclerView: RecyclerView
    private lateinit var collectionEmptyView: TextView
    private var collectionListAdapter: CollectionListAdapter? = null

    //기타정보
    private lateinit var homepageTextView: TextView
    private lateinit var revenueTextView: TextView
    private lateinit var budgetTextView: TextView
    private lateinit var originalLanguageTextView: TextView
    private lateinit var originalTitleTextView: TextView
    private lateinit var productionCountriesTextView: TextView
    private lateinit var spokenLanguagesTextView: TextView
    private lateinit var popularityTextView: TextView

    private val movieApi = MovieApi.create()
    private var movieDetailDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val movieId = intent.getIntExtra("movie_id", -1)

        if (movieId == -1) {
            // Error
        } else {
            voteAverageRatingBar = findViewById(R.id.vote_average)
            voteCountTextView = findViewById(R.id.vote_count)
            voteAverageLabelTextView = findViewById(R.id.vote_label)
            voteAverageTextView = findViewById(R.id.vote_average_text)
            releaseDateTextView = findViewById(R.id.release_date)
            runtimeTextView = findViewById(R.id.runtime)
            overviewTextView = findViewById(R.id.overview)
            collectionRecyclerView = findViewById(R.id.collection_recyclerview)
            collectionEmptyView = findViewById(R.id.collection_empty_view)

            homepageTextView = findViewById(R.id.homepage)
            revenueTextView = findViewById(R.id.revenue)
            budgetTextView = findViewById(R.id.budget)
            originalLanguageTextView = findViewById(R.id.original_lang)
            originalTitleTextView = findViewById(R.id.original_title)
            productionCountriesTextView = findViewById(R.id.production_countries)
            spokenLanguagesTextView = findViewById(R.id.spoken_languages)
            popularityTextView = findViewById(R.id.popularity)

            movieDetailDisposable = movieApi.getMovieDetails(RegionCodeRepository(this).regionCode, movieId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                bind(it)
                            },
                            {
                                it.printStackTrace()
                                //Error
                            })
        }
    }

    override fun onDestroy() {
        movieDetailDisposable?.dispose()
        super.onDestroy()
    }

    private fun bind(movieDetail: MovieDetail) {
        //평가
        if (movieDetail.voteCount == 0) {
            voteCountTextView.visibility = View.GONE
            voteAverageLabelTextView.text = "평가되지 않음"
            voteAverageRatingBar.visibility = View.GONE
            voteAverageTextView.visibility = View.GONE
        } else {
            voteCountTextView.text = movieDetail.voteCount.toString()
            voteAverageTextView.text = movieDetail.voteAverage.toString() + "/10"
            voteAverageRatingBar.rating = movieDetail.voteAverage.toFloat()
        }

        //개봉일
        if (movieDetail.releaseDate == "") {
            releaseDateTextView.text = "해당 정보 없음"
        } else {
            releaseDateTextView.text = movieDetail.releaseDate
        }

        //런타임
        movieDetail.runtime?.let {
            runtimeTextView.text = it.toString() + "분"
        } ?: {
            runtimeTextView.text = "해당 정보 없음"
        }()

        //줄거리
        movieDetail.overview?.let {
            overviewTextView.text = it
        } ?: {
            overviewTextView.text = "해당 정보 없음"
        }()

        //컬렉션
        movieDetail.belongsToCollection?.let {
            collectionListAdapter = CollectionListAdapter()
            collectionListAdapter!!.list.add(it)
            collectionRecyclerView.adapter = collectionListAdapter
            collectionRecyclerView.layoutManager = LinearLayoutManager(this)
        } ?: {
            collectionEmptyView.visibility = View.VISIBLE
            collectionRecyclerView.visibility = View.GONE
        }()

        //홈페이지
        movieDetail.homepage?.let {
            homepageTextView.text = it
        } ?: {
            homepageTextView.text = "해당 정보 없음"
        }()

        //수익
        revenueTextView.text = if (movieDetail.revenue == 0) "해당 정보 없음" else "$" + DecimalFormat("###,###").format(movieDetail.revenue)

        //제작비
        budgetTextView.text = if (movieDetail.budget == 0) "해당 정보 없음" else "$" + DecimalFormat("###,###").format(movieDetail.budget)

        //원어
        originalLanguageTextView.text = movieDetail.originalLanguage

        //원제
        originalTitleTextView.text = movieDetail.originalTitle

        //국가
        if (movieDetail.productionCountries.isEmpty()) {
            productionCountriesTextView.text = "해당 정보 없음"
        } else {
            productionCountriesTextView.text = movieDetail.productionCountries.joinToString(
                    separator = ", ",
                    transform = { it.name }
            )
        }

        //언어
        if (movieDetail.spokenLanguages.isEmpty()) {
            productionCountriesTextView.text = "해당 정보 없음"
        } else {
            spokenLanguagesTextView.text = movieDetail.spokenLanguages.joinToString(
                    separator = ", ",
                    transform = { it.name }
            )
        }

        //우선순위
        popularityTextView.text = movieDetail.popularity.toString()

    }
}