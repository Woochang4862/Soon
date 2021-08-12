package com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter;

import com.lusle.android.soon.Adapter.Contract.CompanyAlarmSettingAdapterContract;
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Model.Contract.CompanyAlarmManagerRemoteSourceContract;
import com.lusle.android.soon.Model.Contract.SubscribeCheckDataRemoteSourceContract;
import com.lusle.android.soon.Model.Schema.Company;
import com.lusle.android.soon.Model.Source.CompanyAlarmManagerRemoteSource;
import com.lusle.android.soon.Model.Source.FavoriteCompanyDataLocalSource;
import com.lusle.android.soon.Model.Source.SubscribeCheckDataRemoteSource;

import java.util.ArrayList;

public class CompanyAlarmSettingPresenter implements CompanyAlarmSettingContractor.Presenter {
    private CompanyAlarmSettingContractor.View view;
    private CompanyAlarmSettingAdapterContract.View adapterView;
    private CompanyAlarmSettingAdapterContract.Model adapterModel;
    private FavoriteCompanyDataLocalSource favoriteCompanyModel;
    private SubscribeCheckDataRemoteSource subscribeCheckModel;
    private CompanyAlarmManagerRemoteSource companyAlarmManagerModel;

    @Override
    public void attachView(CompanyAlarmSettingContractor.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void setAdapterView(CompanyAlarmSettingAdapterContract.View adapterView) {
        this.adapterView = adapterView;
    }

    @Override
    public void setAdapterModel(CompanyAlarmSettingAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public void setModel(FavoriteCompanyDataLocalSource model) {
        this.favoriteCompanyModel = model;
    }

    @Override
    public void setModel(SubscribeCheckDataRemoteSource model) {
        this.subscribeCheckModel = model;
    }

    @Override
    public void setModel(CompanyAlarmManagerRemoteSource model) {
        this.companyAlarmManagerModel = model;
    }

    @Override
    public void setOnEmptyListener() {
        adapterModel.setOnEmptyListener(new OnEmptyListener() {
            @Override
            public void onEmpty() {
                view.setRecyclerEmpty(true);
            }

            @Override
            public void onNotEmpty() {
                view.setRecyclerEmpty(false);
            }
        });
    }

    @Override
    public void loadItems() {
        if (adapterModel != null)
            adapterModel.setList(getAlarms());
    }

    @Override
    public ArrayList<Company> getAlarms() {
        return favoriteCompanyModel.getFavoriteCompany(view.getContext());
    }

    @Override
    public void checkSubscribedTopics(String token) {
        subscribeCheckModel.checkSubscribedTopics(token);
    }

    @Override
    public void addCompanyAlarm(String token, String company_id) {
        companyAlarmManagerModel.addCompanyAlarm(token, company_id);
    }

    @Override
    public void removeCompanyAlarm(String token, String company_id) {
        companyAlarmManagerModel.removeCompanyAlarm(token, company_id);
    }

    @Override
    public void setTopics(ArrayList<String> topics) {
        adapterModel.setTopics(topics);
    }

    @Override
    public ArrayList<String> getTopics() {
        return adapterModel.getTopics();
    }

    @Override
    public void setOnFinishedListener(SubscribeCheckDataRemoteSourceContract.Model.OnFinishedListener onFinishedListener) {
        subscribeCheckModel.setOnFinishedListener(onFinishedListener);
    }

    @Override
    public void setOnFinishedListener(CompanyAlarmManagerRemoteSourceContract.Model.OnFinishedListener onFinishedListener) {
        companyAlarmManagerModel.setOnFinishedListener(onFinishedListener);
    }
}
