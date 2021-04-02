package com.lusle.android.soon.View.Main.Setting.CompanyAlarm.Presenter;

public class CompanyAlarmPresenter implements CompanyAlarmContract.Presenter {

    private CompanyAlarmContract.View view;

    @Override
    public void attachView(CompanyAlarmContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }
}
