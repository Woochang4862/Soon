package com.lusle.android.soon;

import android.app.Application;

import com.facebook.stetho.Stetho;

import net.danlew.android.joda.JodaTimeAndroid;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        JodaTimeAndroid.init(this);
    }
}
