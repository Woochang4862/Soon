package com.lusle.android.soon.view.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.lusle.android.soon.model.api.CompanyApi
import com.lusle.android.soon.model.api.MovieApi
import com.lusle.android.soon.model.schema.CompanyResult
import com.lusle.android.soon.model.schema.Movie
import com.lusle.android.soon.model.schema.UiModel
import com.lusle.android.soon.model.source.RegionCodeRepository
import com.lusle.android.soon.model.source.SearchMoviePageKeyDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SearchViewModel (private val regionCodeRepository: RegionCodeRepository, ) : ViewModel() {

    private val PAGE_SIZE: Int = 20

    private val movieApi = MovieApi.create()

    private val companyApi = CompanyApi.create()

    private val config = PagingConfig(PAGE_SIZE, PAGE_SIZE)

    val query: MutableLiveData<String> = MutableLiveData("")

    var flow = Pager(config) {
        SearchMoviePageKeyDataSource(movieApi = movieApi, region = regionCodeRepository.regionCode, query = query.value ?: "")
    }.flow.map { pagingData: PagingData<Movie> ->
        // Map outer stream, so you can perform transformations on
        // each paging generation.
        pagingData
            .map { movie -> UiModel.MovieModel(movie) }
            .insertSeparators { before: UiModel?, after: UiModel? ->
                if(before == null && after is UiModel.MovieModel) {
                    UiModel.Header("영화 결과")
                } else {
                    null
                }
            }
            .insertSeparators { before: UiModel?, after: UiModel? ->
                if (before == null) {
                    var companyResult: CompanyResult?
                    withContext(Dispatchers.IO) {
                        companyResult = companyApi.searchCompany(query.value ?: "", regionCodeRepository.regionCode, 1).blockingGet()
                    }
                    if (companyResult?.results?.isNotEmpty() == true) {
                        UiModel.CompanyResult(companyResult?.results ?: arrayListOf())
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
            .insertSeparators { before: UiModel?, after: UiModel?->
                if (before == null && after is UiModel.CompanyResult) {
                    UiModel.Header("제작사 결과")
                } else {
                    null
                }
            }

    }.cachedIn(viewModelScope)

    fun clearFlow(){
        flow = Pager(config) {
            SearchMoviePageKeyDataSource(movieApi = movieApi, region = regionCodeRepository.regionCode, query = query.value ?: "")
        }.flow.map { pagingData: PagingData<Movie> ->
            // Map outer stream, so you can perform transformations on
            // each paging generation.
            pagingData
                .map { movie -> UiModel.MovieModel(movie) }
                .insertSeparators { before: UiModel?, after: UiModel? ->
                    if(before == null && after is UiModel.MovieModel) {
                        UiModel.Header("영화 결과")
                    } else {
                        null
                    }
                }
                .insertSeparators { before: UiModel?, after: UiModel? ->
                    if (before == null) {
                        var companyResult: CompanyResult?
                        withContext(Dispatchers.IO) {
                            companyResult = companyApi.searchCompany(query.value ?: "", regionCodeRepository.regionCode, 1).blockingGet()

                        }
                        if (companyResult?.results?.isNotEmpty() == true) {
                            UiModel.CompanyResult(companyResult?.results ?: arrayListOf())
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
                .insertSeparators { before: UiModel?, after: UiModel?->
                    if (before == null && after is UiModel.CompanyResult) {
                        UiModel.Header("제작사 결과")
                    } else {
                        null
                    }
                }

        }.cachedIn(viewModelScope)
    }
}