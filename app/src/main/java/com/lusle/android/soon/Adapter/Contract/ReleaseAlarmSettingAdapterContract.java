package com.lusle.android.soon.Adapter.Contract;

import com.lusle.android.soon.Model.Schema.Alarm;

import java.util.ArrayList;

public interface ReleaseAlarmSettingAdapterContract {
    interface View extends BaseAdapterContract.View {

    }

    interface Model extends BaseAdapterContract.Model {
        void setList(ArrayList<Alarm> list);
        Alarm getItem(int position);
    }
}
