package com.lusle.android.soon.View.Main.Setting.CompanyAlarm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.iid.FirebaseInstanceId
import com.lusle.android.soon.Adapter.CompanyAlarmSettingsAdapter
import com.lusle.android.soon.Model.Contract.SubscribeCheckDataRemoteSourceContract
import com.lusle.android.soon.Model.Source.CompanyAlarmManagerRemoteSource
import com.lusle.android.soon.Model.Source.FavoriteCompanyDataLocalSource
import com.lusle.android.soon.Model.Source.SubscribeCheckDataRemoteSource
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.Util
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmSettingContractor
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmSettingPresenter
import java.util.*

class CompanyAlarmSettingFragment : Fragment(), CompanyAlarmSettingContractor.View {
    private lateinit var aSwitch: SwitchMaterial
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CompanyAlarmSettingsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var emptyViewGroup: FrameLayout
    private lateinit var emptyAnim: LottieAnimationView
    private lateinit var presenter: CompanyAlarmSettingPresenter
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private val clickedByUser = true
    private var isPaused = false
    private lateinit var context: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_company_alarm_setting, container, false)
        presenter = CompanyAlarmSettingPresenter()
        presenter.attachView(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shimmerFrameLayout = view.findViewById(R.id.shimmer)
        presenter = CompanyAlarmSettingPresenter()
        presenter.attachView(this)
        presenter.setModel(FavoriteCompanyDataLocalSource.getInstance())
        presenter.setModel(SubscribeCheckDataRemoteSource.getInstance())
        presenter.setModel(CompanyAlarmManagerRemoteSource.getInstance())
        aSwitch = view.findViewById(R.id.alarm_switch)
        emptyViewGroup = view.findViewById(R.id.list_empty_view)
        emptyAnim = view.findViewById(R.id.list_empty_anim)
        recyclerView = view.findViewById(R.id.company_alarm_list_recyclerView)
        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        adapter = CompanyAlarmSettingsAdapter(presenter)
        presenter.setAdapterView(adapter)
        presenter.setAdapterModel(adapter)
        presenter.setOnEmptyListener()
        recyclerView.adapter = adapter
        try {
            recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        } catch (e:IllegalStateException){
            e.printStackTrace()
        }
        playShimmer(true)
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result.token
                    presenter.checkSubscribedTopics(token)
                })
        presenter.setOnFinishedListener(object : SubscribeCheckDataRemoteSourceContract.Model.OnFinishedListener {
            override fun onFinished(topics: ArrayList<String>) {
                presenter.topics = topics
                presenter.loadItems()
                playShimmer(false)
                Util.runLayoutAnimation(recyclerView)
            }

            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                playShimmer(false)
            }
        })
        setAlarmSwitch()
        aSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (clickedByUser) {
            }
        })
    }

    private fun setAlarmSwitch() {
        /*clickedByUser = false;
        boolean checked = false;
        for (Alarm alarm : presenter.getAlarms()) {
            if (alarm.isActive()) {
                checked = true;
                break;
            }
        }
        aSwitch.setChecked(checked);
        clickedByUser = true;*/
    }

    private fun reload() {
        setAlarmSwitch()
        presenter.loadItems()
        Util.runLayoutAnimation(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        if (isPaused) {
            reload()
            isPaused = false
        }
    }

    override fun setRecyclerEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            recyclerView.visibility = View.GONE
            emptyViewGroup.visibility = View.VISIBLE
            if (!emptyAnim.isAnimating) emptyAnim.playAnimation()
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyViewGroup.visibility = View.GONE
            if (emptyAnim.isAnimating) emptyAnim.pauseAnimation()
        }
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun getContext(): Context {
        return context
    }

    override fun playShimmer(show: Boolean) {
        if (show) {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}