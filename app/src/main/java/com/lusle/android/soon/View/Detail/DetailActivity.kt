package com.lusle.android.soon.View.Detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.chip.ChipGroup
import com.lusle.android.soon.Adapter.CompanyListAdapter
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Adapter.PreviewImageAdapter
import com.lusle.android.soon.Adapter.VideoThumbnailAdapter
import com.lusle.android.soon.Model.API.APIClient
import com.lusle.android.soon.Model.API.APIInterface
import com.lusle.android.soon.Model.Schema.MovieDetail
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.SubtitleCollapsingToolbarLayout.SubtitleCollapsingToolbarLayout
import com.lusle.android.soon.Util.Utils
import com.lusle.android.soon.View.BaseActivity
import com.lusle.android.soon.View.YoutubePlayer.YoutubePlayerActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*

class DetailActivity : BaseActivity() {
    private lateinit var poster: ImageView
    private lateinit var toolbar: SubtitleCollapsingToolbarLayout
    private lateinit var scrollDownArrow: LottieAnimationView

    //주요정보
    private lateinit var headerVoteAverage: RatingBar
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
    //TODO Add Adapter & LayoutManager

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
    //TODO Add Adapter & LayoutManager

    //제작진
    private lateinit var crewRecyclerView: RecyclerView
    private lateinit var crewEmptyView: TextView
    //TODO Add Adapter & LayoutManager

    //유사영화
    private lateinit var similarMovieRecyclerView: RecyclerView
    private lateinit var similarMovieEmptyView: TextView
    private lateinit var similarMovieLoadMoreButton: RelativeLayout
    //TODO Add Adapter & LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        poster = findViewById(R.id.poster)
        toolbar = findViewById(R.id.collapsing_toolbar_layout)
        headerVoteAverage = findViewById(R.id.vote_average_header)
        scrollDownArrow = findViewById(R.id.scroll_down_arrow)
        scrollDownArrow.scale = 0.06f

        //주요정보
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
        companyListAdapter = CompanyListAdapter(object : OnItemClickListener {
            override fun onItemClick(view: View?, position: Int) {

            }
        }, object : OnEmptyListener {
            override fun onEmpty() {

            }

            override fun onNotEmpty() {

            }
        })
        companyLayoutManager = LinearLayoutManager(this)

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
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view!!, ViewCompat.getTransitionName(view)!!)
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
        previewImageAdapter!!.setOnItemClickListener { view: View?, position: Int -> }
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition()
        }
        val id = intent.getIntExtra("movie_id", -1)
        APIClient.getClient().create(APIInterface::class.java).getMovieDetails(Utils.getRegionCode(this), id).enqueue(object : Callback<MovieDetail?> {
            override fun onResponse(call: Call<MovieDetail?>, response: Response<MovieDetail?>) {
                val data = response.body()
                Picasso
                        .get()
                        .load("https://image.tmdb.org/t/p/w500" + data!!.posterPath)
                        .into(poster, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                scheduleStartPostponedImageViewTransition(poster)
                            }

                            override fun onError(e: Exception) {
                                onException()
                            }
                        })
                if (data.voteAverage.toFloat() / 2 == 0f) headerVoteAverage.visibility = View.GONE else headerVoteAverage.rating = data.voteAverage.toFloat() / 2
                toolbar.title = data.title
                toolbar.subtitle = data.tagline
                if (data.voteCount > 0) {
                    voteCount.text = data.voteCount.toString() + ""
                    voteAverageText.text = data.voteAverage.toString() + "/10"
                } else if (data.voteCount == 0) {
                    voteCount.visibility = View.GONE
                    voteAverageLabel.text = "평가되지 않음"
                    voteAverage.visibility = View.GONE
                    voteAverageText.visibility = View.GONE
                }
                voteAverage.rating = data.voteAverage.toFloat()
                val genreList = ArrayList<String?>()
                for (genre in data.genres) genreList.add(genre.name)
                //genre.setText(TextUtils.join(",", genreList))
                releaseDate.text = data.releaseDate
                if (data.runtime != null) runtime.text = data.runtime.toString() + "분" else runtime.text = "해당 정보 없음"
                //popularity.setText(data.popularity.toString() + "")
                if (data.videos.results.isEmpty()) {
                    videoThumbnailAdapter!!.onEmpty()
                } else {
                    videoThumbnailAdapter!!.setList(data.videos.results)
                    videoThumbnailAdapter!!.notifyDataSetChanged()
                }
                //if (data.overview.isNotEmpty()) overview.setText(data.overview) else overview.setText("해당 정보 없음")
                if (data.images.posters.isEmpty() && data.images.backdrops.isEmpty()) {
                    previewImageAdapter!!.onEmpty()
                } else {
                    val previewImages = ArrayList<String>()
                    for (backdrop in data.images.backdrops) previewImages.add(backdrop.filePath)
                    for (poster in data.images.posters) previewImages.add(poster.filePath)
                    previewImageAdapter!!.setList(previewImages)
                    previewImageAdapter!!.notifyDataSetChanged()
                }
                //revenue.setText(if (data.revenue == 0) "해당 정보 없음" else "$" + DecimalFormat("###,###").format(data.revenue))
                //budget.setText(if (data.budget == 0) "해당 정보 없음" else "$" + DecimalFormat("###,###").format(data.budget))
                //originalLang.setText(data.originalLanguage)
                //originalTitle.setText(data.originalTitle)
            }

            override fun onFailure(call: Call<MovieDetail?>, t: Throwable) {
                onException()
            }
        })
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