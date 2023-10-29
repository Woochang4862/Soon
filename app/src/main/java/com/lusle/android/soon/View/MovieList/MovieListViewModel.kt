package com.lusle.android.soon.View.MovieList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.lusle.android.soon.Model.Api.MovieApi
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.Model.Schema.Genre
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.Model.Schema.MovieDetail
import com.lusle.android.soon.Model.Schema.UiModel
import com.lusle.android.soon.Model.Source.CompanyPageKeyDataSource
import com.lusle.android.soon.Model.Source.FavoriteCompanyRepository
import com.lusle.android.soon.Model.Source.GenrePageKeyDataSource
import com.lusle.android.soon.Model.Source.RegionCodeRepository
import com.lusle.android.soon.Model.Source.SimilarMoviePageKeyDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieListViewModel(
    private val regionCodeRepository: RegionCodeRepository,
    private val favoriteCompanyRepository: FavoriteCompanyRepository
) : ViewModel() {

    private var genre: Genre? = null
    private var company: Company? = null
    private var movieDetail: MovieDetail? = null

    val subscribeLoadState: MutableLiveData<Boolean> = MutableLiveData(false)
    val movieLoadState: MutableLiveData<Boolean> = MutableLiveData(false)

    fun init(obj: Any?) {
        when (obj) {
            is Genre -> {
                genre = obj

                flow = Pager(config) {
                    GenrePageKeyDataSource(
                        id = genre!!.id,
                        movieApi = movieApi,
                        region = regionCodeRepository.regionCode
                    )
                }.flow.map { pagingData: PagingData<Movie> ->
                    // Map outer stream, so you can perform transformations on
                    // each paging generation.
                    pagingData
                        .map { movie -> UiModel.MovieModel(movie) }
                        .insertSeparators { before: UiModel.MovieModel?, _: UiModel.MovieModel? ->
                            if (before == null) {
                                UiModel.Header(genre!!.name)
                            } else {
                                null
                            }
                        }
                }.cachedIn(viewModelScope)
            }

            is Company -> {
                company = obj

                flow = Pager(config) {
                    CompanyPageKeyDataSource(
                        id = company!!.id,
                        movieApi = movieApi,
                        region = regionCodeRepository.regionCode
                    )
                }.flow.map { pagingData: PagingData<Movie> ->
                    // Map outer stream, so you can perform transformations on
                    // each paging generation.
                    pagingData
                        .map { movie -> UiModel.MovieModel(movie) }
                        .insertSeparators { before: UiModel.MovieModel?, _: UiModel.MovieModel? ->
                            if (before == null) {
                                val isSubscribed = favoriteCompanyRepository.loadFavoriteCompany()?.contains(company) ?: false
                                UiModel.CompanyHeader(company!!.name, isSubscribed)
                            } else {
                                null
                            }
                        }
                }.cachedIn(viewModelScope)


            }

            is MovieDetail -> {
                movieDetail = obj

                flow = Pager(config) {
                    SimilarMoviePageKeyDataSource(id = movieDetail!!.id, movieApi = movieApi)
                }.flow.map { pagingData: PagingData<Movie> ->
                    // Map outer stream, so you can perform transformations on
                    // each paging generation.
                    pagingData
                        .map { movie -> UiModel.MovieModel(movie) }
                        .insertSeparators { before: UiModel.MovieModel?, _: UiModel.MovieModel? ->
                            if (before == null) {
                                UiModel.Header(movieDetail!!.title)
                            } else {
                                null
                            }
                        }
                }.cachedIn(viewModelScope)
            }
        }
    }

    fun addCompany() {
        subscribeLoadState.value = true

        company?.let{favoriteCompanyRepository.addCompany(it)}

        subscribeLoadState.value = false
    }

    fun removeCompany() {
        subscribeLoadState.value = true

        company?.let{favoriteCompanyRepository.removeCompany(it)}

        subscribeLoadState.value = false
    }

    private val PAGE_SIZE: Int = 20

    private val movieApi = MovieApi.create()

    private val config = PagingConfig(PAGE_SIZE, PAGE_SIZE)

    var flow: Flow<PagingData<UiModel>>? = null

}
