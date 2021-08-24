package com.lusle.android.soon.View.Alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AppRemovedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { intent ->
            Log.d("####", "onReceive : ${intent.action}")
            intent.action?.let { action ->
                if (action == "android.intent.action.PACKAGE_REMOVED") {
                    context?.let {
                        if (intent.data?.schemeSpecificPart == context.packageName) {
                            val serviceIntent = Intent(context, AllCompanyAlarmRemoveService::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(serviceIntent)
                            } else {
                                context.startService(serviceIntent)
                            }
                        }
                    }
                }
            }
        }
    }
}