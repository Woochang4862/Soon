package com.lusle.android.soon.view.main.setting.release_alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.model.source.ReleaseAlarmDataSource

class ReleaseAlarmSettingViewModelFactory(private val releaseAlarmDataSource: ReleaseAlarmDataSource) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReleaseAlarmSettingViewModel(releaseAlarmDataSource) as T
    }
}