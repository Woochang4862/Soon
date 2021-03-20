package com.lusle.android.soon.Model.Source;

import com.lusle.android.soon.Model.API.APIClient;
import com.lusle.android.soon.Model.API.APIInterface;
import com.lusle.android.soon.Model.Contract.SubscribeCheckDataRemoteSourceContract;
import com.lusle.android.soon.Model.Schema.SubscribeCheckResult;
import com.lusle.android.soon.Model.Schema.SubscribedTopicsResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscribeCheckDataRemoteSource implements SubscribeCheckDataRemoteSourceContract.Model{
    private static final SubscribeCheckDataRemoteSource ourInstance = new SubscribeCheckDataRemoteSource();
    private SubscribeCheckDataRemoteSourceContract.Model.OnFinishedListener onFinishedListener;

    public static SubscribeCheckDataRemoteSource getInstance() {
        return ourInstance;
    }

    private SubscribeCheckDataRemoteSource() {
    }

    @Override
    public void setOnFinishedListener(SubscribeCheckDataRemoteSourceContract.Model.OnFinishedListener onFinishedListener) {
        this.onFinishedListener = onFinishedListener;
    }

    @Override
    public void checkSubscribe(String token, String topic) {
        APIClient.getClient().create(APIInterface.class).checkSubscribeTo(token, topic).enqueue(new Callback<SubscribeCheckResult>() {
            @Override
            public void onResponse(Call<SubscribeCheckResult> call, Response<SubscribeCheckResult> response) {

            }

            @Override
            public void onFailure(Call<SubscribeCheckResult> call, Throwable throwable) {
                onFinishedListener.onFailure(throwable);
            }
        });
    }

    @Override
    public void checkSubscribedTopics(String token) {
        APIClient.getClient().create(APIInterface.class).checkSubscribedTopics(token).enqueue(new Callback<SubscribedTopicsResult>() {
            @Override
            public void onResponse(Call<SubscribedTopicsResult> call, Response<SubscribedTopicsResult> response) {
                onFinishedListener.onFinished(response.body().getTopics());
            }

            @Override
            public void onFailure(Call<SubscribedTopicsResult> call, Throwable t) {
                onFinishedListener.onFailure(t);
            }
        });
    }
}
