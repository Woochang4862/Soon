package com.lusle.android.soon.view.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lusle.android.soon.model.schema.Alarm
import com.lusle.android.soon.model.source.ReleaseAlarmDataSource
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val releaseAlarmDataSource = ReleaseAlarmDataSource(context)
            Log.d(TAG, "onReceive: intent.action : ${intent.action}")
            if (intent.action == null || intent.action.equals("com.lusle.android.soon.ALARM_START")) {
                val alarmId = intent.getIntExtra(AlarmSettingFragment.KEY_ALARM_ID, -1)
                Log.d(TAG, "onReceive: data : $alarmId")
                if (alarmId != -1) {
                    val alarms = releaseAlarmDataSource.alarms.filter { it.pendingIntentID == alarmId }

                    if (alarms.isNotEmpty()) {
                        alarms[0].let{
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
                }
            } else {
                val alarms = releaseAlarmDataSource.alarms
                for (alarm in alarms) {
                    /*boolean alarmUp = (PendingIntent.getBroadcast(context, a.getPendingIntentID(),
                            new Intent(context, AlarmReceiver.class),
                            PendingIntent.FLAG_NO_CREATE) != null);*/

                    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val i = Intent(context, AlarmReceiver::class.java)
                    i.putExtra(AlarmSettingFragment.KEY_ALARM_ID, alarm)
                    i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)

                    var pendingIntent = PendingIntent.getBroadcast(context, alarm.pendingIntentID, i,
                        PendingIntent.FLAG_MUTABLE
                    )

                    if (pendingIntent != null) {
                        am.cancel(pendingIntent)
                    }

                    pendingIntent = PendingIntent.getBroadcast(
                        context,
                        alarm.pendingIntentID,
                        i,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                    am[AlarmManager.RTC_WAKEUP, alarm.milliseconds] = pendingIntent
                }

            }
        }
    }

    companion object {
        val TAG: String = AlarmReceiver::class.java.simpleName
    }
}