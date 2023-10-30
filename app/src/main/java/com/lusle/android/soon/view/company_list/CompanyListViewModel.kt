package com.lusle.android.soon.view.company_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lusle.android.soon.model.api.CompanyApi
import com.lusle.android.soon.model.schema.Company
import com.lusle.android.soon.model.source.SearchCompanyPageKeyDataSource
import com.lusle.android.soon.model.source.RegionCodeRepository
import kotlinx.coroutines.flow.Flow

class CompanyListViewModel(private val regionCodeRepository: RegionCodeRepository) :ViewModel() {

    private val PAGE_SIZE: Int = 20

    private val companyApi = CompanyApi.create()

    private val config = PagingConfig(PAGE_SIZE, PAGE_SIZE)

    fun getFlow(query:String): Flow<PagingData<Company>>{
        return Pager(config) {
            SearchCompanyPageKeyDataSource(companyApi, query,regionCodeRepository.regionCode)
        }.flow.cachedIn(viewModelScope)
    }
}