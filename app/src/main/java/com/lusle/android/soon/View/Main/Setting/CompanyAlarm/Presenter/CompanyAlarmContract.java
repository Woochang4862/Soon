package com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter;

import android.content.Context;

public interface CompanyAlarmContract {
    public interface View {
        Context getContext();
    }
    public interface Presenter {
        void attachView(View view);

        void detachView();
    }
}
