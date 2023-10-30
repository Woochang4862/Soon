package com.lusle.android.soon.view.company_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.model.source.RegionCodeRepository

class CompanyListViewModelFactory(private val regionCodeRepository: RegionCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CompanyListViewModel(regionCodeRepository) as T
    }
}