package com.lusle.android.soon.Model.API;

import com.lusle.android.soon.Model.Schema.CompanyResult;
import com.lusle.android.soon.Model.Schema.GenreResult;
import com.lusle.android.soon.Model.Schema.MovieDetail;
import com.lusle.android.soon.Model.Schema.MovieResult;
import com.lusle.android.soon.Model.Schema.MultiResult;
import com.lusle.android.soon.Model.Schema.SubscribeCheckResult;
import com.lusle.android.soon.Model.Schema.SubscribedTopicsResult;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("/api/genre/all")
    Call<GenreResult> getGenreList();

    @GET("/api/movie/company")
    Call<MovieResult> discoverMovieWithCompany(@Query("id") Integer id, @Query("region") String region, @Query("page") Integer page);

    @GET("/api/movie/genre")
    Call<MovieResult> discoverMovieWithGenre(@Query("id") Integer id, @Query("region") String region, @Query("page") Integer page);

    @GET("/api/movie/date")
    Call<MovieResult> discoverMovieWithDate(@Query("date") String date, @Query("region") String region, @Query("page") Integer page);

    @GET("/api/search/multi/{region}/{query}/{page}")
    Call<MultiResult> searchMulti(@Path("query") String query, @Path("page") Integer page);

    @GET("/api/search/company")
    Call<CompanyResult> searchCompany(@Query("query") String query, @Query("region") String region, @Query("page") Integer page);

    @GET("/api/search/movie")
    Call<MovieResult> searchMovie(@Query("query") String query, @Query("region") String region, @Query("page") Integer page);

    @GET("/api/movie/detail")
    Call<MovieDetail> getMovieDetails(@Query("region") String region, @Query("id") Integer id);

    @GET("/api/movie/TMM")
    Call<MovieResult> getThisMonthMovie(@Query("region") String region, @Query("page") Integer page);

    @GET("/api/alarm/check/{token}")
    Call<SubscribedTopicsResult> checkSubscribedTopics(@Path("token") String token);

    @GET("/api/alarm/check/{token}/subscribe/{topic}")
    Call<SubscribeCheckResult> checkSubscribeTo(@Path("token") String token, @Path("topic") String topic);

    @FormUrlEncoded
    @POST("/api/alarm/add/alarm/company")
    Call<ResponseBody> addCompanyAlarm(@FieldMap HashMap<String, String> body);

    @FormUrlEncoded
    @POST("/api/alarm/remove/alarm/company")
    Call<ResponseBody> removeCompanyAlarm(@FieldMap HashMap<String, String> body);
}
