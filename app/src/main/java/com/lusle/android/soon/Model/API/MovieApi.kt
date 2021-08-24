package com.lusle.android.soon.Model.API

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.lusle.android.soon.Model.Schema.GenreResult
import com.lusle.android.soon.Model.Schema.MovieDetail
import com.lusle.android.soon.Model.Schema.MovieResult
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {
    companion object {
        fun create(): MovieApi {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            return Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(OkHttpClient.Builder()
                            .addNetworkInterceptor(StethoInterceptor())
                            .addInterceptor(httpLoggingInterceptor.apply { httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC }).build())
                    .baseUrl("http://3.130.19.73:3000")
                    .build()
                    .create(MovieApi::class.java)
        }
    }

    @GET("/api/genre/all")
    fun getGenreList(@Query("region") region : String): Single<GenreResult>

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

}