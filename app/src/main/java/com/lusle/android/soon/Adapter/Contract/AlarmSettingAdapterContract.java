package com.lusle.android.soon.Adapter.Contract;

import com.lusle.android.soon.Adapter.Listener.OnAlarmItemClickListener;
import com.lusle.android.soon.Model.Schema.Alarm;

import java.util.ArrayList;

public interface AlarmSettingAdapterContract {
    interface View extends BaseAdapterContract.View {

    }

    interface Model extends BaseAdapterContract.Model {
        void setOnAlarmItemClickListener(OnAlarmItemClickListener onItemClickListener);

        void setList(ArrayList<Alarm> list);

        Alarm getItem(int pos);
    }
}
