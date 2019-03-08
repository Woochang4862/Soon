package com.lusle.soon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.soon.Fragments.CompanyFragment;
import com.lusle.soon.Fragments.DateFragment;
import com.lusle.soon.Fragments.GenreFragment;
import com.lusle.soon.Fragments.SettingFragment;
import com.lusle.soon.Fragments.ThisMonthMovieFragment;
import com.lusle.soon.Model.Alarm;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton mSearchFab;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    final private int requestCode = 666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        /*SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Type type = new TypeToken<ArrayList<Alarm>>() {
        }.getType();
        String json = sp.getString("alarms", "");
        ArrayList<Alarm> alarms = new Gson().fromJson(json, type);


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        for (Alarm alarm : alarms) {

            Intent updateServiceIntent = new Intent("com.lusle.soon.ALARM_START");
            PendingIntent pendingUpdateIntent = PendingIntent.getService(this, alarm.getPendingIntentID(), updateServiceIntent, 0);
            // Cancel alarms
            try {
                alarmManager.cancel(pendingUpdateIntent);
            } catch (Exception e) {
                Log.e("####", "AlarmManager update was not canceled. " + e.toString());
            }
        }*/
        init();
    }

    private void init() {
        mSearchFab = findViewById(R.id.floatingActionButton);
        mSearchFab.setOnClickListener(v -> presentActivity(v));

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = ThisMonthMovieFragment.newInstance();
                    break;
                case R.id.company:
                    selectedFragment = CompanyFragment.newInstance();
                    break;
                case R.id.genre:
                    selectedFragment = GenreFragment.newInstance();
                    break;
                case R.id.date:
                    selectedFragment = DateFragment.newInstance();
                    break;
                case R.id.settings:
                    selectedFragment = SettingFragment.newInstance();
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ThisMonthMovieFragment.newInstance());
        transaction.commit();

        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            DynamicToast.make(this, "종료하고 싶다면 한 번더 눌러주세요").show();
        }
    }

    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(SearchActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivityForResult(this, intent, requestCode, options.toBundle());
    }
}
