package com.lusle.android.soon.view.main.setting.release_alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.lusle.android.soon.model.source.ReleaseAlarmDataSource
import com.lusle.android.soon.R
import com.lusle.android.soon.adapter.ReleaseAlarmSettingsAdapter
import kotlinx.coroutines.launch

class ReleaseAlarmSettingFragment : Fragment(){

    private lateinit var aSwitch: SwitchMaterial
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReleaseAlarmSettingsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var emptyViewGroup: FrameLayout
    private lateinit var emptyAnim: LottieAnimationView

    private val viewModel by viewModels<ReleaseAlarmSettingViewModel> {
        ReleaseAlarmSettingViewModelFactory(ReleaseAlarmDataSource(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_release_alarm_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aSwitch = view.findViewById(R.id.alarm_switch)
        emptyViewGroup = view.findViewById(R.id.list_empty_view)
        emptyAnim = view.findViewById(R.id.list_empty_anim)
        recyclerView = view.findViewById(R.id.alarm_list_recyclerView)
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        adapter = ReleaseAlarmSettingsAdapter()
        adapter.setOnItemClickListener { _: View?, pos: Int ->
            val args = Bundle()
            args.putSerializable("alarm_info", adapter.getItem(pos))
            findNavController().navigate(R.id.action_releaseAlarmSettingFragment_to_alarmSettingFragment, args)
        }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        aSwitch.setOnClickListener {
            val alarms = adapter.list
            if (alarms.isEmpty()) return@setOnClickListener
            for (alarm in alarms) alarm.isActive = aSwitch.isChecked
            viewModel.update(alarms)
            Toast.makeText(context, "알림이 " + if (aSwitch.isChecked) "전부 켜졌습니다" else "전부 꺼졌습니다", Toast.LENGTH_SHORT).show()
            reload()
        }
        lifecycleScope.launch {
            viewModel.releaseAlarmLiveData.observe(viewLifecycleOwner
            ) {
                if (it.isEmpty()){
                    setRecyclerEmpty(true)
                } else {
                    setRecyclerEmpty(false)
                    adapter.list = it
                    setAlarmSwitch()
                }
            }
            viewModel.fetch()
        }
    }

    private fun setAlarmSwitch() {
        var checked = false
        for (alarm in adapter.list) {
            if (alarm.isActive) {
                checked = true
                break
            }
        }
        aSwitch.isChecked = checked
    }

    private fun reload() {
        lifecycleScope.launch {
            viewModel.fetch()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.fetch()
        }
    }

    private fun setRecyclerEmpty(isEmpty: Boolean) {
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

    companion object {
        val TAG: String = ReleaseAlarmSettingFragment::class.java.simpleName
    }
}