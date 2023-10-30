package com.lusle.android.soon.model.api

import com.lusle.android.soon.model.schema.CompanyResult
import com.lusle.android.soon.model.schema.SubscribeCheckResult
import com.lusle.android.soon.model.schema.SubscribedTopicsResult
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface CompanyApi {

    companion object {
        fun create(): CompanyApi {
            return ApiClient.retrofit.create(CompanyApi::class.java)
        }
    }

    @GET("/api/search/company")
    fun searchCompany(@Query("query") query: String, @Query("region") region: String, @Query("page") page: Int): Single<CompanyResult>

    @GET("/api/alarm/check/{token}")
    fun checkSubscribedTopics(@Path("token") token: String): Single<SubscribedTopicsResult>

    @GET("/api/alarm/check/{token}/subscribe/{topic}")
    fun checkSubscribeTo(@Path("token") token: String, @Path("topic") topic: String): Single<SubscribeCheckResult>

    @FormUrlEncoded
    @POST("/api/alarm/add/alarm/company")
    fun addCompanyAlarm(@FieldMap body: HashMap<String, String>): Single<ResponseBody>

    @FormUrlEncoded
    @POST("/api/alarm/remove/alarm/company")
    fun removeCompanyAlarm(@FieldMap body: HashMap<String, String>): Single<ResponseBody>
}