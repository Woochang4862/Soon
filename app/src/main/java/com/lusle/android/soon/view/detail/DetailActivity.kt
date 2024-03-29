package com.lusle.android.soon.view.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.model.schema.CreditsResult
import com.lusle.android.soon.model.schema.Movie
import com.lusle.android.soon.model.schema.MovieDetail
import com.lusle.android.soon.model.schema.WatchProviderResult
import com.lusle.android.soon.model.source.RegionCodeRepository
import com.lusle.android.soon.R
import com.lusle.android.soon.util.SubtitleCollapsingToolbarLayout.SubtitleCollapsingToolbarLayout
import com.lusle.android.soon.util.Utils
import com.lusle.android.soon.view.alarm.AlarmSettingActivity
import com.lusle.android.soon.view.movie_list.MovieListActivity
import com.lusle.android.soon.view.youtube_player.YoutubePlayerActivity
import com.lusle.android.soon.adapter.CastListAdapter
import com.lusle.android.soon.adapter.CompanyListAdapter
import com.lusle.android.soon.adapter.CrewListAdapter
import com.lusle.android.soon.adapter.decoration.MovieItemDecoration
import com.lusle.android.soon.adapter.listener.OnEmptyListener
import com.lusle.android.soon.adapter.MovieListAdapter
import com.lusle.android.soon.adapter.PreviewImageAdapter
import com.lusle.android.soon.adapter.VideoThumbnailAdapter
import com.lusle.android.soon.adapter.WatchProviderSectionListAdapter
import com.lusle.android.soon.adapter.decoration.CompanyItemDecoration
import com.lusle.android.soon.view.BaseActivity
import com.skydoves.transformationlayout.TransformationCompat
import com.skydoves.transformationlayout.TransformationLayout
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale

class DetailActivity : BaseActivity() {

    private lateinit var errorSnackbar: Snackbar
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
    private lateinit var companyListAdapter: CompanyListAdapter
    private lateinit var companyLayoutManager: LinearLayoutManager

    //공급사
    private lateinit var watchProviderRecyclerView: RecyclerView
    private lateinit var watchProviderEmptyView: TextView
    private lateinit var watchProviderSectionListAdapter: WatchProviderSectionListAdapter
    private lateinit var watchProviderLayoutManager: LinearLayoutManager

    //예고편
    private lateinit var videoRecyclerView: RecyclerView
    private lateinit var videoEmptyView: TextView
    private lateinit var videoThumbnailAdapter: VideoThumbnailAdapter
    private lateinit var videoLayoutManager: LinearLayoutManager

    //이미지
    private lateinit var previewImageRecyclerView: RecyclerView
    private lateinit var previewEmptyView: TextView
    private lateinit var previewImageAdapter: PreviewImageAdapter
    private lateinit var previewImageLayoutManager: LinearLayoutManager

    //출연진
    private lateinit var castRecyclerView: RecyclerView
    private lateinit var castEmptyView: TextView
    private lateinit var castListAdapter: CastListAdapter
    private lateinit var castLayoutManager: LinearLayoutManager

    //제작진
    private lateinit var crewRecyclerView: RecyclerView
    private lateinit var crewEmptyView: TextView
    private lateinit var crewListAdapter: CrewListAdapter
    private lateinit var crewLayoutManager: LinearLayoutManager

    //유사영화
    private lateinit var similarMovieRecyclerView: RecyclerView
    private lateinit var similarMovieEmptyView: TextView
    private lateinit var similarMovieLoadMoreButton: RelativeLayout
    private lateinit var similarMovieListAdapter: MovieListAdapter
    private lateinit var similarMovieLayoutManager: LinearLayoutManager

    private val viewModel by viewModels<DetailViewModel> {
        DetailViewModelProvider(RegionCodeRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        errorSnackbar = Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.failed_load_movie_detail_msg),
            Snackbar.LENGTH_SHORT
        )
            .setAnchorView(findViewById(android.R.id.content))
            .setGestureInsetBottomIgnored(true)
            .setAction("재시도") {
                load()
            }
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
        watchProviderRecyclerView = findViewById(R.id.watch_provider_section_recyclerview)
        watchProviderEmptyView = findViewById(R.id.watch_provider_empty_view)

        //에고편
        videoRecyclerView = findViewById(R.id.video_thumbnail_recyclerView)
        videoLayoutManager = LinearLayoutManager(this)
        videoLayoutManager.orientation = RecyclerView.HORIZONTAL
        videoRecyclerView.layoutManager = videoLayoutManager
        videoThumbnailAdapter = VideoThumbnailAdapter { view: View?, position: Int ->
            val intent = Intent(this, YoutubePlayerActivity::class.java)
            intent.putExtra("VIDEO_ID", videoThumbnailAdapter.getItem(position).key)
            startActivity(intent)
        }
        videoThumbnailAdapter.setOnEmptyListener(object : OnEmptyListener {
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
        previewImageLayoutManager.orientation = RecyclerView.HORIZONTAL
        previewImageRecyclerView.layoutManager = previewImageLayoutManager
        previewImageAdapter = PreviewImageAdapter()
        previewImageAdapter.setOnItemClickListener { _: View?, _: Int -> }
        previewImageAdapter.setOnEmptyListener(object : OnEmptyListener {
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
        castLayoutManager.orientation = RecyclerView.HORIZONTAL
        castRecyclerView.layoutManager = castLayoutManager
        castListAdapter = CastListAdapter()
        castRecyclerView.adapter = castListAdapter

        //제작진
        crewRecyclerView = findViewById(R.id.crew_recyclerview)
        crewEmptyView = findViewById(R.id.crew_empty_view)
        crewLayoutManager = LinearLayoutManager(this)
        crewLayoutManager.orientation = RecyclerView.HORIZONTAL
        crewRecyclerView.layoutManager = crewLayoutManager
        crewListAdapter = CrewListAdapter()
        crewRecyclerView.adapter = crewListAdapter

        //유사영화
        similarMovieEmptyView = findViewById(R.id.similar_movie_empty_view)
        similarMovieLoadMoreButton = findViewById(R.id.similar_movie_more)
        similarMovieRecyclerView = findViewById(R.id.similar_movie_recyclerview)


        postponeEnterTransition()
        val movieId = intent.getIntExtra("movie_id", -1)
        if (movieId == -1) {
            onException()
        } else {
            viewModel.movieDetail.observe(this) {
                bind(it)
            }
            viewModel.similarMovieResult.observe(this) {
                bind(it)
            }
            viewModel.watchProviderResult.observe(this) {
                bind(it)
            }
            viewModel.creditsResult.observe(this) {
                bind(it)
            }
            viewModel.movieId.observe(this) {
                load()
            }
            viewModel.movieId.value = movieId
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                supportFinishAfterTransition()
            }
        })
    }

    private fun load() {
        lifecycleScope.launch {
            try {
                viewModel.fetch()
            } catch (e: MovieDetailNotFoundException) {
                e.printStackTrace()
                onException()
            } catch (e: CreditsNotFoundException) {
                e.printStackTrace()
                castOnEmpty(true)
                crewOnEmpty(true)
            } catch (e: WatchProviderNotFoundException) {
                e.printStackTrace()
                watchProviderOnEmpty(true)
            } catch (e: SimilarMovieNotFoundException) {
                e.printStackTrace()
                similarMovieListAdapter.onEmpty()
            }
        }
    }

    private fun bind(similarMovies: ArrayList<Movie>) {
        Log.d(TAG, "bind: called - ${similarMovies}")
        //유사영화
        similarMovieListAdapter = MovieListAdapter(
            { view, position ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("movie_id", similarMovieListAdapter.getItem(position)?.id)
                val poster = Pair.create(
                    view.findViewById<View>(R.id.movie_list_recyclerview_poster),
                    ViewCompat.getTransitionName(view.findViewById(R.id.movie_list_recyclerview_poster))
                )
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (view.context as Activity),
                    poster
                )
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

        similarMovieLayoutManager = GridLayoutManager(this, 2)
        similarMovieRecyclerView.layoutManager = similarMovieLayoutManager
        similarMovieRecyclerView.addItemDecoration(MovieItemDecoration(this, 2))

        if (similarMovies.size == 0) {
            similarMovieListAdapter.onEmpty()
        } else {
            similarMovieListAdapter.onNotEmpty()
            if (similarMovies.size <= 4) {
                similarMovieLoadMoreButton.visibility = View.GONE
                similarMovieListAdapter.list.addAll(similarMovies)
            } else {
                similarMovieListAdapter.list.addAll(similarMovies.subList(0,4))

                similarMovieLoadMoreButton.visibility = View.VISIBLE
                similarMovieLoadMoreButton.setOnClickListener {
                    val intent = Intent(this, MovieListActivity::class.java)
                    intent.putExtra("keyword", viewModel.movieDetail.value)
                    startActivity(intent)
                }
            }
            similarMovieRecyclerView.adapter = similarMovieListAdapter
        }
    }

    private fun bind(creditsResult: CreditsResult) {
        //출연진&제작진
        castOnEmpty(creditsResult.cast.isEmpty())
        if (creditsResult.cast.isNotEmpty()) {
            castListAdapter.list.addAll(creditsResult.cast)
        }

        crewOnEmpty(creditsResult.crew.isEmpty())
        if (creditsResult.crew.isNotEmpty()) {
            crewListAdapter.list.addAll(creditsResult.crew)
        }

        castListAdapter.notifyDataSetChanged()
    }

    private fun crewOnEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            crewEmptyView.visibility = View.VISIBLE
            crewRecyclerView.visibility = View.GONE
        } else {
            crewEmptyView.visibility = View.GONE
            crewRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun castOnEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            castEmptyView.visibility = View.VISIBLE
            castRecyclerView.visibility = View.GONE
        } else {
            castEmptyView.visibility = View.GONE
            castRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun bind(watchProviderResult: WatchProviderResult) {
        //공급사
        watchProviderResult.apply {
            if (rent == null && flatrate == null && buy == null) {
                watchProviderOnEmpty(true)
            } else {
                watchProviderOnEmpty(false)
                watchProviderLayoutManager = LinearLayoutManager(this@DetailActivity)
                watchProviderRecyclerView.layoutManager = watchProviderLayoutManager
                watchProviderSectionListAdapter =
                    WatchProviderSectionListAdapter(this) { _, position ->
                        watchProviderSectionListAdapter.getWatchProviderItem(position)
                    }
                watchProviderRecyclerView.adapter = watchProviderSectionListAdapter
            }
        }
    }

    private fun watchProviderOnEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            watchProviderRecyclerView.visibility = View.GONE
            watchProviderEmptyView.visibility = View.VISIBLE
        } else {
            watchProviderRecyclerView.visibility = View.VISIBLE
            watchProviderEmptyView.visibility = View.GONE
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
        if (movieDetail.voteAverage.toFloat() / 2 == 0f) headerVoteAverage.visibility =
            View.GONE else headerVoteAverage.rating = movieDetail.voteAverage.toFloat() / 2
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
        } ?: run {
            runtime.text = "해당 정보 없음"
        }

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
        companyLayoutManager.orientation = RecyclerView.HORIZONTAL
        companyRecyclerView.layoutManager = companyLayoutManager
        if (companyRecyclerView.itemDecorationCount == 0)
            companyRecyclerView.addItemDecoration(CompanyItemDecoration(this, -1, 9f, 0f))
        companyListAdapter = CompanyListAdapter({ _, position ->
            val intent = Intent(this, MovieListActivity::class.java)
            intent.putExtra("keyword", companyListAdapter.getItem(position))
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
        companyListAdapter.list.addAll(movieDetail.productionCompanies.map {
            Company(
                id = it.id,
                name = it.name,
                logo_path = it.logoPath
            )
        })
        companyRecyclerView.adapter = companyListAdapter

        //예고편
        if (movieDetail.videos.results.isEmpty()) {
            videoThumbnailAdapter.onEmpty()
        } else {
            videoThumbnailAdapter.list = movieDetail.videos.results
            videoThumbnailAdapter.notifyDataSetChanged()
        }

        // 개봉일별 알람설정 버튼
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("KR"))
        val date = sdf.parse(movieDetail.releaseDate)
        date?.let {
            val releaseDate: Calendar = GregorianCalendar()
            releaseDate.time = it
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
        } ?: run { subscribeReleaseAlarmButton.visibility = View.GONE }

        //이미지
        if (movieDetail.images.posters.isEmpty() && movieDetail.images.backdrops.isEmpty()) {
            previewImageAdapter.onEmpty()
        } else {
            val previewImages = ArrayList<String>()
            for (backdrop in movieDetail.images.backdrops) previewImages.add(backdrop.filePath)
            for (poster in movieDetail.images.posters) previewImages.add(poster.filePath)
            previewImageAdapter.setList(previewImages)
            previewImageAdapter.notifyDataSetChanged()
        }
    }

    private fun onException() {
        scheduleStartPostponedImageViewTransition(poster)
    }

    private fun scheduleStartPostponedImageViewTransition(imageView: ImageView?) {
        imageView!!.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                imageView.viewTreeObserver.removeOnPreDrawListener(this)
                startPostponedEnterTransition()
                return true
            }
        })
    }

    companion object {
        val TAG: String = DetailActivity::class.java.simpleName
        const val KEY_MOVIE_ID = "movie_id"
        fun startActivity(
            context: Context,
            transformationLayout: TransformationLayout,
            movieId: Int?
        ) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(KEY_MOVIE_ID, movieId)
            TransformationCompat.startActivity(transformationLayout, intent)
        }
    }
}