package com.lusle.android.soon.View.Main.Company

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lusle.android.soon.Model.Api.MovieApi
import com.lusle.android.soon.Model.Source.FavoriteCompanyRepository
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.CompanyListAdapter
import com.lusle.android.soon.adapter.Listener.OnEmptyListener

class CompanyFragment : Fragment() {
    private lateinit var errorSnackBar: Snackbar
    private val spanCount: Int = 2
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var page: Int = 1
    private val movieApi: MovieApi = MovieApi.create()
    private lateinit var companyAdapter: CompanyListAdapter

    //private lateinit var movieAdapter: MovieListAdapter
    //private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var companyList: RecyclerView

    //private lateinit var movieList: RecyclerView
    private lateinit var manageButton: TextView

    //private lateinit var scrollView: NestedScrollView
    private lateinit var companyEmptyView: FrameLayout
    //private lateinit var movieEmptyView: FrameLayout
    //private lateinit var movieEmptyAnim: LottieAnimationView

    private val viewModel by viewModels<CompanyViewModel> {
        CompanyViewModelFactory(
            FavoriteCompanyRepository(requireContext())
        )
    }

    companion object {
        val PAGE_SIZE: Int = 20
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_company, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorSnackBar = Snackbar.make(requireView(), "에러가 발생했습니다.", Snackbar.LENGTH_SHORT)
            .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
            .setGestureInsetBottomIgnored(true)
        //shimmerFrameLayout = view.findViewById(R.id.shimmer)

        // 등록 제작사 설정
        // CompanyStart
        companyEmptyView = view.findViewById(R.id.company_empty_view)
        manageButton = view.findViewById(R.id.manage_btn)
        manageButton.setOnClickListener { findNavController().navigate(R.id.action_companyFragment_to_manageCompanyFragment) }
        companyList = view.findViewById(R.id.company_list)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        companyList.layoutManager = linearLayoutManager
        companyAdapter = CompanyListAdapter({ _, position ->
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
        companyList.adapter = companyAdapter
        // CompanyEnd

        // 예정작 설정
        // MovieStart
        try {
            /*movieEmptyView = view.findViewById(R.id.movie_empty_view)
            movieEmptyAnim = view.findViewById(R.id.movie_empty_anim)
            movieList = view.findViewById(R.id.movie_list)
            val layoutManager = GridLayoutManager(requireContext(), spanCount)
            movieList.layoutManager = layoutManager
            movieList.addItemDecoration(MovieItemDecoration(context as Activity?))
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
            movieList.adapter = movieAdapter*/
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        // MovieEnd

        // PaginationStart
        /*scrollView = view.findViewById(R.id.scroll_view)
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val lastChildView = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = (lastChildView.bottom - (scrollView.height + scrollView.scrollY))
            if (diff in 0..6000 && !isLastPage && !isLoading) {
                try {
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
                                            movieList.apply {
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
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }*/
        // PaginationEnd

        // BindStart
        companyAdapter.list.clear()
        viewModel.favoriteCompanyLiveData.observe(
            viewLifecycleOwner
        ) { favoriteCompany ->
            favoriteCompany?.let {
                if (favoriteCompany.isEmpty()) {
                    companyAdapter.onEmpty()
                    //movieAdapter.onEmpty()
                } else {
                    companyAdapter.onNotEmpty()
                    companyAdapter.list.addAll(favoriteCompany)
                    companyAdapter.notifyDataSetChanged()
                }
            } ?: run {
                companyAdapter.onEmpty()
            }
        }

        viewModel.loadFavoriteCompany()
        /*FavoriteCompanyDataLocalSource.getInstance().getFavoriteCompany(context).apply {
            if (isEmpty()) {
                companyAdapter.onEmpty()
                //movieAdapter.onEmpty()
            } else {
                companyAdapter.onNotEmpty()
                companyAdapter.list.addAll(this)
                companyAdapter.notifyDataSetChanged()

                *//*try {
                    playShimmer(true)
                    val disposable = movieApi.discoverMovieWithCompany(this[0].id, Util.getRegionCode(requireContext()), 1).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        playShimmer(false)
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
                                        playShimmer(false)
                                        showErrorToast()
                                        it.printStackTrace()
                                    }
                            )
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }*//*
            }
        }*/
        // BindEnd
    }

    override fun onResume() {
        super.onResume()

        // 제작사 불러오기
        companyAdapter.list.clear()
        viewModel.loadFavoriteCompany()
        /*FavoriteCompanyDataLocalSource.getInstance().getFavoriteCompany(context).apply {
            if (isEmpty()) {
                companyAdapter.onEmpty()
                //movieAdapter.onEmpty()
            } else {
                companyAdapter.onNotEmpty()
                companyAdapter.list.addAll(this)
                companyAdapter.notifyDataSetChanged()

                *//*try {
                    playShimmer(true)
                    val disposable = movieApi.discoverMovieWithCompany(this[0].id, Util.getRegionCode(requireContext()), 1).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        playShimmer(false)
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
                                        playShimmer(false)
                                        showErrorToast()
                                        it.printStackTrace()
                                    }
                            )
                } catch (e:IllegalStateException){
                    e.printStackTrace()
                }*//*
            }
        }*/
    }

    fun showErrorToast() {
        errorSnackBar.show()
    }

    /*override fun playShimmer(show: Boolean) {
        if (show) {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
        } else {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
        }
    }*/

    fun setCompanyRecyclerEmpty(visibility: Boolean) {
        if (visibility) {
            manageButton.visibility = View.GONE
            companyList.visibility = View.GONE
            companyEmptyView.visibility = View.VISIBLE
        } else {
            manageButton.visibility = View.VISIBLE
            companyList.visibility = View.VISIBLE
            companyEmptyView.visibility = View.GONE
        }
    }

    /*fun setMovieRecyclerEmpty(visibility: Boolean) {
        // 프래그먼트 바로 전환시 에러
        if (visibility) {
            movieList.visibility = View.GONE
            movieEmptyView.visibility = View.VISIBLE
            movieEmptyAnim.visibility = View.VISIBLE
            if (!movieEmptyAnim.isAnimating) movieEmptyAnim.playAnimation()
        } else {
            movieList.visibility = View.VISIBLE
            movieEmptyView.visibility = View.GONE
            movieEmptyAnim.visibility = View.GONE
            if (movieEmptyAnim.isAnimating) movieEmptyAnim.pauseAnimation()
        }
    }*/
}