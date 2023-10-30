package com.lusle.android.soon.view.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.example.android.navigationadvancedsample.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lusle.android.soon.model.schema.Alarm
import com.lusle.android.soon.R
import com.lusle.android.soon.view.alarm.AlarmReceiver
import com.lusle.android.soon.view.alarm.AlarmSettingFragment
import com.lusle.android.soon.view.BaseActivity
import com.lusle.android.soon.view.search.SearchActivity
import com.skydoves.transformationlayout.onTransformationStartContainer
import java.util.ArrayList

//TODO:리펙토링
class MainActivity : BaseActivity() {
    private val REQUEST_CODE_UPDATE: Int = 200
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var searchFab: FloatingActionButton
    private var currentNavController: LiveData<NavController>? = null
    private var appUpdateManager: AppUpdateManager? = null
    private var snackbarForProgressBar : Snackbar? = null

    companion object {
        val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onTransformationStartContainer()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null){
            setUpBottomNavigationBar()
        }

        appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager?.let {
            it.registerListener { state ->
                if (state.installStatus() == InstallStatus.DOWNLOADING) {
                    val bytesDownloaded = state.bytesDownloaded()
                    val totalBytesToDownload = state.totalBytesToDownload()
                    snackbarForProgressBar?.setText("업데이트 중 ... (${(bytesDownloaded / totalBytesToDownload) * 100}%)")
                }
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    // After the update is downloaded, show a notification
                    // and request user confirmation to restart the app.
                    popupSnackbarForCompleteUpdate()
                }
            }
            it.appUpdateInfo.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                Log.d(TAG, "onSuccess: updateAvailability() : ${appUpdateInfo.updateAvailability()}")
                Log.d(TAG, "onSuccess: isUpdateTypeAllowed() : ${appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)}")
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
                {
                    popupSnackbarForProgressBar()
                    it.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            this,
                            REQUEST_CODE_UPDATE
                    )
                }
            }
        }

        val preferences = getSharedPreferences("alarmPref", Context.MODE_PRIVATE)
        val type = object : TypeToken<ArrayList<Alarm>>() {}.type
        val json = preferences.getString("alarms", "")
        var alarms = Gson().fromJson<ArrayList<Alarm>>(json, type)
        if (alarms == null) alarms = ArrayList()
        for (a in alarms) {
            /*boolean alarmUp = (PendingIntent.getBroadcast(context, a.getPendingIntentID(),
                    new Intent(context, AlarmReceiver.class),
                    PendingIntent.FLAG_NO_CREATE) != null);*/

            val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val i = Intent(this, AlarmReceiver::class.java)
            i.putExtra(AlarmSettingFragment.KEY_ALARM_ID, a)
            i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)

            var pendingIntent = PendingIntent.getBroadcast(this, a.pendingIntentID, i,
                PendingIntent.FLAG_MUTABLE)

            if (pendingIntent != null) {
                am.cancel(pendingIntent)
            }

            pendingIntent = PendingIntent.getBroadcast(this, a.pendingIntentID, i, PendingIntent.FLAG_UPDATE_CURRENT)

            am[AlarmManager.RTC_WAKEUP, a.milliseconds] = pendingIntent
        }

        init()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_UPDATE){
            if(resultCode != RESULT_OK){
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                snackbarForProgressBar?.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager?.let {
            it.appUpdateInfo
                .addOnSuccessListener { appUpdateInfo ->
                    // If the update is downloaded but not installed,
                    // notify the user to complete the update.
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate()
                    }
                }
        }

    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
                findViewById(android.R.id.content),
                "업데이트를 위한 다운로드가 완료되었습니다.",
                Snackbar.LENGTH_INDEFINITE
        ).apply {
            anchorView = findViewById(R.id.floatingActionButton)
            setAction("재시작") { appUpdateManager?.completeUpdate() }
            show()
        }
    }

    private fun popupSnackbarForProgressBar() {
        Snackbar.make(
                findViewById(android.R.id.content),
                "업데이트 중 ... (0%)",
                Snackbar.LENGTH_INDEFINITE
        ).apply {
            snackbarForProgressBar = this
            anchorView = findViewById(R.id.floatingActionButton)
            (view.findViewById<View>(com.google.android.material.R.id.snackbar_action).parent as ViewGroup).addView(ProgressBar(this@MainActivity))
            show()
        }
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

        bottomNavigationView.setOnItemReselectedListener {
            when(it.itemId){
                R.id.home -> {
                    if (currentNavController != null && currentNavController!!.value != null) {
                        val inflater = currentNavController!!.value!!.navInflater
                        val graph = inflater.inflate(R.navigation.navigation_home)
                        graph.setStartDestination(R.id.thisMonthMovieFragment)
                        currentNavController!!.value!!.graph = graph
                    }
                }
                R.id.company -> {
                    if (currentNavController != null && currentNavController!!.value != null) {
                        val inflater = currentNavController!!.value!!.navInflater
                        val graph = inflater.inflate(R.navigation.navigation_company)
                        graph.setStartDestination(R.id.companyFragment)
                        currentNavController!!.value!!.graph = graph
                    }
                }
                R.id.genre -> {
                    if (currentNavController != null && currentNavController!!.value != null) {
                        val inflater = currentNavController!!.value!!.navInflater
                        val graph = inflater.inflate(R.navigation.navigation_genre)
                        graph.setStartDestination(R.id.genreFragment)
                        currentNavController!!.value!!.graph = graph
                    }
                }
                R.id.settings -> {
                    if (currentNavController != null && currentNavController!!.value != null) {
                        val inflater = currentNavController!!.value!!.navInflater
                        val graph = inflater.inflate(R.navigation.navigation_settings)
                        graph.setStartDestination(R.id.preferenceFragment)
                        currentNavController!!.value!!.graph = graph
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun init() {
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic("all")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM Token", "getInstanceId failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new Instance ID token
            val token = task.result.toString()

            // Log and toast
            Log.d("FCM Token", token)
        }
        searchFab = findViewById(R.id.floatingActionButton)
        searchFab.setOnClickListener { view: View -> presentActivity(view) }
    }

    private fun presentActivity(view: View) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition")
        val revealX = (view.x + view.width / 2).toInt()
        val revealY = (view.y + view.height / 2).toInt()
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra(SearchActivity.EXTRA_CIRCULAR_REVEAL_X, revealX)
        intent.putExtra(SearchActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY)
        ActivityCompat.startActivity(this, intent, options.toBundle())
    }
}