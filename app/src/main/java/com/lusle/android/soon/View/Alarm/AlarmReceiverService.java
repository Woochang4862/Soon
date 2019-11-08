package com.lusle.android.soon.View.Alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class AlarmReceiverService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        Intent intent = new Intent("com.lusle.android.soon.ALARM_START");

    }
}
