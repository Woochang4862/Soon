package com.lusle.android.soon.View.Main.Company

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lusle.android.soon.Adapter.Decoration.MovieItemDecoration
import com.lusle.android.soon.Adapter.CompanyListAdapter
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Adapter.MovieListAdapter
import com.lusle.android.soon.Model.API.MovieApi
import com.lusle.android.soon.Model.Source.FavoriteCompanyDataLocalSource
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.Util
import com.lusle.android.soon.View.Detail.DetailActivity
import com.lusle.android.soon.View.Dialog.MovieProgressDialog
import com.lusle.android.soon.View.Main.Company.Presenter.CompanyContract
import com.lusle.android.soon.View.Main.Company.Presenter.CompanyPresenter
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_company.*

class CompanyFragment : Fragment(), CompanyContract.View {
    private val spanCount: Int = 2
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var page: Int = 1
    private val movieApi: MovieApi = MovieApi.create()
    private lateinit var presenter: CompanyPresenter
    private lateinit var dialog: MovieProgressDialog
    private lateinit var companyAdapter: CompanyListAdapter
    private lateinit var movieAdapter: MovieListAdapter

    companion object {
        val PAGE_SIZE: Int = 20

        @JvmStatic
        fun newInstance(): CompanyFragment {
            val args = Bundle()
            val fragment = CompanyFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_company, container, false)
        presenter = CompanyPresenter()
        presenter.attachView(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = MovieProgressDialog(requireContext())

        // 등록 제작사 설정
        // CompanyStart
        manage_btn.setOnClickListener { findNavController().navigate(R.id.action_companyFragment_to_manageCompanyFragment) }
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        company_list.layoutManager = linearLayoutManager
        companyAdapter = CompanyListAdapter({ _ , position ->
            val args = Bundle()
            args.putSerializable("keyword", companyAdapter.getItem(position))
            findNavController().navigate(R.id.action_companyFragment_to_movieListFragment, args)
        }, object : OnEmptyListener {
            override fun onEmpty() {
                setCompanyRecyclerEmpty(true)
            }

            override fun onNotEmpty() {
                setCompanyRecyclerEmpty(false)
            }
        })
        company_list.adapter = companyAdapter
        // CompanyEnd

        // 예정작 설정
        // MovieStart
        val layoutManager = GridLayoutManager(requireContext(), spanCount)
        movie_list.layoutManager = layoutManager
        movie_list.addItemDecoration(MovieItemDecoration(context as Activity?))
        movieAdapter = MovieListAdapter({ _, position ->
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("movie_id", movieAdapter.getItem(position)?.id)
            val poster = Pair.create(view.findViewById<View>(R.id.movie_list_recyclerview_poster), ViewCompat.getTransitionName(view.findViewById(R.id.movie_list_recyclerview_poster)))
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation((view.context as Activity), poster)
            requireContext().startActivity(intent, options.toBundle())
        }, object : OnEmptyListener {
            override fun onEmpty() {
                setMovieRecyclerEmpty(true)
            }

            override fun onNotEmpty() {
                setMovieRecyclerEmpty(false)
            }
        })
        movie_list.adapter = movieAdapter
        // MovieEnd

        // PaginationStart
        scroll_view.viewTreeObserver.addOnScrollChangedListener {
            if (scroll_view != null) {
                val lastChildView = scroll_view.getChildAt(scroll_view.childCount - 1)
                val diff = (lastChildView.bottom - (scroll_view.height + scroll_view.scrollY))
                if (diff in 0..6000 && !isLastPage && !isLoading) {
                    isLoading = true
                    movieApi.discoverMovieWithCompany(420, Util.getRegionCode(requireContext()), page).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        isLoading = false
                                        if (it.results.isEmpty()) {
                                            isLastPage = true
                                        } else {
                                            val position = movieAdapter.list.size
                                            movieAdapter.list.addAll(it.results)
                                            movieAdapter.notifyItemInserted(position)
                                            movie_list.apply {
                                                removeItemDecorationAt(0)
                                                addItemDecoration((MovieItemDecoration(context as Activity?)))
                                            }
                                            page += 1
                                        }
                                    },
                                    {
                                        it.printStackTrace()
                                        isLoading = false
                                        movieAdapter.onEmpty()
                                        isLastPage = true
                                    }
                            )
                }
            }
        }
        // PaginationEnd

        // BindStart
        FavoriteCompanyDataLocalSource.getInstance().getFavoriteCompany(context).apply {
            if (isEmpty()) {
                companyAdapter.onEmpty()
                movieAdapter.onEmpty()
            } else {
                companyAdapter.onNotEmpty()
                companyAdapter.list.addAll(this)
                companyAdapter.notifyDataSetChanged()

                val disposable = movieApi.discoverMovieWithCompany(this[0].id ,Util.getRegionCode(requireContext()), 1).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    if (it.results.isEmpty()) {
                                        movieAdapter.onEmpty()
                                        isLastPage = true
                                    } else {
                                        movieAdapter.onNotEmpty()
                                        movieAdapter.list.addAll(it.results)
                                        movieAdapter.notifyDataSetChanged()
                                        page += 1
                                    }
                                },
                                {
                                    it.printStackTrace()
                                }
                        )
            }
        }
        // BindEnd
    }

    override fun onResume() {
        super.onResume()

        // 제작사 불러오기
        companyAdapter.list.clear()
        FavoriteCompanyDataLocalSource.getInstance().getFavoriteCompany(context).apply {
            if (isEmpty()) {
                companyAdapter.onEmpty()
                movieAdapter.onEmpty()
            } else {
                companyAdapter.onNotEmpty()
                companyAdapter.list.addAll(this)
                companyAdapter.notifyDataSetChanged()

                val disposable = movieApi.discoverMovieWithCompany(this[0].id ,Util.getRegionCode(requireContext()), 1).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    movieAdapter.list.clear()
                                    if (it.results.isEmpty()) {
                                        movieAdapter.onEmpty()
                                        isLastPage = true
                                    } else {
                                        movieAdapter.onNotEmpty()
                                        movieAdapter.list.addAll(it.results)
                                        movieAdapter.notifyDataSetChanged()
                                        page += 1
                                    }
                                },
                                {
                                    it.printStackTrace()
                                }
                        )
            }
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun showErrorToast() {
        DynamicToast.makeError(requireContext(), "즐겨찾기 정보를 불러 올 수 없습니다.").show()
    }

    override fun showDialog(show: Boolean) {
        if (show) dialog.show() else dialog.dismiss()
    }

    fun setCompanyRecyclerEmpty(visibility: Boolean) {
        if (visibility) {
            manage_btn.visibility = View.GONE
            company_list.visibility = View.GONE
            company_empty_view.visibility = View.VISIBLE
        } else {
            manage_btn.visibility = View.VISIBLE
            company_list.visibility = View.VISIBLE
            company_empty_view.visibility = View.GONE
        }
    }

    fun setMovieRecyclerEmpty(visibility: Boolean) {
        // 프래그먼트 바로 전환시 에러
        if (visibility) {
            movie_list.visibility = View.GONE
            movie_empty_view.visibility = View.VISIBLE
            movie_empty_anim.visibility = View.VISIBLE
            if (!movie_empty_anim.isAnimating) movie_empty_anim.playAnimation()
        } else {
            movie_list.visibility = View.VISIBLE
            movie_empty_view.visibility = View.GONE
            movie_empty_anim.visibility = View.GONE
            if (movie_empty_anim.isAnimating) movie_empty_anim.pauseAnimation()
        }
    }
}