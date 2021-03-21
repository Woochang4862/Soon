package com.lusle.android.soon.View.Main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.example.android.navigationadvancedsample.setupWithNavController
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessaging
import com.lusle.android.soon.R
import com.lusle.android.soon.View.BaseActivity
import com.lusle.android.soon.View.Search.SearchActivity

class MainActivity : BaseActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var searchFab: FloatingActionButton
    private val requestCode = 666
    private var currentNavController: LiveData<NavController>? = null
    private val TAG = MainActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null){
            setUpBottomNavigationBar()
        }
        init()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setUpBottomNavigationBar()
    }

    private fun setUpBottomNavigationBar() {
        bottomNavigationView = findViewById(R.id.navigation_view)
        bottomNavigationView.menu.findItem(R.id.blank).isEnabled = false
        val navGraphIds = listOf(R.navigation.navigation_home, R.navigation.navigation_company, R.navigation.navigation_genre, R.navigation.navigation_settings)

        val controller = bottomNavigationView.setupWithNavController(
                navGraphIds, supportFragmentManager, R.id.nav_host_fragment, intent
        )

        // Whenever the selected controller changes, setup the action bar.
        /*
        controller.observe(this, Observer { navController -> setupActionBarWithNavController(navController) })
        */
        //액션바 관련 코드 (액션바를 사용하지 않기 때문에 주석처리)

        currentNavController = controller

        bottomNavigationView.setOnNavigationItemReselectedListener {
            when(it.itemId){
                R.id.home -> {

                }
                R.id.company -> {}
                R.id.genre -> {}
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun init() {
        //MobileAds.initialize(this, "ca-app-pub-2329923322434251~4419072683");
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic("all")
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener { task: Task<InstanceIdResult> ->
                    if (!task.isSuccessful) {
                        Log.w("FCM Token", "getInstanceId failed", task.exception)
                        return@addOnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result.token

                    // Log and toast
                    Log.d("FCM Token", token)
                }
        searchFab = findViewById(R.id.floatingActionButton)
        searchFab.setOnClickListener(View.OnClickListener { view: View -> presentActivity(view) })
    }

    private fun presentActivity(view: View) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition")
        val revealX = (view.x + view.width / 2).toInt()
        val revealY = (view.y + view.height / 2).toInt()
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra(SearchActivity.EXTRA_CIRCULAR_REVEAL_X, revealX)
        intent.putExtra(SearchActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY)
        ActivityCompat.startActivityForResult(this, intent, requestCode, options.toBundle())
    }
}