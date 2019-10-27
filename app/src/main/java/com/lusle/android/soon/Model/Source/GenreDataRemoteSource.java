package com.lusle.android.soon.Model.Source;

import com.lusle.android.soon.Model.API.APIClient;
import com.lusle.android.soon.Model.API.APIInterface;
import com.lusle.android.soon.Model.Contract.GenreDataRemoteSourceContract;
import com.lusle.android.soon.Model.Schema.GenreResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreDataRemoteSource implements GenreDataRemoteSourceContract.Model {

    private static GenreDataRemoteSource ourInstance = new GenreDataRemoteSource();

    private OnFinishedListener onFinishedListener;

    public static GenreDataRemoteSource getInstance() {
        return ourInstance;
    }

    private GenreDataRemoteSource(){}

    @Override
    public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
        this.onFinishedListener = onFinishedListener;
    }

    @Override
    public void getGenreList() {
        APIClient.getClient().create(APIInterface.class).getGenreList().enqueue(new Callback<GenreResult>() {
            @Override
            public void onResponse(Call<GenreResult> call, Response<GenreResult> response) {
                if(onFinishedListener != null) onFinishedListener.onFinished(response.body().getGenres());
            }

            @Override
            public void onFailure(Call<GenreResult> call, Throwable throwable) {
                if(onFinishedListener != null) onFinishedListener.onFailure(throwable);
            }
        });
    }
}
