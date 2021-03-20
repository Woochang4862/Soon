package com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter;

import android.content.Context;

import com.lusle.android.soon.Adapter.Contract.CompanyAlarmSettingAdapterContract;
import com.lusle.android.soon.Model.Contract.CompanyAlarmManagerRemoteSourceContract;
import com.lusle.android.soon.Model.Contract.SubscribeCheckDataRemoteSourceContract;
import com.lusle.android.soon.Model.Schema.Company;
import com.lusle.android.soon.Model.Source.CompanyAlarmManagerRemoteSource;
import com.lusle.android.soon.Model.Source.FavoriteCompanyDataLocalSource;
import com.lusle.android.soon.Model.Source.SubscribeCheckDataRemoteSource;

import java.util.ArrayList;

public interface CompanyAlarmSettingContractor {
    interface View {

        void setRecyclerEmpty(boolean isEmpty);

        Context getContext();

        void showDialog(boolean show);
    }

    interface Presenter {
        void attachView(View view);

        void detachView();

        void setAdapterView(CompanyAlarmSettingAdapterContract.View adapterView);

        void setAdapterModel(CompanyAlarmSettingAdapterContract.Model adapterModel);

        void setOnEmptyListener();

        void loadItems();

        void setModel(FavoriteCompanyDataLocalSource model);

        void setModel(SubscribeCheckDataRemoteSource model);

        void setModel(CompanyAlarmManagerRemoteSource model);

        ArrayList<Company> getAlarms();

        void setOnFinishedListener(SubscribeCheckDataRemoteSourceContract.Model.OnFinishedListener onFinishedListener);

        void setOnFinishedListener(CompanyAlarmManagerRemoteSourceContract.Model.OnFinishedListener onFinishedListener);

        void checkSubscribedTopics(String token);

        void addCompanyAlarm(String token, String topic);

        void removeCompanyAlarm(String token, String topic);
    }
}
