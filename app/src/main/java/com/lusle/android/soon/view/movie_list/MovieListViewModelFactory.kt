package com.lusle.android.soon.view.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.model.source.FavoriteCompanyRepository
import com.lusle.android.soon.model.source.RegionCodeRepository

class MovieListViewModelFactory(private val regionCodeRepository: RegionCodeRepository, private val favoriteCompanyRepository: FavoriteCompanyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MovieListViewModel(regionCodeRepository, favoriteCompanyRepository) as T
    }
}