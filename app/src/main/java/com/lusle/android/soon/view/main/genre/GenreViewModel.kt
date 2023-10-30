package com.lusle.android.soon.view.main.genre

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lusle.android.soon.model.api.GenreApi
import com.lusle.android.soon.model.schema.Genre
import com.lusle.android.soon.model.source.RegionCodeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GenreViewModel(private val regionCodeRepository: RegionCodeRepository) : ViewModel() {

    private val genreApi = GenreApi.create()

    private val _genreLiveData: MutableLiveData<ArrayList<Genre>> = MutableLiveData()
    val genreLiveData: LiveData<ArrayList<Genre>> = _genreLiveData

    suspend fun fetchGenre() {
        _genreLiveData.value = try {
            withContext(Dispatchers.IO) {
                genreApi.getGenreList(regionCodeRepository.regionCode).blockingGet().genres
            }
        } catch (e: Exception) {
            throw GenreNotFoundException(e)
        }
    }
}

class GenreNotFoundException(private val e: Exception) : Exception(e) {
    override val message: String
        get() = "장르를 불러오는 데 문제가 생겼습니다\n${e.stackTrace}"
}
