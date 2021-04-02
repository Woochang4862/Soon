package com.lusle.android.soon.View.Main.Setting.ReleaseAlarm.Presenter;

import android.content.Context;

import com.lusle.android.soon.Adapter.Contract.AlarmSettingAdapterContract;
import com.lusle.android.soon.Model.Schema.Alarm;
import com.lusle.android.soon.Model.Source.AlarmDataLocalSource;

import java.util.ArrayList;

public interface ReleaseAlarmContact {
    interface View {

        void setRecyclerEmpty(boolean isEmpty);

        Context getContext();
    }

    interface Presenter {
        void attachView(View view);

        void detachView();

        void setAdapterView(AlarmSettingAdapterContract.View adapterView);

        void setAdapterModel(AlarmSettingAdapterContract.Model adapterModel);

        void setModel(AlarmDataLocalSource model);

        void setOnItemClickListener();

        void setOnEmptyListener();

        void loadItems();

        ArrayList<Alarm> getAlarms();

        void setAlarms(ArrayList<Alarm> alarms);
    }
}
