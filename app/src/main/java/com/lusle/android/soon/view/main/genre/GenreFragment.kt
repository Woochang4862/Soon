package com.lusle.android.soon.view.main.genre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.lusle.android.soon.model.source.RegionCodeRepository
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.decoration.GenreItemDecoration
import com.lusle.android.soon.adapter.GenreListAdapter
import com.lusle.android.soon.adapter.listener.OnEmptyListener
import kotlinx.coroutines.launch

class GenreFragment : Fragment() {
    private lateinit var errorSnackBar: Snackbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: RelativeLayout
    private lateinit var emptyAnim: LottieAnimationView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: GenreListAdapter

    private val viewModel by viewModels<GenreViewModel> {
        GenreViewModelFactory(
            RegionCodeRepository(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_genre, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorSnackBar = Snackbar.make(
            requireView(),
            getString(R.string.failed_load_genre_msg),
            Snackbar.LENGTH_SHORT
        )
            .setAnchorView(requireActivity().findViewById(R.id.floatingActionButton))
            .setGestureInsetBottomIgnored(true)
        view.apply {
            shimmerFrameLayout = findViewById(R.id.shimmer)
            recyclerView = findViewById(R.id.fragment_genre_genre_list)
            emptyView = findViewById(R.id.list_empty_view)
            emptyAnim = findViewById(R.id.list_empty_anim)
        }

        gridLayoutManager = GridLayoutManager(context, 2)
        if (recyclerView.itemDecorationCount == 0)
            recyclerView.addItemDecoration(GenreItemDecoration(activity))
        recyclerView.layoutManager = gridLayoutManager
        adapter = GenreListAdapter({ _, position ->
            val args = Bundle()
            args.putSerializable("keyword", adapter.getItem(position))
            findNavController().navigate(R.id.action_genreFragment_to_movieListFragment, args)
        }, object : OnEmptyListener {
            override fun onEmpty() {
                setRecyclerEmpty(true)
            }

            override fun onNotEmpty() {
                setRecyclerEmpty(false)
            }

        })
        recyclerView.adapter = adapter

        viewModel.genreLiveData.observe(
            viewLifecycleOwner
        ) { genres ->
            genres?.let {
                if (genres.isEmpty()) {
                    adapter.onEmpty()
                } else {
                    adapter.onNotEmpty()
                    adapter.setList(genres)
                }
            } ?: run {
                adapter.onEmpty()
                showErrorSnackBar()
            }
        }

        lifecycleScope.launch {
            try {
                viewModel.fetchGenre()
            } catch (e: GenreNotFoundException) {
                e.printStackTrace()
                showErrorSnackBar()
            }
        }

    }

    override fun onDestroyView() {
        errorSnackBar.dismiss()
        super.onDestroyView()
    }

    private fun showErrorSnackBar() {
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
        shimmerFrameLayout.stopShimmer()
        shimmerFrameLayout.visibility = View.GONE
    }
}