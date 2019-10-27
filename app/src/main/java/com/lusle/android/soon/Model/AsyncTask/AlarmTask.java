package com.lusle.android.soon.Model.AsyncTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.android.soon.Model.Schema.Alarm;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AlarmTask extends AsyncTask<Context, Void, ArrayList<Alarm>> {
    @Override
    protected ArrayList<Alarm> doInBackground(Context... contexts) {
        SharedPreferences mPrefs = contexts[0].getSharedPreferences("alarmPref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Alarm>>() {
        }.getType();
        String json = mPrefs.getString("alarms", "");
        ArrayList<Alarm> alarms = gson.fromJson(json, type);
        Log.d("####", alarms+"");
        if (alarms == null) return new ArrayList<>();
        return alarms;
    }
}
