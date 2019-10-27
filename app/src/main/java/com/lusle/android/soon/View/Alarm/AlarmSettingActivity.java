package com.lusle.android.soon.View.Alarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.android.soon.View.BaseActivity;
import com.lusle.android.soon.Model.Schema.Alarm;
import com.lusle.android.soon.Model.Schema.Movie;
import com.lusle.android.soon.R;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Intent.FLAG_INCLUDE_STOPPED_PACKAGES;

public class AlarmSettingActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    private ImageView poster, closeBtn, deleteBtn;
    private Switch aSwitch;
    private TextView title, releaseDate, date, time;
    private RelativeLayout dateSection, timeSection, hiddenSection;
    private Button saveBtn, weekBtn, mTenDayBtn, pTenDayBtn, mOneDayBtn, pOneDayBtn, mOneHourBtn, pOneHourBtn, mOneMinBtn, pOneMinBtn, AMPMBtn;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", /*Locale.getDefault()*/Locale.KOREAN);
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH시 mm분");
    private Calendar currentCal = Calendar.getInstance();
    private boolean active = true;
    private SharedPreferences mPrefs;
    private Movie movieData;
    private Alarm alarmData;
    private AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);

        mPrefs = getSharedPreferences("alarmPref", Context.MODE_PRIVATE);
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        closeBtn = findViewById(R.id.close_btn);
        saveBtn = findViewById(R.id.save_btn);
        poster = findViewById(R.id.poster);
        title = findViewById(R.id.title);
        releaseDate = findViewById(R.id.release_date);
        dateSection = findViewById(R.id.alarm_date_section);
        date = findViewById(R.id.alarm_date);
        timeSection = findViewById(R.id.alarm_time_section);
        time = findViewById(R.id.alarm_time);
        weekBtn = findViewById(R.id.weekBtn);
        mTenDayBtn = findViewById(R.id.mTenDayBtn);
        pTenDayBtn = findViewById(R.id.pTenDayBtn);
        mOneDayBtn = findViewById(R.id.mOneDayBtn);
        pOneDayBtn = findViewById(R.id.pOneDayBtn);
        mOneHourBtn = findViewById(R.id.mOneHourBtn);
        pOneHourBtn = findViewById(R.id.pOneHourBtn);
        mOneMinBtn = findViewById(R.id.mOneMinBtn);
        pOneMinBtn = findViewById(R.id.pOneMinBtn);
        AMPMBtn = findViewById(R.id.AMPMBtn);


        movieData = (Movie) getIntent().getSerializableExtra("movie_info");
        alarmData = (Alarm) getIntent().getSerializableExtra("alarm_info");
        if (alarmData != null)
            movieData = alarmData.getMovie();
        binding();
        saveBtn.setOnClickListener(v -> save());
        closeBtn.setOnClickListener(v -> finish());
        dateSection.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH), currentCal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        timeSection.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, currentCal.get(Calendar.HOUR_OF_DAY), currentCal.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        });
        weekBtn.setOnClickListener(this);
        mTenDayBtn.setOnClickListener(this);
        pTenDayBtn.setOnClickListener(this);
        mOneDayBtn.setOnClickListener(this);
        pOneDayBtn.setOnClickListener(this);
        mOneHourBtn.setOnClickListener(this);
        pOneHourBtn.setOnClickListener(this);
        mOneMinBtn.setOnClickListener(this);
        pOneMinBtn.setOnClickListener(this);
        AMPMBtn.setOnClickListener(this);
    }

    private void save() {
        alarmData = new Alarm(movieData, currentCal.getTimeInMillis(), alarmData == null ? movieData.hashCode() : alarmData.getPendingIntentID(), active);

        Intent intent = new Intent(this, AlarmReceiver.class);
        Bundle args = new Bundle();
        args.putSerializable("DATA", alarmData);
        intent.putExtra("alarm_info", args);
        intent.addFlags(FLAG_INCLUDE_STOPPED_PACKAGES);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmData.getPendingIntentID(), intent, 0);

        if (pendingIntent != null) {
            am.cancel(pendingIntent);
        }

        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmData.getPendingIntentID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        am.set(AlarmManager.RTC_WAKEUP, currentCal.getTimeInMillis(), pendingIntent);

        Type type = new TypeToken<ArrayList<Alarm>>() {
        }.getType();

        String json = mPrefs.getString("alarms", "");
        ArrayList<Alarm> alarms = new Gson().fromJson(json, type);
        if (alarms == null) alarms = new ArrayList<>();

        alarms.remove(alarmData);
        alarms.add(alarmData);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        String _json = new Gson().toJson(alarms, type);
        prefsEditor.putString("alarms", _json);
        prefsEditor.apply();

        Toast.makeText(this, "알림이 설정되었습니다", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void binding() {
        if (alarmData != null) {
            hiddenSection = findViewById(R.id.hidden_section);
            hiddenSection.setVisibility(View.VISIBLE);
            aSwitch = findViewById(R.id.alarm_switch);
            active = this.alarmData.isActive();
            aSwitch.setChecked(active);
            aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> active = isChecked);
            deleteBtn = findViewById(R.id.delete_btn);
            deleteBtn.setOnClickListener(v -> delete());
        }

        Picasso.get().load("https://image.tmdb.org/t/p/w500" + movieData.getPosterPath()).centerInside().fit().error(R.drawable.ic_broken_image).into(poster);

        if (!movieData.getTitle().equals(""))
            title.setText(movieData.getTitle());

        SimpleDateFormat formatD = new SimpleDateFormat("yyyy-MM-dd");
        if (!movieData.getReleaseDate().equals("")) {
            try {
                releaseDate.setText(dateFormat.format(formatD.parse(movieData.getReleaseDate())));
                if(alarmData == null) {
                    date.setText(dateFormat.format(formatD.parse(movieData.getReleaseDate())));
                    currentCal.setTime(formatD.parse(movieData.getReleaseDate()));
                    currentCal.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                    currentCal.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
                } else {
                    currentCal.setTimeInMillis(alarmData.getMilliseconds());
                    date.setText(dateFormat.format(currentCal.getTime()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        time.setText(timeFormat.format(currentCal.getTime()));
    }

    private void delete() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("삭제");
        alert.setMessage("정말 삭제하시겠습니까?");
        alert.setPositiveButton("Yes", (dialog, which) -> {

            Intent intent = new Intent("com.lusle.android.soon.ALARM_START");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmData.getPendingIntentID(), intent, 0);
            am.cancel(pendingIntent);

            Type type = new TypeToken<ArrayList<Alarm>>() {
            }.getType();
            String json = mPrefs.getString("alarms", "");
            ArrayList<Alarm> alarms = new Gson().fromJson(json, type);
            alarms.remove(this.alarmData);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            String _json = new Gson().toJson(alarms, type);
            prefsEditor.putString("alarms", _json);
            prefsEditor.apply();

            Toast.makeText(this, "알림이 삭제되었습니다", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            finish();
        });

        alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        alert.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        currentCal.set(year, month, dayOfMonth);
        date.setText(dateFormat.format(currentCal.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        currentCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        currentCal.set(Calendar.MINUTE, minute);
        time.setText(timeFormat.format(currentCal.getTime()));
    }

    private void changeDate(int day) {
        currentCal.add(Calendar.DAY_OF_MONTH, day);
        date.setText(dateFormat.format(currentCal.getTime()));
    }

    private void changeTime(int hour, int min) {
        currentCal.add(Calendar.HOUR_OF_DAY, hour);
        currentCal.add(Calendar.MINUTE, min);
        time.setText(timeFormat.format(currentCal.getTime()));
    }

    @Override
    public void onClick(View v) {
        int day = 0, hour = 0, min = 0;
        switch (v.getId()) {
            case R.id.weekBtn:
                day = -7;
                break;
            case R.id.mTenDayBtn:
                day = -10;
                break;
            case R.id.pTenDayBtn:
                day = 10;
                break;
            case R.id.mOneDayBtn:
                day = -1;
                break;
            case R.id.pOneDayBtn:
                day = 1;
                break;
            case R.id.mOneHourBtn:
                hour = -1;
                break;
            case R.id.pOneHourBtn:
                hour = 1;
                break;
            case R.id.mOneMinBtn:
                min = -1;
                break;
            case R.id.pOneMinBtn:
                min = 1;
                break;
            case R.id.AMPMBtn:
                if (0 <= currentCal.get(Calendar.HOUR_OF_DAY) && currentCal.get(Calendar.HOUR_OF_DAY) < 12)
                    hour = 12;
                else
                    hour = -12;
        }
        changeDate(day);
        changeTime(hour, min);
    }
}
