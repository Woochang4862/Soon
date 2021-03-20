package com.lusle.android.soon.Model.Source;

import android.content.Context;

import com.lusle.android.soon.Model.AsyncTask.FavoriteCompanyTask;
import com.lusle.android.soon.Model.Schema.Company;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FavoriteCompanyDataLocalSource {
    private static final FavoriteCompanyDataLocalSource ourInstance = new FavoriteCompanyDataLocalSource();

    public static FavoriteCompanyDataLocalSource getInstance() {
        return ourInstance;
    }

    private FavoriteCompanyDataLocalSource() {
    }

    public ArrayList<Company> getFavoriteCompany(Context context) {
        ArrayList<Company> companyList = new ArrayList<>();
        try {
            companyList.addAll(new FavoriteCompanyTask().execute(context).get());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return companyList;
    }
}
