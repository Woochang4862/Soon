package com.lusle.android.soon.View.Main.Genre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.lusle.android.soon.Adapter.Decoration.GenreItemDecoration
import com.lusle.android.soon.Adapter.GenreListAdapter
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener
import com.lusle.android.soon.Model.API.MovieApi
import com.lusle.android.soon.Model.Source.GenreDataRemoteSource
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.Util
import com.lusle.android.soon.View.Dialog.MovieProgressDialog
import com.lusle.android.soon.View.Main.Genre.Presenter.GenreContractor
import com.lusle.android.soon.View.Main.Genre.Presenter.GenrePresenter
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GenreFragment : Fragment(), GenreContractor.View {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: FrameLayout
    private lateinit var emptyAnim: LottieAnimationView
    private lateinit var dialog: MovieProgressDialog
    private lateinit var presenter: GenrePresenter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: GenreListAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_genre, container, false)
        presenter = GenrePresenter()
        presenter.attachView(this)
        presenter.setModel(GenreDataRemoteSource.getInstance())
        dialog = MovieProgressDialog(requireContext())
        recyclerView = view.findViewById(R.id.fragment_genre_genre_list)
        emptyView = view.findViewById(R.id.list_empty_view)
        emptyAnim = view.findViewById(R.id.list_empty_anim)
        gridLayoutManager = GridLayoutManager(context, 2)
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
        presenter.setAdapterView(adapter)
        presenter.setAdapterModel(adapter)
        recyclerView.adapter = adapter
        //presenter.loadItems()

        val disposable = MovieApi.create().getGenreList(Util.getRegionCode(requireContext()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    adapter.setList(result.genres)
                    adapter.notifyAdapter()
                }
        return view
    }

    override fun runRecyclerViewAnimation() {
        Util.runLayoutAnimation(recyclerView)
    }

    override fun showDialog(show: Boolean) {
        if (show) dialog.show() else dialog.dismiss()
    }

    override fun showErrorToast() {
        DynamicToast.makeError(requireContext(), getString(R.string.server_error_msg)).show()
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

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance(): GenreFragment {
            val args = Bundle()
            val fragment = GenreFragment()
            fragment.arguments = args
            return fragment
        }
    }
}