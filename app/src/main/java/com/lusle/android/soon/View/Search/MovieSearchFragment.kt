package com.lusle.android.soon.View.Search

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.SearchMoviePagedListAdapter
import com.lusle.android.soon.Model.API.APIClient
import com.lusle.android.soon.Model.API.APIInterface
import com.lusle.android.soon.Model.API.MovieApi
import com.lusle.android.soon.Model.Schema.GenreResult
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.Model.Source.SearchMoviePageKeyDataSource
import com.lusle.android.soon.Model.Source.TMMPageKeyDataSource
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.Utils
import com.lusle.android.soon.View.Alarm.AlarmSettingActivity
import com.lusle.android.soon.View.Alarm.AlarmSettingFragment
import com.lusle.android.soon.View.Detail.DetailActivity
import com.lusle.android.soon.View.Dialog.MovieProgressDialog
import com.lusle.android.soon.View.Search.SearchActivity.OnQueryReceivedListener
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import io.reactivex.disposables.Disposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MovieSearchFragment : Fragment(), OnQueryReceivedListener {
    private val movieApi: MovieApi = MovieApi.create()
    private val PAGE_SIZE: Int = 20
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyViewGroup: FrameLayout
    private lateinit var emptyAnim: LottieAnimationView
    private var adapter: SearchMoviePagedListAdapter? = null
    private lateinit var listDisposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as SearchActivity?)!!.addQueryReceivedListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_movie, container, false)
        recyclerView = view.findViewById(R.id.movieRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        emptyViewGroup = view.findViewById(R.id.list_empty_view)
        emptyAnim = view.findViewById(R.id.list_empty_anim)

        return view
    }

    override fun onQueryReceived(query: String) {
        val dialog = MovieProgressDialog(requireContext())
        dialog.show()
        val apiInterface = APIClient.getClient().create(APIInterface::class.java)
        apiInterface.genreList.enqueue(object : Callback<GenreResult> {
            override fun onResponse(call: Call<GenreResult>, response: Response<GenreResult>) {
                response.body()?.genres?.let { genreList ->
                    val genreMap = HashMap<Int, String>()
                    for (genre in genreList) {
                        genreMap[genre.id] = genre.name
                    }

                    val config = PagedList.Config.Builder()
                            .setInitialLoadSizeHint(PAGE_SIZE)
                            .setPageSize(PAGE_SIZE)
                            .build()

                    val factory = object : DataSource.Factory<Int, Movie>() {
                        override fun create(): DataSource<Int, Movie> = SearchMoviePageKeyDataSource(movieApi = movieApi, query = query, region = Utils.getRegionCode(requireContext()))
                    }

                    val builder = RxPagedListBuilder(factory, config)

                    adapter = SearchMoviePagedListAdapter(
                            genreMap,
                            { view: View, position: Int ->
                                val intent = Intent(context, DetailActivity::class.java)
                                intent.putExtra("movie_id", adapter?.getItem(position)?.id)
                                val poster = Pair.create(view.findViewById<View>(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(view.findViewById(R.id.movie_list_recyclerview_poster)))
                                val title = Pair.create(view.findViewById<View>(R.id.movie_list_recyclerView_title), ViewCompat.getTransitionName(view.findViewById(R.id.movie_list_recyclerView_title)))
                                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, poster, title)
                                startActivity(intent, options.toBundle())
                            },
                            object : OnEmptyListener {
                                override fun onEmpty() {
                                    recyclerView.visibility = View.GONE
                                    emptyViewGroup.visibility = View.VISIBLE
                                    emptyAnim.playAnimation()
                                }

                                override fun onNotEmpty() {
                                    recyclerView.visibility = View.VISIBLE
                                    emptyViewGroup.visibility = View.GONE
                                    if (emptyAnim.isAnimating) emptyAnim.pauseAnimation()
                                }
                            },
                            { movie: Movie? ->
                                val intent = Intent(context, AlarmSettingActivity::class.java)
                                intent.putExtra("movie_info", movie)
                                startActivity(intent)
                            }
                    )
                    recyclerView.adapter = adapter

                    listDisposable = builder.buildObservable()
                            .subscribe(
                                    {
                                        adapter?.onNotEmpty()
                                        adapter?.submitList(it)
                                        dialog.dismiss()
                                        //TODO Shimmer Effect
                                    },
                                    { t: Throwable ->
                                        t.printStackTrace()
                                        adapter?.onEmpty()
                                    }
                            )
                } ?: {
                    onFailure(call, Throwable())
                }()
            }

            override fun onFailure(call: Call<GenreResult>, t: Throwable) {
                adapter!!.onEmpty()
                dialog.dismiss()
                DynamicToast.makeError(context!!, getString(R.string.server_error_msg)).show()
            }
        })
    }
}