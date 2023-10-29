package com.lusle.android.soon.View.Detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.Model.Source.RegionCodeRepository

class DetailViewModelProvider(private val regionCodeRepository: RegionCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T & Any {
        return DetailViewModel(regionCodeRepository) as (T & Any)
    }

}
