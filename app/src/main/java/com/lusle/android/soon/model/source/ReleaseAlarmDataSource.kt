package com.lusle.android.soon.model.source

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lusle.android.soon.model.schema.Alarm

class ReleaseAlarmDataSource(context: Context) {
    private val pref = context.getSharedPreferences("alarmPref", Context.MODE_PRIVATE)
    private val type = object : TypeToken<ArrayList<Alarm>>() {}.type
    private val gson = Gson()

    var alarms: ArrayList<Alarm>
        get() {
            val json: String = pref.getString("alarms", "") ?: "[]"
            val alarms = gson.fromJson<ArrayList<Alarm>>(json, type)
            alarms?.toString()?.let { Log.d(TAG, it) }
            return alarms ?: ArrayList()
        }
        set(value) {
            val editPref = pref.edit()
            val json = gson.toJson(value, type)
            editPref.putString("alarms", json)
            editPref.apply()
        }

    companion object {
        val TAG: String = ReleaseAlarmDataSource::class.java.simpleName
    }
}