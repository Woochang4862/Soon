package com.lusle.android.soon.Model.API;

import com.lusle.android.soon.Model.Schema.CompanyResult;
import com.lusle.android.soon.Model.Schema.GenreResult;
import com.lusle.android.soon.Model.Schema.MovieDetail;
import com.lusle.android.soon.Model.Schema.MovieResult;
import com.lusle.android.soon.Model.Schema.MultiResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Path;
import retrofit2.http.Path;

public interface APIInterface {
    @GET("/api/genre/all")
    Call<GenreResult> getGenreList();

    @GET("/api/movie/company/{region}/{id}/{page}")
    Call<MovieResult> discoverMovieWithCompany(@Path("id") Integer id, @Path("region") String region, @Path("page") Integer page);

    @GET("/api/movie/genre/{region}/{id}/{page}")
    Call<MovieResult> discoverMovieWithGenre(@Path("id") Integer id, @Path("region") String region, @Path("page") Integer page);

    @GET("/api/movie/date/{region}/{date}/{page}")
    Call<MovieResult> discoverMovieWithDate(@Path("date") String date, @Path("region") String region, @Path("page") Integer page);

    @GET("/api/search/multi/{query}/{page}")
    Call<MultiResult> searchMulti(@Path("query") String query, @Path("page") Integer page);

    @GET("/api/search/company/{region}/{query}/{page}")
    Call<CompanyResult> searchCompany(@Path("query") String query, @Path("region") String region, @Path("page") Integer page);

    @GET("/api/search/movie/{region}/{query}/{page}")
    Call<MovieResult> searchMovie(@Path("query") String query, @Path("region") String region, @Path("page")Integer page);

    @GET("/api/movie/detail/{region}/{id}")
    Call<MovieDetail> getMovieDetails(@Path("id") Integer id, @Path("region") String region);

    @GET("/api/movie/TMM/{region}/{page}")
    Call<MovieResult> getThisMonthMovie(@Path("region") String region, @Path("page") Integer page);
}
