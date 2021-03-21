package com.lusle.android.soon.View.Main.Setting.ReleaseAlarm.Presenter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.lusle.android.soon.Adapter.Contract.ReleaseAlarmSettingAdapterContract;
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Adapter.Listener.OnItemClickListener;
import com.lusle.android.soon.Model.Schema.Alarm;
import com.lusle.android.soon.Model.Source.AlarmDataLocalSource;
import com.lusle.android.soon.R;
import com.lusle.android.soon.View.Alarm.AlarmSettingFragment;

import java.util.ArrayList;

import androidx.navigation.fragment.FragmentKt;

public class ReleaseAlarmSettingPresenter implements ReleaseAlarmSettingContractor.Presenter {

    private ReleaseAlarmSettingContractor.View view;
    private ReleaseAlarmSettingAdapterContract.View adapterView;
    private ReleaseAlarmSettingAdapterContract.Model adapterModel;
    private AlarmDataLocalSource model;

    @Override
    public void attachView(ReleaseAlarmSettingContractor.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void setAdapterView(ReleaseAlarmSettingAdapterContract.View adapterView) {
        this.adapterView = adapterView;
    }

    @Override
    public void setAdapterModel(ReleaseAlarmSettingAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public void setModel(AlarmDataLocalSource model) {
        this.model = model;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        adapterModel.setOnItemClickListener(onItemClickListener);
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
        adapterModel.setList(getAlarms());
    }

    @Override
    public ArrayList<Alarm> getAlarms() {
        return model.getAlarm();
    }

    @Override
    public void setAlarms(ArrayList<Alarm> alarms) {
        model.setAlarm(alarms);
    }
}
