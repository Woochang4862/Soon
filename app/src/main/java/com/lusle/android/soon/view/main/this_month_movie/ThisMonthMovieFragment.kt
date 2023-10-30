package com.lusle.android.soon.view.main.this_month_movie

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.lusle.android.soon.model.source.RegionCodeRepository
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.decoration.MovieItemDecoration
import com.lusle.android.soon.adapter.listener.OnEmptyListener
import com.lusle.android.soon.adapter.MoviePagedListAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ThisMonthMovieFragment : Fragment() {

    private lateinit var emptyView: RelativeLayout
    private lateinit var emptyAnim: LottieAnimationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: MoviePagedListAdapter
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var errorSnackBar: Snackbar

    private val viewModel by viewModels<ThisMonthMovieViewModel> {
        ThisMonthMovieViewModelFactory(
            RegionCodeRepository(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_this_month_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorSnackBar = Snackbar.make(
            requireView(),
            getString(R.string.failed_load_movie_msg),
            Snackbar.LENGTH_SHORT
        )
            .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
            .setGestureInsetBottomIgnored(true)

        shimmerFrameLayout = view.findViewById(R.id.shimmer)
        emptyView = view.findViewById(R.id.list_empty_view)
        emptyAnim = view.findViewById(R.id.list_empty_anim)
        recyclerView = view.findViewById(R.id.movie_list_recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.movie_list_swipe_refresh)

        adapter = MoviePagedListAdapter({ _, _ ->

        }, object : OnEmptyListener {
            override fun onEmpty() {
                setRecyclerEmpty(true)
            }

            override fun onNotEmpty() {
                setRecyclerEmpty(false)
            }
        })
        adapter.addLoadStateListener { loadState ->
            adapter.apply {
                onNotEmpty()
                Log.d(TAG, "onLoadStateListener: $loadState")
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && itemCount < 1) {
                    Log.d(TAG, "비어있음")
                    onEmpty()
                }
            }
        }
        recyclerView.adapter = adapter

        layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0) return 2
                return 1
            }
        }
        recyclerView.layoutManager = layoutManager
        activity?.let { MovieItemDecoration(it) }?.let {
            if (recyclerView.itemDecorationCount == 0)
                recyclerView.addItemDecoration(it)
        }

        swipeRefreshLayout.setOnRefreshListener {
            load()
        }

        load()
    }

    private fun load() {
        lifecycleScope.launch {
            loading(true)
            try {
                viewModel.flow.collectLatest { pagingData ->
                    loading(false)
                    adapter.submitData(pagingData)
                }
            } catch (e:Exception){
                e.printStackTrace()
                adapter.onEmpty()
                showErrorSnackBar()
            }
        }
    }

    private fun showErrorSnackBar(){
        errorSnackBar.show()
    }

    fun setRecyclerEmpty(empty: Boolean) {
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

    override fun onDetach() {
        errorSnackBar.dismiss()
        super.onDetach()
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    companion object {
        val TAG: String = ThisMonthMovieFragment::class.java.simpleName
    }

}