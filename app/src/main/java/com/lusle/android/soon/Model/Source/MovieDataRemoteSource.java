package com.lusle.android.soon.Model.Source;

import com.lusle.android.soon.Model.API.APIClient;
import com.lusle.android.soon.Model.API.APIInterface;
import com.lusle.android.soon.Model.Contract.MovieDataRemoteSourceContract;
import com.lusle.android.soon.Model.Schema.MovieResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDataRemoteSource implements MovieDataRemoteSourceContract.Model {
    private static final MovieDataRemoteSource ourInstance = new MovieDataRemoteSource();
    private OnFinishedListener onFinishedListener;

    public static MovieDataRemoteSource getInstance() {
        return ourInstance;
    }

    private MovieDataRemoteSource() {
    }

    @Override
    public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
        this.onFinishedListener = onFinishedListener;
    }

    @Override
    public void getThisMonthMovieResult(String region, int page) {
        APIClient.getClient().create(APIInterface.class).getThisMonthMovie(region, page).enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                onFinishedListener.onFinished(response.body());
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable throwable) {
                onFinishedListener.onFailure(throwable);
            }
        });
    }

    @Override
    public void discoverMovieWithDate(String region, String date, int page){
        APIClient.getClient().create(APIInterface.class).discoverMovieWithDate(date, region, page).enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                onFinishedListener.onFinished(response.body());
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable throwable) {
                onFinishedListener.onFailure(throwable);
            }
        });
    }
}
