package com.lusle.android.soon.view.main.this_month_movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.model.source.RegionCodeRepository

class ThisMonthMovieViewModelFactory(private val regionCodeRepository: RegionCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ThisMonthMovieViewModel(regionCodeRepository) as T
    }
}