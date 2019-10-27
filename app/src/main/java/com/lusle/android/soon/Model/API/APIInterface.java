package com.lusle.android.soon.Model.API;

import com.lusle.android.soon.Model.Schema.CompanyResult;
import com.lusle.android.soon.Model.Schema.GenreResult;
import com.lusle.android.soon.Model.Schema.MovieDetail;
import com.lusle.android.soon.Model.Schema.MovieResult;
import com.lusle.android.soon.Model.Schema.MultiResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIInterface {
    @GET("/api/genre/all")
    Call<GenreResult> getGenreList();

    @GET("/api/movie/company/{id}/{page}")
    Call<MovieResult> discoverMovieWithCompany(@Path("id") Integer id, @Path("page") Integer page);

    @GET("/api/movie/genre/{id}/{page}")
    Call<MovieResult> discoverMovieWithGenre(@Path("id") Integer id, @Path("page") Integer page);

    @GET("/api/movie/date/{date}/{page}")
    Call<MovieResult> discoverMovieWithDate(@Path("date") String date, @Path("page") Integer page);

    @GET("/api/search/multi/{query}/{page}")
    Call<MultiResult> searchMulti(@Path("query") String query, @Path("page") Integer page);

    @GET("/api/search/company/{query}/{page}")
    Call<CompanyResult> searchCompany(@Path("query") String query, @Path("page") Integer page);

    @GET("/api/search/movie/{query}/{page}")
    Call<MovieResult> searchMovie(@Path("query") String query, @Path("page")Integer page);

    @GET("/api/movie/detail/{id}")
    Call<MovieDetail> getMovieDetails(@Path("id") Integer id);

    @GET("/api/movie/TMM/{page}")
    Call<MovieResult> getThisMonthMovie(@Path("page") Integer page);
}
