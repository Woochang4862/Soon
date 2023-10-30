package com.lusle.android.soon.model.api;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface {
    @FormUrlEncoded
    @POST("/api/alarm/remove/alarm/company")
    Call<ResponseBody> removeCompanyAlarm(@FieldMap HashMap<String, String> body);
}
