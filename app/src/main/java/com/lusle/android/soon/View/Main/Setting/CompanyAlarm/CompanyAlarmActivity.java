package com.lusle.android.soon.View.Main.Setting.CompanyAlarm;

import android.content.Context;
import android.os.Bundle;

import com.lusle.android.soon.R;
import com.lusle.android.soon.View.BaseActivity;
import com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter.CompanyAlarmContract;

public class CompanyAlarmActivity extends BaseActivity implements CompanyAlarmContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_alarm);
    }


    @Override
    public Context getContext() {
        return this;
    }
}
