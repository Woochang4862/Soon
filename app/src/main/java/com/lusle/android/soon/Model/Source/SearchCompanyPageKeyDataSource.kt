package com.lusle.android.soon.Model.Source

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.lusle.android.soon.Model.API.CompanyApi
import com.lusle.android.soon.Model.Schema.Company
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchCompanyPageKeyDataSource(private val companyApi: CompanyApi, private val query:String, private val region: String) : PageKeyedDataSource<Int, Company>() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Company>) {
        Log.d("TAG", "loadInitial: ${params.requestedLoadSize}")
        companyApi.searchCompany(query, region, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            callback.onResult(it.results, 0, it.totalResults, null, 2)
                        },
                        {
                            it.printStackTrace()
                        }
                )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Company>) {
        Log.d("TAG", "loadAfter: ${params.key}")
        companyApi.searchCompany(query, region, params.key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            callback.onResult(it.results, params.key + 1)
                        },
                        {
                            it.printStackTrace()
                        }
                )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Company>) {
        Log.d("TAG", "loadBefore")
    }


}