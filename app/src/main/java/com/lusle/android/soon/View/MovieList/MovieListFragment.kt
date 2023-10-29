package com.lusle.android.soon.View.MovieList

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
import com.airbnb.lottie.LottieAnimationView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.lusle.android.soon.Model.Source.FavoriteCompanyRepository
import com.lusle.android.soon.Model.Source.RegionCodeRepository
import com.lusle.android.soon.R
import com.lusle.android.soon.View.Dialog.MovieProgressDialog
import com.lusle.android.soon.adapter.Decoration.MovieItemDecoration
import com.lusle.android.soon.adapter.Listener.OnCompanyBookMarkButtonClickListener
import com.lusle.android.soon.adapter.Listener.OnEmptyListener
import com.lusle.android.soon.adapter.MoviePagedListAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MovieListFragment : Fragment() {

    private lateinit var errorSnackBar: Snackbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyViewGroup: RelativeLayout
    private lateinit var emptyAnim: LottieAnimationView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var movieProgressDialog: MovieProgressDialog

    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapter: MoviePagedListAdapter

    private val viewModel by viewModels<MovieListViewModel> { MovieListViewModelFactory(
        RegionCodeRepository(requireContext()), FavoriteCompanyRepository(requireContext())
    ) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            errorSnackBar = Snackbar.make(requireView(), getString(R.string.failed_load_movie_msg), Snackbar.LENGTH_SHORT)
                .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
                .setGestureInsetBottomIgnored(true)
                .setAction("재시도"){
                    load()
                }
            shimmerFrameLayout = findViewById(R.id.shimmer)
            recyclerView = findViewById(R.id.movie_list_recyclerView)
            emptyViewGroup = findViewById(R.id.list_empty_view)
            emptyAnim = findViewById(R.id.list_empty_anim)
        }

        movieProgressDialog = MovieProgressDialog(requireContext())

        val obj: Any? = requireArguments().getSerializable("keyword")
        viewModel.init(obj)

        viewModel.movieLoadState.observe(viewLifecycleOwner) {
            movieLoading(it)
        }

        viewModel.subscribeLoadState.observe(viewLifecycleOwner) {
            saveCompanyLoading(it)
        }

        layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0) return 2
                return 1
            }
        }
        recyclerView.layoutManager = layoutManager
        if (recyclerView.itemDecorationCount == 0)
            recyclerView.addItemDecoration(MovieItemDecoration(requireActivity()))

        adapter = MoviePagedListAdapter({ _, _ -> }, object : OnEmptyListener {
            override fun onEmpty() {
                setRecyclerEmpty(true)
            }

            override fun onNotEmpty() {
                setRecyclerEmpty(false)
            }
        },object : OnCompanyBookMarkButtonClickListener {
            override fun onCompanyBookMarkButtonClicked(view: View, isChecked: Boolean) {
                lifecycleScope.launch {
                    if (isChecked) {
                        viewModel.addCompany()
                    } else {
                        viewModel.removeCompany()
                    }
                }
            }

        })
        adapter.addLoadStateListener { loadState ->
            adapter.onNotEmpty()
            adapter.let {
                Log.d(TAG, "onLoadStateListener: $loadState")
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && it.itemCount < 1) {
                    Log.d(TAG, "비어있음")
                    it.onEmpty()
                }
            }
        }
        recyclerView.adapter = adapter

        load()
    }

    private fun load() {
        lifecycleScope.launch {
            viewModel.movieLoadState.value = true
            try {
                viewModel.flow!!.collectLatest { pagingData ->
                    viewModel.movieLoadState.value = false
                    shimmerFrameLayout.visibility = View.GONE
                    adapter.submitData(pagingData)
                }
            } catch (e:Exception){
                e.printStackTrace()
                adapter.onEmpty()
                showErrorSnackBar()
            }
        }
    }

    override fun onDetach() {
        errorSnackBar.dismiss()
        super.onDetach()
    }

    private fun showErrorSnackBar() {
        errorSnackBar.show()
    }

    private fun movieLoading(isLoading:Boolean){
        if(isLoading){
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun saveCompanyLoading(isLoading:Boolean){
        if(isLoading){
            movieProgressDialog.show()
        } else {
            movieProgressDialog.hide()
        }
    }

    fun setRecyclerEmpty(empty: Boolean) {
        if (empty) {
            recyclerView.visibility = View.GONE
            emptyViewGroup.visibility = View.VISIBLE
            if (!emptyAnim.isAnimating) emptyAnim.playAnimation()
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyViewGroup.visibility = View.GONE
            if (emptyAnim.isAnimating) emptyAnim.pauseAnimation()
        }
    }

    companion object {
        private const val TAG = "MovieListFragment"
    }
}