package com.lusle.android.soon.View.Main.Setting.Presenter;

import android.content.Intent;

import com.lusle.android.soon.Adapter.Contract.AlarmSettingAdapterContract;
import com.lusle.android.soon.Adapter.Listener.OnEmptyListener;
import com.lusle.android.soon.Model.Schema.Alarm;
import com.lusle.android.soon.Model.Source.AlarmDataLocalSource;
import com.lusle.android.soon.View.Alarm.AlarmSettingActivity;

import java.util.ArrayList;

public class SettingPresenter implements SettingContact.Presenter {

    private SettingContact.View view;
    private AlarmSettingAdapterContract.View adapterView;
    private AlarmSettingAdapterContract.Model adapterModel;
    private AlarmDataLocalSource model;

    @Override
    public void attachView(SettingContact.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void setAdapterView(AlarmSettingAdapterContract.View adapterView) {
        this.adapterView = adapterView;
    }

    @Override
    public void setAdapterModel(AlarmSettingAdapterContract.Model adapterModel) {
        this.adapterModel = adapterModel;
    }

    @Override
    public void setModel(AlarmDataLocalSource model) {
        this.model = model;
    }

    @Override
    public void setOnItemClickListener() {
        adapterModel.setOnAlarmItemClickListener((v, pos, alarm) -> {
            Intent intent = new Intent(view.getContext(), AlarmSettingActivity.class);
            intent.putExtra("alarm_info", alarm);
            view.getContext().startActivity(intent);
        });
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
