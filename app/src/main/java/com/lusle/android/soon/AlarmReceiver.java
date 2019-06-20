package com.lusle.android.soon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lusle.android.soon.Model.Alarm;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm data = (Alarm) intent.getBundleExtra("alarm_info").getSerializable("DATA");
        if (data == null) return;
        Intent serviceIntent = new Intent(context, AlarmService.class);
        Bundle args = new Bundle();
        args.putSerializable("DATA", data);
        serviceIntent.putExtra("alarm_info", args);
        serviceIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }
}