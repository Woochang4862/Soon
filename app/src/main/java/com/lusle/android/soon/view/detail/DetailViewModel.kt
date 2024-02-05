package com.lusle.android.soon.view.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lusle.android.soon.model.api.MovieApi
import com.lusle.android.soon.model.schema.CreditsResult
import com.lusle.android.soon.model.schema.Movie
import com.lusle.android.soon.model.schema.MovieDetail
import com.lusle.android.soon.model.schema.WatchProviderResult
import com.lusle.android.soon.model.source.RegionCodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieDetailNotFoundException(private val e: Exception) : Exception(e) {
    override val message: String
        get() = "영화 상세정보를 불러오지 못했습니다\n${e.stackTrace}"
}

class CreditsNotFoundException(private val e: Exception) : Exception(e) {
    override val message: String
        get() = "영화 인물 정보를 불러올 수 없습니다\n${e.stackTrace}"
}

class WatchProviderNotFoundException(private val e: Exception) : Exception(e) {
    override val message: String
        get() = "공급사 정보를 불러올 수 없습니다\n${e.stackTrace}"
}

class SimilarMovieNotFoundException(private val e: Exception) : Exception(e) {
    override val message: String
        get() = "유사영화 정보를 불러올 수 없습니다\n${e.stackTrace}"
}

class DetailViewModel(private val regionCodeRepository: RegionCodeRepository) : ViewModel() {

    val movieDetail = MutableLiveData<MovieDetail>()

    val watchProviderResult = MutableLiveData<WatchProviderResult>()

    val creditsResult = MutableLiveData<CreditsResult>()

    val similarMovieResult = MutableLiveData<ArrayList<Movie>>()

    val movieId = MutableLiveData<Int>()

    private val movieApi = MovieApi.create()

    suspend fun fetch() {
        try {
            movieDetail.value = withContext(Dispatchers.IO) {
                movieId.value?.let {
                    movieApi.getMovieDetails(regionCodeRepository.regionCode, it).blockingGet()
                }
            }
        } catch (e:Exception) {
            throw MovieDetailNotFoundException(e)
        }

        try {
            creditsResult.value = withContext(Dispatchers.IO) {
                movieId.value?.let { movieApi.getCredits(it).blockingGet() }
            }
        } catch (e:Exception) {
            throw CreditsNotFoundException(e)
        }

        try {
            watchProviderResult.value = withContext(Dispatchers.IO) {
                movieId.value?.let {
                    movieApi.getWatchProvider(regionCodeRepository.regionCode, it).blockingGet()
                }
            }
        } catch (e:Exception) {
            throw WatchProviderNotFoundException(e)
        }

        try {
            similarMovieResult.value = withContext(Dispatchers.IO) {
                movieId.value?.let {
                    movieApi.getSimilarMovie(it, 1).blockingGet().results
                }
            }
        } catch (e:Exception){
            throw SimilarMovieNotFoundException(e)
        }
    }

}
