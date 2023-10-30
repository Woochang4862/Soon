package com.lusle.android.soon.view.main.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.model.source.FavoriteCompanyRepository

class CompanyViewModel(private val favoriteCompanyRepository: FavoriteCompanyRepository) : ViewModel() {

    private val _favoriteCompanyLiveData:MutableLiveData<ArrayList<Company>> = MutableLiveData()

    val favoriteCompanyLiveData: LiveData<ArrayList<Company>> = _favoriteCompanyLiveData

    fun loadFavoriteCompany(){
        _favoriteCompanyLiveData.value = favoriteCompanyRepository.loadFavoriteCompany()
    }

}