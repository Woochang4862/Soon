package com.lusle.android.soon.Adapter.Holder

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Adapter.Decoration.MovieItemDecoration
import com.lusle.android.soon.Adapter.CompanyListAdapter
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.MoviePagedListAdapter
import com.lusle.android.soon.Model.API.MovieApi
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.Model.Schema.Section
import com.lusle.android.soon.Model.Schema.Section.Companion.VIEW_COMPANY
import com.lusle.android.soon.Model.Schema.Section.Companion.VIEW_MOVIE
import com.lusle.android.soon.Model.Source.TMMPageKeyDataSource
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.Util
import com.lusle.android.soon.View.Detail.DetailActivity
import com.lusle.android.soon.View.Main.Company.ManageCompanyFragment
import com.lusle.android.soon.View.Main.Company.CompanyFragment.Companion.PAGE_SIZE
import com.lusle.android.soon.View.MovieList.MovieListFragment

class SectionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context = itemView.context

    private val emptyView: FrameLayout = itemView.findViewById(R.id.empty_view)
    private val emptyAnim: LottieAnimationView = itemView.findViewById(R.id.empty_anim)
    private val content_title: TextView = itemView.findViewById(R.id.content_title)
    private val content_list: RecyclerView = itemView.findViewById(R.id.content_list)
    private val manage_btn: TextView = itemView.findViewById(R.id.manage_btn)

    private lateinit var movieAdapter: MoviePagedListAdapter
    private lateinit var companyAdapter: CompanyListAdapter

    fun bind(section: Section) {
        content_title.text = section.title
        if (section.viewType == VIEW_MOVIE) {
            manage_btn.visibility = View.GONE
            //val gridLayoutManager = GridLayoutManager(context, 2)
            val layoutManager = GridLayoutManager(itemView.context, 2)
            content_list.layoutManager = layoutManager
            content_list.addItemDecoration(MovieItemDecoration(context as Activity?))
            val config = PagedList.Config.Builder()
                    .setInitialLoadSizeHint(PAGE_SIZE)
                    .setPageSize(PAGE_SIZE)
                    .build()

            val factory = object : DataSource.Factory<Int, Movie>() {
                override fun create(): DataSource<Int, Movie> {
                    val movieApi = MovieApi.create()
                    return TMMPageKeyDataSource(movieApi = movieApi, region = Util.getRegionCode(context))
                }
            }

            val builder = RxPagedListBuilder(factory, config)
            movieAdapter = MoviePagedListAdapter({ view, position ->
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("movie_id", movieAdapter.getItem(position)?.id)
                val poster = Pair.create(view.findViewById<View>(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(view.findViewById(R.id.movie_list_recyclerview_poster)))
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation((view.context as Activity), poster)
                context.startActivity(intent, options.toBundle())
            }, object : OnEmptyListener {
                override fun onEmpty() {
                    setMovieRecyclerEmpty(true)
                }

                override fun onNotEmpty() {
                    setMovieRecyclerEmpty(false)
                }
            })
            content_list.adapter = movieAdapter
            builder.buildObservable()
                    .subscribe {
                        movieAdapter.submitList(it)
                    }
        }
        if (section.viewType == VIEW_COMPANY) {
            manage_btn.visibility = View.VISIBLE
            manage_btn.setOnClickListener { context.startActivity(Intent(context, ManageCompanyFragment::class.java)) }
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = RecyclerView.HORIZONTAL
            content_list.layoutManager = linearLayoutManager
            companyAdapter = CompanyListAdapter({ view, position ->
                val intent = Intent(view.context, MovieListFragment::class.java)
                intent.putExtra("keyword", companyAdapter.getItem(position))
                view.context.startActivity(intent)
            }, object : OnEmptyListener {
                override fun onEmpty() {
                    setCompanyRecyclerEmpty(true)
                }

                override fun onNotEmpty() {
                    setCompanyRecyclerEmpty(false)
                }
            })
            content_list.adapter = companyAdapter
        }
    }

    fun setCompanyRecyclerEmpty(visibility: Boolean) {
        if (visibility) {
            manage_btn.visibility = View.GONE
        } else {
            manage_btn.visibility = View.VISIBLE
        }
    }

    fun setMovieRecyclerEmpty(visibility: Boolean) {
        if (visibility) {
            content_list.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            emptyAnim.visibility = View.VISIBLE
            if (!emptyAnim.isAnimating) emptyAnim.playAnimation()
        } else {
            content_list.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            emptyAnim.visibility = View.GONE
            if (emptyAnim.isAnimating) emptyAnim.pauseAnimation()
        }
    }
}
