package com.lusle.android.soon.model.api

import com.lusle.android.soon.model.schema.GenreResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GenreApi {

    companion object {
        fun create(): GenreApi {
            return ApiClient.retrofit.create(GenreApi::class.java)
        }
    }

    @GET("/api/genre/all")
    fun getGenreList(@Query("region") region: String): Single<GenreResult>

}