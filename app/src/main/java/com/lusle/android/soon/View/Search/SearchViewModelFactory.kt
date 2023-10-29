package com.lusle.android.soon.View.Search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.Model.Source.RegionCodeRepository

class SearchViewModelFactory(private val regionCodeRepository: RegionCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(regionCodeRepository) as T
    }

}
