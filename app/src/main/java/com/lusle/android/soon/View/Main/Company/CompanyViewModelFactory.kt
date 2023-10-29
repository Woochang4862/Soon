package com.lusle.android.soon.View.Main.Company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lusle.android.soon.Model.Source.FavoriteCompanyRepository

class CompanyViewModelFactory(private val favoriteCompanyRepository: FavoriteCompanyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CompanyViewModel(favoriteCompanyRepository) as T
    }
}