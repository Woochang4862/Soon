package com.lusle.android.soon.Model.Source;

import com.lusle.android.soon.Model.API.APIClient;
import com.lusle.android.soon.Model.API.APIInterface;
import com.lusle.android.soon.Model.Contract.CompanyAlarmManagerRemoteSourceContract;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompanyAlarmManagerRemoteSource implements CompanyAlarmManagerRemoteSourceContract.Model {
    private static final CompanyAlarmManagerRemoteSource ourInstance = new CompanyAlarmManagerRemoteSource();
    private OnFinishedListener onFinishedListener;

    public static CompanyAlarmManagerRemoteSource getInstance() {
        return ourInstance;
    }

    private CompanyAlarmManagerRemoteSource() {
    }

    @Override
    public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
        this.onFinishedListener = onFinishedListener;
    }

    @Override
    public void addCompanyAlarm(String token, String company_id) {
        HashMap<String, String> body = new HashMap<>();
        body.put("company_id", company_id);
        body.put("token", token);
        APIClient.getClient().create(APIInterface.class).addCompanyAlarm(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.code() == 200)
                    if (onFinishedListener != null)
                        onFinishedListener.onFinished();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (onFinishedListener != null)
                    onFinishedListener.onFailure(t);
            }
        });
    }

    @Override
    public void removeCompanyAlarm(String token, String company_id) {
        HashMap<String, String> body = new HashMap<>();
        body.put("company_id", company_id);
        body.put("token", token);
        APIClient.getClient().create(APIInterface.class).removeCompanyAlarm(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.code() == 200)
                    if (onFinishedListener != null)
                        onFinishedListener.onFinished();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (onFinishedListener != null)
                    onFinishedListener.onFailure(t);
            }
        });
    }
}
