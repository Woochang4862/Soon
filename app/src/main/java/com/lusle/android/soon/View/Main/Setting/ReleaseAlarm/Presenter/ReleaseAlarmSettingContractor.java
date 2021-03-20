package com.lusle.android.soon.View.Main.Setting.ReleaseAlarm.Presenter;

import android.content.Context;

import com.lusle.android.soon.Adapter.Contract.ReleaseAlarmSettingAdapterContract;
import com.lusle.android.soon.Model.Schema.Alarm;
import com.lusle.android.soon.Model.Source.AlarmDataLocalSource;

import java.util.ArrayList;

public interface ReleaseAlarmSettingContractor {
    interface View {

        void setRecyclerEmpty(boolean isEmpty);

        Context getContext();
    }

    interface Presenter {
        void attachView(View view);

        void detachView();

        void setAdapterView(ReleaseAlarmSettingAdapterContract.View adapterView);

        void setAdapterModel(ReleaseAlarmSettingAdapterContract.Model adapterModel);

        void setModel(AlarmDataLocalSource model);

        void setOnItemClickListener();

        void setOnEmptyListener();

        void loadItems();

        ArrayList<Alarm> getAlarms();

        void setAlarms(ArrayList<Alarm> alarms);
    }
}
