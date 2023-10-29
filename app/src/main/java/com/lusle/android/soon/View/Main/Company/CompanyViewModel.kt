package com.lusle.android.soon.View.Main.Company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.Model.Source.FavoriteCompanyRepository

class CompanyViewModel(private val favoriteCompanyRepository: FavoriteCompanyRepository) : ViewModel() {

    private val _favoriteCompanyLiveData:MutableLiveData<ArrayList<Company>> = MutableLiveData()

    val favoriteCompanyLiveData: LiveData<ArrayList<Company>> = _favoriteCompanyLiveData

    fun loadFavoriteCompany(){
        _favoriteCompanyLiveData.value = favoriteCompanyRepository.loadFavoriteCompany()
    }

}