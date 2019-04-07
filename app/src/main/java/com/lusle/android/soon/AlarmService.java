package com.lusle.android.soon;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.android.soon.Model.Alarm;
import com.lusle.android.soon.Utils.Utils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {
    private static final String CHANNEL_ID = "com.lusle.android.soon";

    public AlarmService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            Alarm data = (Alarm) intent.getBundleExtra("alarm_info").getSerializable("DATA");

            if (data.isActive()) {

                Calendar releaseDate = Calendar.getInstance();

                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(data.getMovie().getReleaseDate());
                    releaseDate.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                createNotificationChannel();
                Intent _intent = new Intent(this, MainActivity.class);
                _intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pi = PendingIntent.getActivity(this, 111, _intent, PendingIntent.FLAG_UPDATE_CURRENT);
                long[] pattern = {0, 300, 0};
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.icon)
                        .setContentTitle(data.getMovie().getTitle())
                        .setContentText("개봉일까지 " + Utils.calDDay(releaseDate) + "일 전입니다")
                        .setVibrate(pattern)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setAutoCancel(true);

                mBuilder.setContentIntent(pi);
                mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(data.hashCode(), mBuilder.build());

                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

                Intent __intent = new Intent("com.lusle.android.soon.ALARM_START");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), data.getPendingIntentID(), __intent, 0);
                am.cancel(pendingIntent);

                Type type = new TypeToken<ArrayList<Alarm>>() {
                }.getType();
                String json = mPrefs.getString("alarms", "");
                ArrayList<Alarm> alarms = new Gson().fromJson(json, type);
                alarms.remove(data);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                String _json = new Gson().toJson(alarms, type);
                prefsEditor.putString("alarms", _json);
                prefsEditor.commit();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "movie_alarm_channel";
            String description = "movie_alarm_channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
