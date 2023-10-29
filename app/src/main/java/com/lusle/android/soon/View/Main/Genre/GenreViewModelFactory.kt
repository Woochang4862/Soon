package com.lusle.android.soon.View.Main.Genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.Model.Source.RegionCodeRepository

class GenreViewModelFactory(private val regionCodeRepository: RegionCodeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GenreViewModel(regionCodeRepository) as T
    }
}