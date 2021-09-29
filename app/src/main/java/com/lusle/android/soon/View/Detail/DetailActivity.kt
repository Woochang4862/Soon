package com.lusle.android.soon.View.Detail

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lusle.android.soon.Adapter.*
import com.lusle.android.soon.Adapter.Decoration.MovieItemDecoration
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Model.API.MovieApi
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.Model.Schema.MovieDetail
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.SubtitleCollapsingToolbarLayout.SubtitleCollapsingToolbarLayout
import com.lusle.android.soon.Util.Utils
import com.lusle.android.soon.View.Alarm.AlarmSettingActivity
import com.lusle.android.soon.View.BaseActivity
import com.lusle.android.soon.View.MovieList.MovieListActivity
import com.lusle.android.soon.View.YoutubePlayer.YoutubePlayerActivity
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : BaseActivity() {

    private lateinit var poster: ImageView
    private lateinit var toolbar: SubtitleCollapsingToolbarLayout
    private lateinit var scrollDownArrow: LottieAnimationView
    private lateinit var headerVoteAverage: RatingBar

    //주요정보
    private lateinit var infoSection: CardView
    private lateinit var voteAverage: RatingBar
    private lateinit var voteCount: TextView
    private lateinit var voteAverageLabel: TextView
    private lateinit var voteAverageText: TextView
    private lateinit var releaseDate: TextView
    private lateinit var runtime: TextView
    private lateinit var subscribeReleaseAlarmButton: Button

    //장르
    private lateinit var genreChipGroup: ChipGroup
    private lateinit var genreEmptyView: TextView

    //제작사
    private lateinit var companyRecyclerView: RecyclerView
    private lateinit var companyEmptyView: TextView
    private var companyListAdapter: CompanyListAdapter? = null
    private var companyLayoutManager: LinearLayoutManager? = null

    //공급사
    private lateinit var watchProviderRecyclerView: RecyclerView
    private lateinit var watchProviderEmptyView: TextView
    private var watchProviderListAdapter: WatchProviderListAdapter? = null
    private var watchProviderLayoutManager: LinearLayoutManager? = null

    //예고편
    private lateinit var videoRecyclerView: RecyclerView
    private lateinit var videoEmptyView: TextView
    private var videoThumbnailAdapter: VideoThumbnailAdapter? = null
    private var videoLayoutManager: LinearLayoutManager? = null

    //이미지
    private lateinit var previewImageRecyclerView: RecyclerView
    private lateinit var previewEmptyView: TextView
    private var previewImageAdapter: PreviewImageAdapter? = null
    private var previewImageLayoutManager: LinearLayoutManager? = null

    //출연진
    private lateinit var castRecyclerView: RecyclerView
    private lateinit var castEmptyView: TextView
    private var castListAdapter: CastListAdapter? = null
    private var castLayoutManager: LinearLayoutManager? = null

    //제작진
    private lateinit var crewRecyclerView: RecyclerView
    private lateinit var crewEmptyView: TextView
    private var crewListAdapter: CrewListAdapter? = null
    private var crewLayoutManager: LinearLayoutManager? = null

    //유사영화
    private lateinit var similarMovieRecyclerView: RecyclerView
    private lateinit var similarMovieEmptyView: TextView
    private lateinit var similarMovieLoadMoreButton: RelativeLayout
    private var similarMovieListAdapter: MovieListAdapter? = null
    private var similarMovieLayoutManager: LinearLayoutManager? = null

    private val movieApi = MovieApi.create()
    private var movieDetailDisposable: Disposable? = null
    private var watchProviderDisposable: Disposable? = null
    private var creditDisposable: Disposable? = null
    private var similarMovieDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        poster = findViewById(R.id.poster)
        toolbar = findViewById(R.id.collapsing_toolbar_layout)
        headerVoteAverage = findViewById(R.id.vote_average_header)
        scrollDownArrow = findViewById(R.id.scroll_down_arrow)
        scrollDownArrow.scale = 0.06f

        //주요정보
        infoSection = findViewById(R.id.info_section)
        voteCount = findViewById(R.id.vote_count)
        voteAverageLabel = findViewById(R.id.vote_label)
        voteAverage = findViewById(R.id.vote_average)
        voteAverageText = findViewById(R.id.vote_average_text)
        releaseDate = findViewById(R.id.release_date)
        runtime = findViewById(R.id.runtime)
        subscribeReleaseAlarmButton = findViewById(R.id.subscribe_release_alarm_button)

        //장르
        genreChipGroup = findViewById(R.id.genre_chip_group)
        genreEmptyView = findViewById(R.id.genre_empty_view)

        //제작사
        companyRecyclerView = findViewById(R.id.company_recyclerview)
        companyEmptyView = findViewById(R.id.company_empty_view)


        //공급사
        watchProviderRecyclerView = findViewById(R.id.watch_provider_recyclerview)
        watchProviderEmptyView = findViewById(R.id.watch_provider_empty_view)

        //에고편
        videoRecyclerView = findViewById(R.id.video_thumbnail_recyclerView)
        videoLayoutManager = LinearLayoutManager(this)
        videoLayoutManager!!.orientation = RecyclerView.HORIZONTAL
        videoRecyclerView.layoutManager = videoLayoutManager
        videoThumbnailAdapter = VideoThumbnailAdapter()
        videoThumbnailAdapter!!.setOnClickListener { view: View?, position: Int ->
            val intent = Intent(this, YoutubePlayerActivity::class.java)
            intent.putExtra("VIDEO_ID", videoThumbnailAdapter!!.getItem(position).key)
            startActivity(intent)
        }
        videoThumbnailAdapter!!.setOnEmptyListener(object : OnEmptyListener {
            override fun onEmpty() {
                videoEmptyView.visibility = View.VISIBLE
                videoRecyclerView.visibility = View.GONE
            }

            override fun onNotEmpty() {
                videoEmptyView.visibility = View.GONE
                videoRecyclerView.visibility = View.VISIBLE
            }
        })
        videoRecyclerView.adapter = videoThumbnailAdapter
        videoEmptyView = findViewById(R.id.video_empty_view)

        //이미지
        previewImageRecyclerView = findViewById(R.id.preview_image_recyclerView)
        previewImageLayoutManager = LinearLayoutManager(this)
        previewImageLayoutManager!!.orientation = RecyclerView.HORIZONTAL
        previewImageRecyclerView.layoutManager = previewImageLayoutManager
        previewImageAdapter = PreviewImageAdapter()
        previewImageAdapter!!.setOnItemClickListener { _: View?, _: Int -> }
        previewImageAdapter!!.setOnEmptyListener(object : OnEmptyListener {
            override fun onEmpty() {
                previewEmptyView.visibility = View.VISIBLE
                previewImageRecyclerView.visibility = View.GONE
            }

            override fun onNotEmpty() {
                previewEmptyView.visibility = View.GONE
                previewImageRecyclerView.visibility = View.VISIBLE
            }
        })
        previewImageRecyclerView.adapter = previewImageAdapter
        previewEmptyView = findViewById(R.id.preview_image_empty_view)

        //출연진
        castRecyclerView = findViewById(R.id.cast_recyclerview)
        castEmptyView = findViewById(R.id.cast_empty_view)
        castLayoutManager = LinearLayoutManager(this)
        castLayoutManager!!.orientation = RecyclerView.HORIZONTAL
        castRecyclerView.layoutManager = castLayoutManager
        castListAdapter = CastListAdapter()
        castRecyclerView.adapter = castListAdapter

        //제작진
        crewRecyclerView = findViewById(R.id.crew_recyclerview)
        crewEmptyView = findViewById(R.id.crew_empty_view)
        crewLayoutManager = LinearLayoutManager(this)
        crewLayoutManager!!.orientation = RecyclerView.HORIZONTAL
        crewRecyclerView.layoutManager = crewLayoutManager
        crewListAdapter = CrewListAdapter()
        crewRecyclerView.adapter = crewListAdapter

        //유사영화
        similarMovieEmptyView = findViewById(R.id.similar_movie_empty_view)
        similarMovieLoadMoreButton = findViewById(R.id.similar_movie_more)
        similarMovieRecyclerView = findViewById(R.id.similar_movie_recyclerview)
        similarMovieListAdapter = MovieListAdapter(
                { view, position ->
                    val intent = Intent(this, DetailActivity::class.java)
                    intent.putExtra("movie_id", similarMovieListAdapter?.getItem(position)?.id)
                    val poster = Pair.create(view.findViewById<View>(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(view.findViewById(R.id.movie_list_recyclerview_poster)))
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation((view.context as Activity), poster)
                    startActivity(intent, options.toBundle())
                },
                object : OnEmptyListener {
                    override fun onEmpty() {
                        similarMovieEmptyView.visibility = View.VISIBLE
                        similarMovieRecyclerView.visibility = View.GONE
                        similarMovieLoadMoreButton.visibility = View.GONE
                    }

                    override fun onNotEmpty() {
                        similarMovieEmptyView.visibility = View.GONE
                        similarMovieRecyclerView.visibility = View.VISIBLE
                        similarMovieLoadMoreButton.visibility = View.VISIBLE
                    }
                }
        )
        similarMovieRecyclerView.adapter = similarMovieListAdapter
        similarMovieLayoutManager = GridLayoutManager(this, 2)
        similarMovieRecyclerView.layoutManager = similarMovieLayoutManager
        similarMovieRecyclerView.addItemDecoration(MovieItemDecoration(this,2 , 1.1f, 10f))


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition()
        }
        val movieId = intent.getIntExtra("movie_id", -1)
        if (movieId == -1) {
            onException()
        } else {
            movieDetailDisposable = movieApi.getMovieDetails(Utils.getRegionCode(this), movieId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                bind(it)
                            },
                            {
                                it.printStackTrace()
                                onException()
                            }
                    )
        }
    }

    private fun bind(movieDetail: MovieDetail) {
        Picasso
                .get()
                .load("https://image.tmdb.org/t/p/w500" + movieDetail.posterPath)
                .into(poster, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        scheduleStartPostponedImageViewTransition(poster)
                    }

                    override fun onError(e: Exception) {
                        onException()
                    }
                })
        if (movieDetail.voteAverage.toFloat() / 2 == 0f) headerVoteAverage.visibility = View.GONE else headerVoteAverage.rating = movieDetail.voteAverage.toFloat() / 2
        toolbar.title = movieDetail.title
        toolbar.subtitle = movieDetail.tagline

        //주요정보
        infoSection.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            intent.putExtra("movie_id", movieDetail.id)
            startActivity(intent)
        }
        if (movieDetail.voteCount == 0) {
            voteCount.visibility = View.GONE
            voteAverageLabel.text = "평가되지 않음"
            voteAverage.visibility = View.GONE
            voteAverageText.visibility = View.GONE
        } else {
            voteCount.text = movieDetail.voteCount.toString()
            voteAverageText.text = movieDetail.voteAverage.toString() + "/10"
            voteAverage.rating = movieDetail.voteAverage.toFloat()
        }
        releaseDate.text = movieDetail.releaseDate
        movieDetail.runtime?.let {
            runtime.text = movieDetail.runtime.toString() + "분"
        } ?: {
            runtime.text = "해당 정보 없음"
        }()

        //장르
        genreChipGroup.removeAllViews()
        if (movieDetail.genres.isEmpty()) {
            genreChipGroup.visibility = View.GONE
            genreEmptyView.visibility = View.VISIBLE
        } else {
            genreChipGroup.visibility = View.VISIBLE
            genreEmptyView.visibility = View.GONE

            for (genre in movieDetail.genres) {
                val newChip = Chip(this)
                newChip.text = "#" + genre.name
                newChip.setOnClickListener {
                    val intent = Intent(this, MovieListActivity::class.java)
                    intent.putExtra("keyword", genre)
                    startActivity(intent)
                }
                genreChipGroup.addView(newChip)
            }
        }

        //제작사
        companyLayoutManager = LinearLayoutManager(this)
        companyLayoutManager!!.orientation = RecyclerView.HORIZONTAL
        companyRecyclerView.layoutManager = companyLayoutManager
        companyListAdapter = CompanyListAdapter({ _, position ->
            val intent = Intent(this, MovieListActivity::class.java)
            intent.putExtra("keyword", companyListAdapter?.getItem(position))
            startActivity(intent)

        }, object : OnEmptyListener {
            override fun onEmpty() {
                companyRecyclerView.visibility = View.GONE
                companyEmptyView.visibility = View.VISIBLE
            }

            override fun onNotEmpty() {
                companyRecyclerView.visibility = View.VISIBLE
                companyEmptyView.visibility = View.GONE
            }
        })
        companyListAdapter?.list?.addAll(movieDetail.productionCompanies.map {
            Company(
                    id = it.id,
                    name = it.name,
                    logo_path = it.logoPath
            )
        })
        companyRecyclerView.adapter = companyListAdapter

        //공급사
        watchProviderLayoutManager = LinearLayoutManager(this)
        watchProviderRecyclerView.layoutManager = watchProviderLayoutManager
        watchProviderListAdapter = WatchProviderListAdapter()
        watchProviderRecyclerView.adapter = watchProviderListAdapter
        watchProviderDisposable = movieApi.getWatchProvider(Utils.getRegionCode(this), movieDetail.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            result.rent?.let {
                                watchProviderEmptyView.visibility = View.GONE
                                watchProviderRecyclerView.visibility = View.VISIBLE
                                watchProviderListAdapter?.list?.addAll(it)
                                watchProviderListAdapter?.notifyDataSetChanged()

                            } ?: {
                                watchProviderEmptyView.visibility = View.VISIBLE
                                watchProviderRecyclerView.visibility = View.GONE
                            }()
                        },
                        {
                            it.printStackTrace()
                            watchProviderEmptyView.visibility = View.VISIBLE
                            watchProviderRecyclerView.visibility = View.GONE
                        }
                )

        //예고편
        if (movieDetail.videos.results.isEmpty()) {
            videoThumbnailAdapter!!.onEmpty()
        } else {
            videoThumbnailAdapter!!.setList(movieDetail.videos.results)
            videoThumbnailAdapter!!.notifyDataSetChanged()
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date = sdf.parse(movieDetail.releaseDate)
        date?.let { _date ->
            val releaseDate: Calendar = GregorianCalendar()
            releaseDate.time = _date
            val day = Utils.calDDay(releaseDate)
            if (day <= 0) {
                subscribeReleaseAlarmButton.visibility = View.GONE
            } else {
                subscribeReleaseAlarmButton.text = "DAY-$day"
                subscribeReleaseAlarmButton.setOnClickListener {
                    val intent = Intent(this, AlarmSettingActivity::class.java)
                    intent.putExtra("movie_info", movieDetail.toMovie())
                    startActivity(intent)
                }
            }
        } ?: {
            subscribeReleaseAlarmButton.visibility = View.GONE
        }()

        //이미지
        if (movieDetail.images.posters.isEmpty() && movieDetail.images.backdrops.isEmpty()) {
            previewImageAdapter!!.onEmpty()
        } else {
            val previewImages = ArrayList<String>()
            for (backdrop in movieDetail.images.backdrops) previewImages.add(backdrop.filePath)
            for (poster in movieDetail.images.posters) previewImages.add(poster.filePath)
            previewImageAdapter!!.setList(previewImages)
            previewImageAdapter!!.notifyDataSetChanged()
        }

        //출연진&제작진
        creditDisposable = movieApi.getCredits(movieDetail.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            if (result.cast.isEmpty()){
                                castEmptyView.visibility = View.VISIBLE
                                castRecyclerView.visibility = View.GONE
                            } else {
                                castEmptyView.visibility = View.GONE
                                castRecyclerView.visibility = View.VISIBLE
                                castListAdapter?.list?.addAll(result.cast)
                                castListAdapter?.notifyDataSetChanged()
                            }

                            if (result.crew.isEmpty()){
                                crewEmptyView.visibility = View.VISIBLE
                                crewRecyclerView.visibility = View.GONE
                            } else {
                                crewEmptyView.visibility = View.GONE
                                crewRecyclerView.visibility = View.VISIBLE
                                crewListAdapter?.list?.addAll(result.crew)
                                crewListAdapter?.notifyDataSetChanged()
                            }

                        },
                        {
                            it.printStackTrace()
                            crewEmptyView.visibility = View.VISIBLE
                            crewRecyclerView.visibility = View.GONE
                            castEmptyView.visibility = View.VISIBLE
                            castRecyclerView.visibility = View.GONE
                        })

        //유사영화
        similarMovieDisposable = movieApi.getSimilarMovie(movieDetail.id, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result->
                            if (result.totalResults == 0) {
                                similarMovieListAdapter?.onEmpty()
                            } else {
                                Log.d("TAG", "무비 온 낫 엠티")
                                similarMovieListAdapter?.onNotEmpty()
                                if (result.totalResults <= 4) {
                                    similarMovieListAdapter?.list?.addAll(result.results)
                                } else {
                                    val movieCollapseArrayList = ArrayList(result.results.subList(0, 4))
                                    similarMovieListAdapter?.list?.addAll(movieCollapseArrayList)
                                    similarMovieLoadMoreButton.visibility = View.VISIBLE
                                    similarMovieLoadMoreButton.setOnClickListener {
                                        val intent = Intent(this, MovieListActivity::class.java)
                                        intent.putExtra("keyword", movieDetail)
                                        startActivity(intent)
                                    }
                                }
                                similarMovieListAdapter?.notifyDataSetChanged()
                            }

                        },
                        {
                            it.printStackTrace()
                            similarMovieListAdapter?.onEmpty()
                        }
                )

    }

    private fun onException() {
        scheduleStartPostponedImageViewTransition(poster)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFinishAfterTransition()
    }

    private fun scheduleStartPostponedImageViewTransition(imageView: ImageView?) {
        imageView!!.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                imageView.viewTreeObserver.removeOnPreDrawListener(this)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startPostponedEnterTransition()
                }
                return true
            }
        })
    }
}