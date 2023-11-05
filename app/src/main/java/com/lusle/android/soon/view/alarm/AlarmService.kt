package com.lusle.android.soon.view.alarm

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.util.Pair
import androidx.core.app.NotificationCompat
import com.lusle.android.soon.R
import com.lusle.android.soon.model.schema.Alarm
import com.lusle.android.soon.model.source.ReleaseAlarmDataSource
import com.lusle.android.soon.util.Utils
import com.lusle.android.soon.view.detail.DetailActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar

class AlarmService : Service() {
    private var intent: Intent? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        this.intent = intent
        Log.d(TAG, "onStartCommand: called")
        val p: Pair<Int, Notification> = startForegroundService()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(p.first, p.second)
            stopForeground(STOP_FOREGROUND_DETACH) // To remove notification
        } else {
            val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.notify(p.first, p.second)
        }
        return START_STICKY
    }

    private fun startForegroundService(): Pair<Int, Notification> {
        Log.d(TAG, "startForegroundService: called")
        intent?.let{
            val alarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializableExtra("alarm_info", Alarm::class.java)
            } else {
                it.getSerializableExtra("alarm_info") as Alarm
            }
            if (alarm != null && alarm.isActive) {
                // 개봉일 변수 선언
                var releaseDate:Calendar? = Calendar.getInstance()
                try {
                    val date = SimpleDateFormat("yyyy-MM-dd").parse(alarm.movie.releaseDate)
                    date?.let{releaseDate!!.time = date} ?: run {releaseDate=null}
                } catch (e: ParseException) {
                    e.printStackTrace()
                    releaseDate = null
                }

                // 알람 메니저에서 알람 삭제
                val am = getSystemService(ALARM_SERVICE) as AlarmManager
                val i = Intent(this, AlarmReceiver::class.java).apply {
                    action = "com.lusle.android.soon.ALARM_START"
                }
                val pendingIntent =
                    PendingIntent.getBroadcast(applicationContext, alarm.pendingIntentID, i,
                        PendingIntent.FLAG_IMMUTABLE)
                if (pendingIntent != null)
                    am.cancel(pendingIntent)

                // 알람 목록에서 제외
                val releaseAlarmDataSource = ReleaseAlarmDataSource(applicationContext)
                val alarms = releaseAlarmDataSource.alarms
                if (alarms.contains(alarm))
                    alarms.remove(alarm)
                releaseAlarmDataSource.alarms = alarms
                Log.d(TAG, "startForegroundService: $alarms 에서 $alarm 가 제외되었습니다")

                // Notification Action 설정
                val detailActivityIntent = Intent(this, DetailActivity::class.java)
                detailActivityIntent.putExtra("movie_id", alarm.movie.id)
                detailActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                val pi =
                    PendingIntent.getActivity(this, 111, detailActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val pattern = longArrayOf(0, 1000, 0)
                val bigText = NotificationCompat.BigTextStyle()
                bigText.bigText("자세히 보기")
                bigText.setBigContentTitle(alarm.movie.title + "개봉일까지 " + Utils.calDDay(releaseDate) + "일 전입니다")
                bigText.setSummaryText("Soon")
                val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.icon)
                    .setContentTitle(alarm.movie.title)
                    .setContentText("개봉일까지 " + Utils.calDDay(releaseDate) + "일 전입니다")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setVibrate(pattern)
                    .setOngoing(false)
                    .setStyle(bigText)
                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                createNotificationChannel()
                return Pair(alarm.hashCode(), mBuilder.build())
            }
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Pair(404, Notification.Builder(this, CHANNEL_ID).build())
        } else {
            Pair(404, Notification.Builder(this).build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Soon - Release Alarm"
            val description = "Remind your alarm that movie will release \"Soon\"!"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "com.lusle.android.soon.RELEASE_ALARM"
        val TAG: String = AlarmService::class.java.simpleName
    }
}
