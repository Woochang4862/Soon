package com.lusle.android.soon.Receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.android.soon.Service.AlarmService;
import com.lusle.android.soon.Model.Alarm;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Intent.FLAG_INCLUDE_STOPPED_PACKAGES;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            Type type = new TypeToken<ArrayList<Alarm>>() {
            }.getType();

            String json = preferences.getString("alarms", "");
            ArrayList<Alarm> alarms = new Gson().fromJson(json, type);
            if (alarms == null) alarms = new ArrayList<>();

            for (Alarm a : alarms) {
                boolean alarmUp = (PendingIntent.getBroadcast(context, a.getPendingIntentID(),
                        new Intent(context, AlarmReceiver.class),
                        PendingIntent.FLAG_NO_CREATE) != null);
                if (alarmUp) {
                    Toast.makeText(context, "Alarm is already active", Toast.LENGTH_SHORT).show();
                } else {
                    AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                    Toast.makeText(context, "Alarm is now active", Toast.LENGTH_SHORT).show();
                    IntentFilter intentFilter = new IntentFilter("com.lusle.android.soon.ALARM_START");
                    context.getApplicationContext().registerReceiver(new AlarmReceiver(), intentFilter);
                    Intent i = new Intent(context, AlarmReceiver.class);
                    Bundle args = new Bundle();
                    args.putSerializable("DATA", a);
                    i.putExtra("alarm_info", args);
                    i.addFlags(FLAG_INCLUDE_STOPPED_PACKAGES);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, a.getPendingIntentID(), i, 0);

                    if (pendingIntent != null) {
                        am.cancel(pendingIntent);
                    }

                    pendingIntent = PendingIntent.getBroadcast(context, a.getPendingIntentID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), pendingIntent);
                }
            }
        } else {
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
}