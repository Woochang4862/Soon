package com.lusle.android.soon.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.model.source.RegionCodeRepository

class DetailViewModelProvider(private val regionCodeRepository: RegionCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        return DetailViewModel(regionCodeRepository) as T
    }

}
