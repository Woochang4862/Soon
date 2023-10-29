package com.lusle.android.soon.View.Detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lusle.android.soon.Model.Api.MovieApi
import com.lusle.android.soon.Model.Schema.CreditsResult
import com.lusle.android.soon.Model.Schema.MovieDetail
import com.lusle.android.soon.Model.Schema.MovieResult
import com.lusle.android.soon.Model.Schema.WatchProviderResult
import com.lusle.android.soon.Model.Source.RegionCodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailViewModel(private val regionCodeRepository: RegionCodeRepository) : ViewModel() {

    val movieDetail = MutableLiveData<MovieDetail>()

    val watchProviderResult = MutableLiveData<WatchProviderResult>()

    val creditsResult = MutableLiveData<CreditsResult>()

    val similarMovieResult = MutableLiveData<MovieResult>()

    val movieId = MutableLiveData<Int>()

    private val movieApi = MovieApi.create()

    suspend fun fetch() {
        try {
            movieDetail.value = withContext(Dispatchers.IO) {
                movieId.value?.let {
                    movieApi.getMovieDetails(regionCodeRepository.regionCode, it).blockingGet()
                }
            }

            creditsResult.value = withContext(Dispatchers.IO) {
                movieId.value?.let { movieApi.getCredits(it).blockingGet() }
            }

            watchProviderResult.value = withContext(Dispatchers.IO) {
                movieId.value?.let {
                    movieApi.getWatchProvider(regionCodeRepository.regionCode, it).blockingGet()
                }
            }

            similarMovieResult.value = withContext(Dispatchers.IO) {
                movieId.value?.let { movieApi.getSimilarMovie(it, 1).blockingGet() }
            }
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

}
