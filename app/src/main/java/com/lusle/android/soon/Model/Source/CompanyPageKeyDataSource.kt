package com.lusle.android.soon.Model.Source

import android.annotation.SuppressLint
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lusle.android.soon.Model.Api.MovieApi
import com.lusle.android.soon.Model.Schema.Movie
import io.reactivex.schedulers.Schedulers

class CompanyPageKeyDataSource(private val movieApi: MovieApi, private val region: String, private val id:Int) : PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    @SuppressLint("LongLogTag")
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        Log.d(TAG, "load: loadSize: ${params.loadSize}")
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val response = movieApi.discoverMovieWithCompany(id, region, nextPageNumber).subscribeOn(Schedulers.io()).blockingGet()

            Log.d(TAG, "load: result size: ${response.results.size}")
            if (response.results.size == 0){
                return LoadResult.Page(
                    data = response.results,
                    prevKey = null, // Only paging forward.
                    nextKey = null
                )
            }
            return LoadResult.Page(
                data = response.results,
                prevKey = null, // Only paging forward.
                nextKey = nextPageNumber + 1
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an
            // expected error (such as a network failure).
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

    companion object {
        private const val TAG = "CompanyPageKeyDataSource"
    }

}