package com.lusle.android.soon.View.Main.ThisMonthMovie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.lusle.android.soon.Model.Api.MovieApi
import com.lusle.android.soon.Model.Schema.Movie
import com.lusle.android.soon.Model.Schema.UiModel
import com.lusle.android.soon.Model.Source.RegionCodeRepository
import com.lusle.android.soon.Model.Source.TMMPageKeyDataSource
import kotlinx.coroutines.flow.map

class ThisMonthMovieViewModel(private val regionCodeRepository: RegionCodeRepository) : ViewModel() {

    private val PAGE_SIZE: Int = 20

    private val movieApi = MovieApi.create()

    private val config = PagingConfig(PAGE_SIZE, PAGE_SIZE)

    val flow = Pager(config) {
        TMMPageKeyDataSource(movieApi = movieApi, region = regionCodeRepository.regionCode)
    }.flow.map { pagingData: PagingData<Movie> ->
        // Map outer stream, so you can perform transformations on
        // each paging generation.
        pagingData
                .map { movie -> UiModel.MovieModel(movie) }
                .insertSeparators { before: UiModel.MovieModel?, after: UiModel.MovieModel? ->
                    if (before == null) {
                        UiModel.Header("이번 달")
                    } else {
                        null
                    }
                }
    }.cachedIn(viewModelScope)

}