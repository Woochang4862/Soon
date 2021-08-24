package com.lusle.android.soon.View.Alarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.android.soon.Model.Schema.Alarm;
import com.lusle.android.soon.Model.Schema.Movie;
import com.lusle.android.soon.R;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Intent.FLAG_INCLUDE_STOPPED_PACKAGES;
import static androidx.navigation.fragment.FragmentKt.findNavController;

public class AlarmSettingFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    private ImageView poster, closeBtn, deleteBtn;
    private SwitchMaterial aSwitch;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPrefs = requireContext().getSharedPreferences("alarmPref", Context.MODE_PRIVATE);
        am = (AlarmManager) requireContext().getSystemService(ALARM_SERVICE);

        closeBtn = view.findViewById(R.id.close_btn);
        saveBtn = view.findViewById(R.id.save_btn);
        poster = view.findViewById(R.id.poster);
        title = view.findViewById(R.id.title);
        releaseDate = view.findViewById(R.id.release_date);
        dateSection = view.findViewById(R.id.alarm_date_section);
        date = view.findViewById(R.id.alarm_date);
        timeSection = view.findViewById(R.id.alarm_time_section);
        time = view.findViewById(R.id.alarm_time);
        weekBtn = view.findViewById(R.id.weekBtn);
        mTenDayBtn = view.findViewById(R.id.mTenDayBtn);
        pTenDayBtn = view.findViewById(R.id.pTenDayBtn);
        mOneDayBtn = view.findViewById(R.id.mOneDayBtn);
        pOneDayBtn = view.findViewById(R.id.pOneDayBtn);
        mOneHourBtn = view.findViewById(R.id.mOneHourBtn);
        pOneHourBtn = view.findViewById(R.id.pOneHourBtn);
        mOneMinBtn = view.findViewById(R.id.mOneMinBtn);
        pOneMinBtn = view.findViewById(R.id.pOneMinBtn);
        AMPMBtn = view.findViewById(R.id.AMPMBtn);
        hiddenSection = view.findViewById(R.id.hidden_section);
        aSwitch = view.findViewById(R.id.alarm_switch);
        deleteBtn = view.findViewById(R.id.delete_btn);


        movieData = (Movie) requireArguments().getSerializable("movie_info");
        alarmData = (Alarm) requireArguments().getSerializable("alarm_info");
        if (alarmData != null)
            movieData = alarmData.getMovie();
        binding();
        saveBtn.setOnClickListener(v -> save());
        closeBtn.setOnClickListener(v -> findNavController(this).navigateUp());
        dateSection.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, currentCal.get(Calendar.YEAR), currentCal.get(Calendar.MONTH), currentCal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });
        timeSection.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK, this, currentCal.get(Calendar.HOUR_OF_DAY), currentCal.get(Calendar.MINUTE), false);
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
        TimeZone tz = currentCal.getTimeZone();
        DateTimeZone jodaTz = DateTimeZone.forID(tz.getID());
        DateTime dateTime = new DateTime(currentCal.getTimeInMillis(), jodaTz);
        DateTime now = DateTime.now();
        long diff = now.getMillis() - dateTime.getMillis();
        if (diff >= 0) {
            Toast.makeText(requireContext(), "현재 시간 이후로 설정해 주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        alarmData = new Alarm(movieData, currentCal.getTimeInMillis(), alarmData == null ? movieData.hashCode() : alarmData.getPendingIntentID(), active);

        requireContext().registerReceiver(new AlarmReceiver(), new IntentFilter());

        Log.d("####", "save: " + alarmData);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("alarm_info", alarmData);
        intent.addFlags(FLAG_INCLUDE_STOPPED_PACKAGES);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), alarmData.getPendingIntentID(), intent, 0);

        if (pendingIntent != null) {
            am.cancel(pendingIntent);
        }

        pendingIntent = PendingIntent.getBroadcast(requireContext(), alarmData.getPendingIntentID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

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

        Toast.makeText(requireContext(), "알림이 설정되었습니다", Toast.LENGTH_SHORT).show();
        try {
            findNavController(this).navigateUp();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            requireActivity().finish();
        }
    }

    private void binding() {
        if (alarmData != null) {
            hiddenSection.setVisibility(View.VISIBLE);
            active = alarmData.isActive();
            Log.d("#####", "binding: " + alarmData.isActive());
            aSwitch.setChecked(active);
            aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                active = isChecked;
                Log.d("#####", "setOnCheckedChangeListener: " + active);
            });
            deleteBtn.setOnClickListener(v -> delete());
        }

        Picasso.get().load("https://image.tmdb.org/t/p/w500" + movieData.getPosterPath()).centerInside().fit().error(R.drawable.ic_broken_image).into(poster);

        if (!movieData.getTitle().equals(""))
            title.setText(movieData.getTitle());

        SimpleDateFormat formatD = new SimpleDateFormat("yyyy-MM-dd");
        if (!movieData.getReleaseDate().equals("")) {
            try {
                releaseDate.setText(dateFormat.format(formatD.parse(movieData.getReleaseDate())));
                if (alarmData == null) {
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
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
        alert.setTitle("삭제");
        alert.setMessage("정말 삭제하시겠습니까?");
        alert.setPositiveButton("Yes", (dialog, which) -> {

            Intent intent = new Intent(requireContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), alarmData.getPendingIntentID(), intent, 0);
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

            Toast.makeText(requireContext(), "알림이 삭제되었습니다", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            findNavController(this).navigateUp();
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
