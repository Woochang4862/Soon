package com.lusle.android.soon.Model.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.android.soon.Model.Schema.Company;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class FavoriteCompanyTask extends AsyncTask<Context, Void, ArrayList<Company>> {
    @Override
    protected ArrayList<Company> doInBackground(Context... contexts) {
        SharedPreferences pref = contexts[0].getSharedPreferences("pref", MODE_PRIVATE);
        String list = pref.getString("favorite_company", "");
        Type type = new TypeToken<ArrayList<Company>>() {
        }.getType();
        ArrayList<Company> companyList = new Gson().fromJson(list, type);
        return companyList;
    }
}
