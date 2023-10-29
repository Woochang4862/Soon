package com.lusle.android.soon.View.Main.Setting.CompanyAlarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.Model.Source.FavoriteCompanyRepository

class CompanyAlarmSettingViewModelFactory(private val favoriteCompanyRepository: FavoriteCompanyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CompanyAlarmSettingViewModel(favoriteCompanyRepository) as T
    }

}
