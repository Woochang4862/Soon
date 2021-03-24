package com.lusle.android.soon.View.Main.ThisMonthMovie

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Adapter.MoviePagedListAdapter
import com.lusle.android.soon.Model.API.MovieApi
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.Model.Source.MovieDataRemoteSource
import com.lusle.android.soon.Model.Source.TMMPageKeyDataSource
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.Util
import com.lusle.android.soon.View.Detail.DetailActivity
import com.lusle.android.soon.View.Dialog.MovieProgressDialog
import com.lusle.android.soon.View.Main.ThisMonthMovie.Presenter.ThisMonthMovieContract
import com.lusle.android.soon.View.Main.ThisMonthMovie.Presenter.ThisMonthMoviePresenter
import com.pranavpandey.android.dynamic.toasts.DynamicToast

class ThisMonthMovieFragment : Fragment(), ThisMonthMovieContract.View {
    private val movieApi = MovieApi.create()
    private val PAGE_SIZE: Int = 20
    private lateinit var emptyView: FrameLayout
    private lateinit var emptyAnim: LottieAnimationView
    private lateinit var recyclerView: RecyclerView
    private var adapter: MoviePagedListAdapter? = null
    private var layoutManager: GridLayoutManager? = null
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var presenter: ThisMonthMoviePresenter
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_this_month_movie, container, false)
        presenter = ThisMonthMoviePresenter()
        //presenter.attachView(this)
        //presenter.setMovieModel(MovieDataRemoteSource.getInstance())
        shimmerFrameLayout = view.findViewById(R.id.shimmer)
        emptyView = view.findViewById(R.id.list_empty_view)
        emptyAnim = view.findViewById(R.id.list_empty_anim)
        recyclerView = view.findViewById(R.id.movie_list_recyclerView)

        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build()

        val factory = object : DataSource.Factory<Int, Movie>() {
            override fun create(): DataSource<Int, Movie> = TMMPageKeyDataSource(movieApi = movieApi, region = Util.getRegionCode(requireContext()))
        }

        val builder = RxPagedListBuilder(factory, config)

        adapter = MoviePagedListAdapter({ _, position ->
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("movie_id", adapter?.getItem(position)?.id)
            val poster = Pair.create(view.findViewById<View>(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(view.findViewById(R.id.movie_list_recyclerview_poster)))
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation((view.context as Activity), poster)
            startActivity(intent, options.toBundle())
        }, object : OnEmptyListener {
            override fun onEmpty() {
                setRecyclerEmpty(true)
            }

            override fun onNotEmpty() {
                setRecyclerEmpty(false)
            }
        })
        recyclerView.adapter = adapter
        builder.buildObservable()
                .subscribe {
                    adapter!!.submitList(it)
                    shimmerFrameLayout.stopShimmer()
                    shimmerFrameLayout.visibility = View.GONE
                }
        layoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(MovieItemDecoration(activity))
        //presenter.setOnEmptyListener()
        //presenter.setOnLoadMoreListener()
        return view
    }

    override fun showErrorToast() {
        DynamicToast.makeError(requireContext(), getString(R.string.server_error_msg)).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showDialog(show: Boolean) {

    }

    override fun runRecyclerViewAnimation() {
        Util.runLayoutAnimation(recyclerView)
    }

    override fun getContext(): Context? {
        return super.getContext()
    }

    override fun setRecyclerEmpty(empty: Boolean) {
        if (empty) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            emptyAnim.visibility = View.VISIBLE
            if (!emptyAnim.isAnimating) emptyAnim.playAnimation()
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            emptyAnim.visibility = View.GONE
            if (emptyAnim.isAnimating) emptyAnim.pauseAnimation()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): ThisMonthMovieFragment {
            val args = Bundle()
            val fragment = ThisMonthMovieFragment()
            fragment.arguments = args
            return fragment
        }
    }
}