package com.lusle.android.soon.Model.Source

import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.lusle.android.soon.Model.API.MovieApi
import com.lusle.android.soon.Model.Schema.Movie
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

class GenrePageKeyDataSource(private val movieApi: MovieApi, private val region: String, private val id:Int) : PageKeyedDataSource<Int, Movie>() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        Log.d("TAG", "loadInitial: ${params.requestedLoadSize}")
        movieApi.discoverMovieWithGenre(id, region, 1)
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

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        Log.d("TAG", "loadAfter: ${params.key}")
        movieApi.discoverMovieWithGenre(id, region, params.key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            callback.onResult(it.results, params.key + 1)
                        },
                        {
                            //error control
                        }
                )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        Log.d("TAG", "loadBefore")
    }


}