package com.lusle.android.soon.Model.Source;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.android.soon.Model.AsyncTask.AlarmTask;
import com.lusle.android.soon.Model.Schema.Alarm;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class AlarmDataLocalSource {
    private static final AlarmDataLocalSource ourInstance = new AlarmDataLocalSource();
    private static Context context;

    public static AlarmDataLocalSource getInstance(Context _context) {
        context = _context;
        return ourInstance;
    }

    private AlarmDataLocalSource() {
    }

    public ArrayList<Alarm> getAlarm() {
        Log.d("####", "AlarmDataLocalSource::getAlarm() called");
        try {
            return new AlarmTask().execute(context).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setAlarm(ArrayList<Alarm> alarms) {
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences("alarmPref", Context.MODE_PRIVATE).edit();
        Type type = new TypeToken<ArrayList<Alarm>>() {
        }.getType();
        String _json = new Gson().toJson(alarms, type);
        prefsEditor.putString("alarms", _json);
        prefsEditor.apply();
    }
}
