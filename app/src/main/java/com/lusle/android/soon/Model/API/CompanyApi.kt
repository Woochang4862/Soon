package com.lusle.android.soon.Model.API

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.lusle.android.soon.Model.Schema.*
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

interface CompanyApi {
    companion object {
        fun create(): CompanyApi {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            return Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(OkHttpClient.Builder()
                            .addNetworkInterceptor(StethoInterceptor())
                            .addInterceptor(httpLoggingInterceptor.apply { httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC }).build())
                    .baseUrl("http://3.130.19.73:3000")
                    .build()
                    .create(CompanyApi::class.java)
        }
    }

    @GET("/api/search/company")
    fun searchCompany(@Query("query") query: String, @Query("region") region: String, @Query("page") page: Int): Single<CompanyResult>

    @GET("/api/alarm/check/{token}")
    fun checkSubscribedTopics(@Path("token") token: String): Call<SubscribedTopicsResult>

    @GET("/api/alarm/check/{token}/subscribe/{topic}")
    fun checkSubscribeTo(@Path("token") token: String, @Path("topic") topic: String): Single<SubscribeCheckResult>

    @FormUrlEncoded
    @POST("/api/alarm/add/alarm/company")
    fun addCompanyAlarm(@FieldMap body: HashMap<String, String>): Single<ResponseBody>

    @FormUrlEncoded
    @POST("/api/alarm/remove/alarm/company")
    fun removeCompanyAlarm(@FieldMap body: HashMap<String, String>): Single<ResponseBody>
}