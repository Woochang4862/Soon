package com.lusle.android.soon.View.Search

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.SearchManager
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lusle.android.soon.Model.Source.RegionCodeRepository
import com.lusle.android.soon.MySuggestionProvider
import com.lusle.android.soon.R
import com.lusle.android.soon.View.BaseActivity
import com.lusle.android.soon.View.Main.ThisMonthMovie.ThisMonthMovieFragment
import com.lusle.android.soon.View.MovieList.MovieListActivity
import com.lusle.android.soon.adapter.Decoration.MovieItemDecoration
import com.lusle.android.soon.adapter.Listener.OnEmptyListener
import com.lusle.android.soon.adapter.SearchResultAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.max

open class SearchActivity : BaseActivity() {
    // View
    private lateinit var rootLayout: View
    private lateinit var searchResultRecyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var backBtn: ImageView
    private lateinit var pleaseInputQuerySnackBar: Snackbar
    private lateinit var searchBtn: ImageView

    private val viewModel by viewModels<SearchViewModel> { SearchViewModelFactory(RegionCodeRepository(this)) }
    private lateinit var layoutManager: GridLayoutManager
    private lateinit var decoration: MovieItemDecoration

    private lateinit var adapter: SearchResultAdapter
    private var revealX = 0
    private var revealY = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //RecyclerView
        searchResultRecyclerView = findViewById(R.id.search_result_recyclerview)
        clearRecyclerViewOption()

        // SnackBar
        pleaseInputQuerySnackBar = Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.please_input_query),
            Snackbar.LENGTH_SHORT
        ).setGestureInsetBottomIgnored(true)

        val intent = intent
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            val suggestions = SearchRecentSuggestions(
                this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE
            )
            suggestions.saveRecentQuery(query, null)
        }
        rootLayout = findViewById(R.id.rootView)
        if (savedInstanceState == null && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) && intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)
        ) {
            rootLayout.visibility = View.INVISIBLE
            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0)
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0)
            val viewTreeObserver = rootLayout.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        revealActivity(revealX, revealY)
                        rootLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        } else {
            rootLayout.visibility = View.VISIBLE
        }
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView = findViewById(R.id.activity_search_searchview)
        searchView.setSearchableInfo(
            searchManager.getSearchableInfo(
                componentName
            )
        )
        searchView.setIconifiedByDefault(false)
        searchView.isQueryRefinementEnabled = true
        searchView.requestFocus(1)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                var s = s
                s = s.trim { it <= ' ' }
                if (s == "") {
                    pleaseInputQuerySnackBar.show()
                } else {
                    val suggestions = SearchRecentSuggestions(
                        this@SearchActivity,
                        MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE
                    )
                    suggestions.saveRecentQuery(s, null)
                    searchView.clearFocus()
                    viewModel.query.value = s
                    lifecycleScope.launch {
                        Log.d(TAG, "onQueryTextSubmit: $s")
                        clearRecyclerViewOption()
                        viewModel.clearFlow()
                        viewModel.flow.collectLatest {
                            adapter.submitData(it)
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })
        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(i: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(i: Int): Boolean {
                val selectedView = searchView.suggestionsAdapter
                val cursor = selectedView.getItem(i) as Cursor
                val index = cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1)
                searchView.setQuery(cursor.getString(index), true)
                return true
            }
        })
        backBtn = findViewById(R.id.activity_search_back_btn)
        backBtn.setOnClickListener { unRevealActivity() }
        searchBtn = findViewById(R.id.activity_search_search_btn)
        searchBtn.setOnClickListener {
            searchView.setQuery(
                searchView.query,
                true
            )
        }
    }

    private fun clearRecyclerViewOption() {
        layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {

                if (adapter.getItemViewType(position) != R.layout.item_movie_recyclerview) return 2
                return 1
            }

        }
        searchResultRecyclerView.layoutManager = layoutManager
        adapter = SearchResultAdapter(
            { view, position ->

            },
            {
                //TODO:제작사 모두보기 화면 이동
            },
            { view, position ->
                val intent = Intent(this, MovieListActivity::class.java)
                intent.putExtra("keyword", adapter.getCompanyItem(position))
                startActivity(intent)
            },
            object:OnEmptyListener{
                override fun onEmpty() {

                }

                override fun onNotEmpty() {

                }
            }
        )
        adapter.addLoadStateListener { loadState ->
            adapter.onNotEmpty()
            adapter.let {
                Log.d(ThisMonthMovieFragment.TAG, "onLoadStateListener: $loadState")
                if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && it.itemCount < 1) {
                    Log.d(ThisMonthMovieFragment.TAG, "비어있음")
                    it.onEmpty()
                }
            }
        }
        searchResultRecyclerView.adapter = adapter
        decoration = MovieItemDecoration(this)
        var i = 0
        while(i<searchResultRecyclerView.itemDecorationCount){
            searchResultRecyclerView.removeItemDecorationAt(i)
            i+=1
        }
        searchResultRecyclerView.addItemDecoration(decoration)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            searchView.setQuery(query, true)
        }
    }

    protected fun revealActivity(x: Int, y: Int) {
        val finalRadius = (max(rootLayout.width, rootLayout.height) * 1.1).toFloat()

        // create the animator for this view (the start radius is zero)
        val circularReveal =
            ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0f, finalRadius)
        circularReveal.setDuration(1000)
        circularReveal.interpolator = AccelerateInterpolator()

        // make the view visible and start the animation
        rootLayout.visibility = View.VISIBLE
        circularReveal.start()
    }

    private fun unRevealActivity() {
        val finalRadius = (max(rootLayout.width, rootLayout.height) * 1.1).toFloat()
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            rootLayout, revealX, revealY, finalRadius, 0f
        )
        circularReveal.setDuration(1000)
        circularReveal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                rootLayout.visibility = View.INVISIBLE
                finish()
            }
        })
        circularReveal.start()
    }

    override fun onBackPressed() {
        unRevealActivity()
    }

    companion object {
        const val EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X"
        const val EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y"
        var TAG:String = SearchActivity::class.java.simpleName
    }
}
