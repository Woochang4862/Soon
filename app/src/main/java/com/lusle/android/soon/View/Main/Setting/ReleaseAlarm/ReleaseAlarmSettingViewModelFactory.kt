package com.lusle.android.soon.View.Main.Setting.ReleaseAlarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.Model.Source.ReleaseAlarmDataSource

class ReleaseAlarmSettingViewModelFactory(private val releaseAlarmDataSource: ReleaseAlarmDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReleaseAlarmSettingViewModel(releaseAlarmDataSource) as T
    }
}