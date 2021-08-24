package com.lusle.android.soon.View.Alarm

import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.View.BaseActivity

class AlarmSettingActivity : BaseActivity() {
    private var movie: Movie? = null
    private val CONTENT_VIEW_ID = 10101010

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val frame = FrameLayout(this)
        frame.id = CONTENT_VIEW_ID
        setContentView(frame, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        val obj: Any? = intent.getSerializableExtra("movie_info")
        if (obj is Movie) {
            movie = obj
        }
        if (savedInstanceState == null) {
            val alarmSettingFragment: Fragment = AlarmSettingFragment()
            val arguments = Bundle()
            movie?.let{
                arguments.putSerializable("movie_info", it)
            }
            alarmSettingFragment.arguments = arguments
            val ft = supportFragmentManager.beginTransaction()
            ft.add(CONTENT_VIEW_ID, alarmSettingFragment).commit()
        }
    }
}