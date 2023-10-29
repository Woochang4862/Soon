package com.lusle.android.soon.View.Main.Setting.ReleaseAlarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lusle.android.soon.Model.Schema.Alarm
import com.lusle.android.soon.Model.Source.ReleaseAlarmDataSource
import kotlinx.coroutines.launch

class ReleaseAlarmSettingViewModel(private val releaseAlarmDataSource:ReleaseAlarmDataSource) : ViewModel() {
    private val _releaseAlarmLiveData:MutableLiveData<ArrayList<Alarm>> = MutableLiveData()
    var releaseAlarmLiveData:LiveData<ArrayList<Alarm>> = _releaseAlarmLiveData

    fun update(value: ArrayList<Alarm>) {
        viewModelScope.launch {
            _releaseAlarmLiveData.value = value
            releaseAlarmDataSource.alarms = value
        }
    }

    fun fetch() {
        viewModelScope.launch {
            _releaseAlarmLiveData.value = releaseAlarmDataSource.alarms
        }
    }
}