package com.lusle.android.soon.Model.Api

import com.lusle.android.soon.Model.Schema.CreditsResult
import com.lusle.android.soon.Model.Schema.MovieDetail
import com.lusle.android.soon.Model.Schema.MovieResult
import com.lusle.android.soon.Model.Schema.WatchProviderResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    companion object {
        fun create(): MovieApi {
            return ApiClient.retrofit.create(MovieApi::class.java)
        }
    }

    @GET("/api/movie/company")
    fun discoverMovieWithCompany(@Query("id") id: Int, @Query("region") region: String, @Query("page") page: Int): Single<MovieResult>

    @GET("/api/movie/genre")
    fun discoverMovieWithGenre(@Query("id") id: Int, @Query("region") region: String, @Query("page") page: Int): Single<MovieResult>

    @GET("/api/movie/date")
    fun discoverMovieWithDate(@Query("date") date: String, @Query("region") region: String, @Query("page") page: Int): Single<MovieResult>

    @GET("/api/search/movie")
    fun searchMovie(@Query("query") query: String, @Query("region") region: String, @Query("page") page: Int): Single<MovieResult>

    @GET("/api/movie/detail")
    fun getMovieDetails(@Query("region") region: String, @Query("id") id: Int): Single<MovieDetail>

    @GET("/api/movie/TMM")
    fun getThisMonthMovie(@Query("region") region: String, @Query("page") page: Int): Single<MovieResult>

    @GET("/api/movie/watch/providers")
    fun getWatchProvider(@Query("region") region: String, @Query("id") id: Int): Single<WatchProviderResult>

    @GET("/api/movie/credits")
    fun getCredits(@Query("id") id: Int): Single<CreditsResult>

    @GET("/api/movie/similar")
    fun getSimilarMovie(@Query("id") id: Int,@Query("page") page: Int): Single<MovieResult>

}