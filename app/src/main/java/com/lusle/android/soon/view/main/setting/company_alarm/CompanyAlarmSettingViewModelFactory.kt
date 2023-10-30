package com.lusle.android.soon.view.main.setting.company_alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.model.source.FavoriteCompanyRepository

class CompanyAlarmSettingViewModelFactory(private val favoriteCompanyRepository: FavoriteCompanyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CompanyAlarmSettingViewModel(favoriteCompanyRepository) as T
    }

}
