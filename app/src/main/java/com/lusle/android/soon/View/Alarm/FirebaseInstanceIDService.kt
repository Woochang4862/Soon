package com.lusle.android.soon.View.Alarm

import android.app.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lusle.android.soon.R
import android.os.Build
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lusle.android.soon.Model.Schema.Company
import com.lusle.android.soon.View.Main.MainActivity
import com.lusle.android.soon.View.MovieList.MovieListActivity

class FirebaseInstanceIDService : FirebaseMessagingService() {
    override fun startForegroundService(service: Intent?): ComponentName? {
        return super.startForegroundService(service)
    }
    /**
     * 구글 토큰을 얻는 값입니다.
     * 아래 토큰은 앱이 설치된 디바이스에 대한 고유값으로 푸시를 보낼때 사용됩니다.
     */
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        Log.d("Firebase", "FirebaseInstanceIDService : $s")
    }

    /**
     * 메세지를 받았을 경우 그 메세지에 대하여 구현하는 부분입니다.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: " + remoteMessage.data)
        Log.d(TAG, "onMessageReceived: ${remoteMessage.to}")
        if (remoteMessage.to == "/topics/all") {
            if (PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(getString(R.string.key_monthly_alarm), true)
            ) sendNotification(remoteMessage)
        } else {
            sendNotification(remoteMessage)
        }
    }

    /**
     * remoteMessage 메세지 안애 getData 와 getNotification 이 있습니다.
     * 이부분은 차후 테스트 날릴때 설명 드리겠습니다.
     */
    private fun sendNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["body"]
        val notificationId = System.currentTimeMillis().toInt()
        var intent: Intent? = null
        Log.d(TAG, "sendNotification: ${remoteMessage.data["company"]}")
        remoteMessage.data["company"]?.let { company ->
            val type = object : TypeToken<Company>() {}.type
            val companyObj = Gson().fromJson<Company>(
                company,
                type
            )
            intent = Intent(this, MovieListActivity::class.java)
            intent!!.putExtra("keyword", companyObj)
            Log.d(TAG, "sendNotification: ${companyObj.id}")
        } ?: run {
            intent = Intent(this, MainActivity::class.java)
            Log.d(TAG, "sendNotification: Main")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        /**
         * 오레오 버전부터는 Notification Channel 이 없으면 푸시가 생성되지 않는 현상이 있습니다.
         */
        val channel = "TMM_movie_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "TMM_movie_channel"
            val notificationChannel = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channelMessage = NotificationChannel(
                channel, channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channelMessage.description = "채널에 대한 설명."
            channelMessage.enableLights(true)
            channelMessage.enableVibration(true)
            channelMessage.setShowBadge(false)
            channelMessage.vibrationPattern = longArrayOf(100, 200, 100, 200)
            notificationChannel.createNotificationChannel(channelMessage)
            val notificationBuilder = NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setChannelId(channel)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notificationBuilder.build())
        } else {
            val notificationBuilder = NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(notificationId, notificationBuilder.build())
        }
    }

    companion object {
        val TAG: String = FirebaseInstanceIDService::class.java.simpleName
    }
}