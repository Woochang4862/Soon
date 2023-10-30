package com.lusle.android.soon.view.main.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.model.source.FavoriteCompanyRepository

class CompanyViewModelFactory(private val favoriteCompanyRepository: FavoriteCompanyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CompanyViewModel(favoriteCompanyRepository) as T
    }
}