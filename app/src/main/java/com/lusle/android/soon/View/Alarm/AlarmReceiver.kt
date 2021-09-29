package com.lusle.android.soon.View.Alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lusle.android.soon.Model.Schema.Alarm
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { context ->
            intent?.let { _intent ->
                Log.d("####", "onReceive: intent.action : ${_intent.action}")
                if (_intent.action == null || _intent.action.equals("com.lusle.android.soon.ALARM_START")) {
                    val alarmId = _intent.getIntExtra(AlarmSettingFragment.KEY_ALARM_ID, -1)
                    Log.d("####", "onReceive: data : $alarmId")
                    if (alarmId == -1) {

                    } else {
                        val preferences = context.getSharedPreferences("alarmPref", Context.MODE_PRIVATE)
                        val type = object : TypeToken<ArrayList<Alarm>>() {}.type
                        val json = preferences.getString("alarms", "")
                        var alarms = Gson().fromJson<ArrayList<Alarm>>(json, type)
                        if (alarms == null) alarms = ArrayList()
                        var data: Alarm? = null
                        for (alarm in alarms){
                            if(alarm.pendingIntentID == alarmId)
                                data = alarm
                        }
                        data?.let {
                            val serviceIntent = Intent(context, AlarmService::class.java)
                            serviceIntent.putExtra("alarm_info", it)
                            serviceIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(serviceIntent)
                            } else {
                                context.startService(serviceIntent)
                            }
                        }
                    }
                } else {
                    val preferences = context.getSharedPreferences("alarmPref", Context.MODE_PRIVATE)
                    val type = object : TypeToken<ArrayList<Alarm>>() {}.type
                    val json = preferences.getString("alarms", "")
                    var alarms = Gson().fromJson<ArrayList<Alarm>>(json, type)
                    if (alarms == null) alarms = ArrayList()
                    for (a in alarms) {
                        /*boolean alarmUp = (PendingIntent.getBroadcast(context, a.getPendingIntentID(),
                                new Intent(context, AlarmReceiver.class),
                                PendingIntent.FLAG_NO_CREATE) != null);*/

                        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val i = Intent(context, AlarmReceiver::class.java)
                        i.putExtra(AlarmSettingFragment.KEY_ALARM_ID, a)
                        i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)

                        var pendingIntent = PendingIntent.getBroadcast(context, a.pendingIntentID, i, 0)

                        if (pendingIntent != null) {
                            am.cancel(pendingIntent)
                        }

                        pendingIntent = PendingIntent.getBroadcast(context, a.pendingIntentID, i, PendingIntent.FLAG_UPDATE_CURRENT)

                        am[AlarmManager.RTC_WAKEUP, a.milliseconds] = pendingIntent
                    }

                }
            }
        }
    }
}