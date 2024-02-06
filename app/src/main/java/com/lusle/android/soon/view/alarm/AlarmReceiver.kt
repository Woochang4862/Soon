package com.lusle.android.soon.view.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.lusle.android.soon.model.schema.Alarm
import com.lusle.android.soon.model.source.ReleaseAlarmDataSource

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val releaseAlarmDataSource = ReleaseAlarmDataSource(context)
            Log.d(TAG, "onReceive: intent.action : ${intent.action}")

            if ( intent.action.equals("com.lusle.android.soon.ALARM_START")) {
                val alarmId = intent.getIntExtra(AlarmSettingFragment.KEY_ALARM_ID, -1)
                if (alarmId == -1) {
                    Log.w(TAG, "onReceive: alarm data must not be null. so this alarm is passed.")
                    return
                }

                val alarm = releaseAlarmDataSource.alarms.filter { it.pendingIntentID == alarmId }[0]
                Log.d(TAG, "onReceive: data : $alarm")

                val serviceIntent = Intent(context, AlarmService::class.java)
                serviceIntent.putExtra("alarm_info", alarm)
                serviceIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            } else if (intent.action.equals("android.intent.action.BOOT_COMPLETED")) {
                val alarms = releaseAlarmDataSource.alarms
                Log.d(TAG, "onReceive: $alarms")
                for (alarm in alarms) {
                    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val i = Intent(context, AlarmReceiver::class.java).apply {
                        action = "com.lusle.android.soon.ALARM_START"
                        putExtra(AlarmSettingFragment.KEY_ALARM_ID, alarm.pendingIntentID)
                        addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                    }

                    var pendingIntent = PendingIntent.getBroadcast(
                        context,
                        alarm.pendingIntentID,
                        i,
                        PendingIntent.FLAG_IMMUTABLE
                    )

                    Log.d(TAG, "onReceive: $pendingIntent")
                    if (pendingIntent != null) {
                        Log.d(TAG, "onReceive: pendingIntent was canceled!")
                        am.cancel(pendingIntent)
                    }

                    pendingIntent = PendingIntent.getBroadcast(
                        context,
                        alarm.pendingIntentID,
                        i,
                        PendingIntent.FLAG_IMMUTABLE // TODO : UPDATE_CURRENT
                    )
                    try {
                        am.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            alarm.milliseconds,
                            pendingIntent
                        )
                    } catch (e: SecurityException) {
                        throw e
                    }
                }

            }
        }
    }

    companion object {
        val TAG: String = AlarmReceiver::class.java.simpleName
    }
}