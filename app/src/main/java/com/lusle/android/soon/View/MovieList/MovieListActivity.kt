package com.lusle.android.soon.View.MovieList

import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.Model.Schema.Genre
import com.lusle.android.soon.View.BaseActivity

class MovieListActivity : BaseActivity() {
    private var genre: Genre? = null
    private var company: Company? = null
    private val CONTENT_VIEW_ID = 10101010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val frame = FrameLayout(this)
        frame.id = CONTENT_VIEW_ID
        setContentView(frame, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        val obj: Any? = intent.getSerializableExtra("keyword")
        if (obj is Genre) {
            genre = obj
        } else if (obj is Company) {
            company = obj
        }
        if (savedInstanceState == null) {
            val movieListFragment: Fragment = MovieListFragment()
            val arguments = Bundle()
            if (genre != null) {
                arguments.putSerializable("keyword", genre)
            }
            if (company != null) {
                arguments.putSerializable("keyword", company)
            }
            movieListFragment.arguments = arguments
            val ft = supportFragmentManager.beginTransaction()
            ft.add(CONTENT_VIEW_ID, movieListFragment).commit()
        }
    }
}