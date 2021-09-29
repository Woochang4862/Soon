package com.lusle.android.soon.View.MovieList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.facebook.shimmer.ShimmerFrameLayout
import com.lusle.android.soon.Adapter.Decoration.MovieItemDecoration
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.MoviePagedListAdapter
import com.lusle.android.soon.Model.API.MovieApi
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.Model.Schema.Genre
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.Model.Schema.MovieDetail
import com.lusle.android.soon.Model.Source.CompanyPageKeyDataSource
import com.lusle.android.soon.Model.Source.GenrePageKeyDataSource
import com.lusle.android.soon.Model.Source.SimilarMoviePageKeyDataSource
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.Utils
import com.lusle.android.soon.View.Detail.DetailActivity
import com.lusle.android.soon.View.Main.Company.CompanyFragment.Companion.PAGE_SIZE

class MovieListFragment : Fragment() {
    private lateinit var config: PagedList.Config
    private lateinit var factory: DataSource.Factory<Int, Movie>
    private val movieApi: MovieApi = MovieApi.create()
    private var genre: Genre? = null
    private var company: Company? = null
    private var movieDetail: MovieDetail? = null

    private lateinit var keyword: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyViewGroup: RelativeLayout
    private lateinit var emptyAnim: LottieAnimationView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    private lateinit var adapter: MoviePagedListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            shimmerFrameLayout = findViewById(R.id.shimmer)
            keyword = findViewById(R.id.keyword)
            recyclerView = findViewById(R.id.movie_list_recyclerView)
            emptyViewGroup = findViewById(R.id.list_empty_view)
            emptyAnim = findViewById(R.id.list_empty_anim)
        }

        when (val obj: Any? = requireArguments().getSerializable("keyword")) {
            is Genre -> {
                genre = obj
                keyword.text = genre!!.name

                factory = object : DataSource.Factory<Int, Movie>() {
                    override fun create(): DataSource<Int, Movie> = GenrePageKeyDataSource(movieApi = movieApi, region = Utils.getRegionCode(requireContext()), genre!!.id)
                }
            }
            is Company -> {
                company = obj
                keyword.text = company!!.name

                factory = object : DataSource.Factory<Int, Movie>() {
                    override fun create(): DataSource<Int, Movie> = CompanyPageKeyDataSource(movieApi = movieApi, region = Utils.getRegionCode(requireContext()), company!!.id)
                }
            }
            is MovieDetail -> {
                movieDetail = obj
                keyword.text = movieDetail!!.title

                factory = object : DataSource.Factory<Int, Movie>() {
                    override fun create(): DataSource<Int, Movie> = SimilarMoviePageKeyDataSource(movieApi = movieApi, id = movieDetail!!.id)
                }
            }
        }
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.addItemDecoration(MovieItemDecoration(requireActivity()))

        config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build()
        val builder = RxPagedListBuilder(factory, config)
        adapter = MoviePagedListAdapter({ _, position ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("movie_id", adapter.getItem(position)!!.id)
            val poster: Pair<View, String> = Pair.create(view.findViewById(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(view.findViewById(R.id.movie_list_recyclerview_poster)))
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), poster)
            startActivity(intent, options.toBundle())
        }, object : OnEmptyListener {
            override fun onEmpty() {
                recyclerView.visibility = View.GONE
                emptyViewGroup.visibility = View.VISIBLE
                if (!emptyAnim.isAnimating) emptyAnim.playAnimation()
            }

            override fun onNotEmpty() {
                recyclerView.visibility = View.VISIBLE
                emptyViewGroup.visibility = View.GONE
                if (emptyAnim.isAnimating) emptyAnim.pauseAnimation()
            }
        })
        recyclerView.adapter = adapter
        val disposable = builder.buildObservable()
                .subscribe(
                        { result ->
                            shimmerFrameLayout.stopShimmer()
                            shimmerFrameLayout.visibility = View.GONE
                            adapter.let {
                                if (result.isEmpty()){
                                    it.onEmpty()
                                }else {
                                    it.onNotEmpty()
                                    it.submitList(result)
                                }
                            }
                        },
                        { t: Throwable ->
                            t.printStackTrace()
                            adapter.onEmpty()
                        }
                )
    }
}