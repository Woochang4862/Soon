package com.lusle.android.soon.Model.Contract;

public interface CompanyAlarmManagerRemoteSourceContract {
    interface Model {

        interface OnFinishedListener {
            void onFinished();

            void onFailure(Throwable t);
        }

        void setOnFinishedListener(OnFinishedListener onFinishedListener);

        void addCompanyAlarm(String token, String topic);

        void removeCompanyAlarm(String token, String topic);
    }
}
