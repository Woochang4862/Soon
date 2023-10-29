package com.lusle.android.soon.View.Main.ThisMonthMovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.Model.Source.RegionCodeRepository

class ThisMonthMovieViewModelFactory(private val regionCodeRepository: RegionCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ThisMonthMovieViewModel(regionCodeRepository) as T
    }
}