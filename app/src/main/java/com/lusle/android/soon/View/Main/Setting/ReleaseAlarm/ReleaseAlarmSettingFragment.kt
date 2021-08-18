package com.lusle.android.soon.View.Main.Setting.ReleaseAlarm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener
import com.lusle.android.soon.Adapter.ReleaseAlarmSettingsAdapter
import com.lusle.android.soon.Model.Source.AlarmDataLocalSource
import com.lusle.android.soon.R
import com.lusle.android.soon.Util.Utils
import com.lusle.android.soon.View.Main.Setting.ReleaseAlarm.Presenter.ReleaseAlarmSettingContractor
import com.lusle.android.soon.View.Main.Setting.ReleaseAlarm.Presenter.ReleaseAlarmSettingPresenter

class ReleaseAlarmSettingFragment : Fragment(), ReleaseAlarmSettingContractor.View {
    private var isPaused: Boolean = false
    private val TAG: String = this::class.java.simpleName
    private lateinit var aSwitch: SwitchMaterial
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReleaseAlarmSettingsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var emptyViewGroup: FrameLayout
    private lateinit var emptyAnim: LottieAnimationView
    private lateinit var presenter: ReleaseAlarmSettingPresenter
    private lateinit var context: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_release_alarm_setting, container, false)
        presenter = ReleaseAlarmSettingPresenter()
        presenter.attachView(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setModel(AlarmDataLocalSource.getInstance(context))
        aSwitch = view.findViewById(R.id.alarm_switch)
        emptyViewGroup = view.findViewById(R.id.list_empty_view)
        emptyAnim = view.findViewById(R.id.list_empty_anim)
        recyclerView = view.findViewById(R.id.alarm_list_recyclerView)
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        adapter = ReleaseAlarmSettingsAdapter()
        presenter.setAdapterView(adapter)
        presenter.setAdapterModel(adapter)
        presenter.setOnItemClickListener(OnItemClickListener { _: View?, pos: Int ->
            val args = Bundle()
            args.putSerializable("alarm_info", adapter.getItem(pos))
            findNavController().navigate(R.id.action_releaseAlarmSettingFragment_to_alarmSettingFragment, args)
        })
        presenter.setOnEmptyListener()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        setAlarmSwitch()
        aSwitch.setOnClickListener {
            val alarms = presenter.alarms
            for (alarm in alarms) alarm.isActive = aSwitch.isChecked
            presenter.alarms = alarms
            Toast.makeText(context, "알림이 " + if (aSwitch.isChecked) "전부 켜졌습니다" else "전부 꺼졌습니다", Toast.LENGTH_SHORT).show()
            reload()
        }
        presenter.loadItems()
        Utils.runLayoutAnimation(recyclerView)
    }

    private fun setAlarmSwitch() {
        var checked = false
        for (alarm in presenter.alarms) {
            if (alarm.isActive) {
                checked = true
                break
            }
        }
        aSwitch.isChecked = checked
    }

    private fun reload() {
        setAlarmSwitch()
        presenter.loadItems()
        Utils.runLayoutAnimation(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        if(isPaused) {
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
}