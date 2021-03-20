package com.lusle.android.soon.View.Main.Setting.ReleaseAlarm;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.lusle.android.soon.Adapter.ReleaseAlarmSettingsAdapter;
import com.lusle.android.soon.Model.Schema.Alarm;
import com.lusle.android.soon.Model.Source.AlarmDataLocalSource;
import com.lusle.android.soon.R;
import com.lusle.android.soon.Util.Util;
import com.lusle.android.soon.View.BaseActivity;
import com.lusle.android.soon.View.Main.Setting.ReleaseAlarm.Presenter.ReleaseAlarmSettingContractor;
import com.lusle.android.soon.View.Main.Setting.ReleaseAlarm.Presenter.ReleaseAlarmSettingPresenter;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReleaseAlarmSettingActivity extends BaseActivity implements ReleaseAlarmSettingContractor.View {

    private Switch aSwitch;
    private RecyclerView recyclerView;
    private ReleaseAlarmSettingsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private FrameLayout emptyViewGroup;
    private LottieAnimationView emptyAnim;

    private boolean clickedByUser = true;

    private ReleaseAlarmSettingPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_alarm_setting);

        presenter = new ReleaseAlarmSettingPresenter();
        presenter.attachView(this);
        presenter.setModel(AlarmDataLocalSource.getInstance(getContext()));

        aSwitch = findViewById(R.id.alarm_switch);
        emptyViewGroup = findViewById(R.id.list_empty_view);
        emptyAnim = findViewById(R.id.list_empty_anim);
        recyclerView = findViewById(R.id.alarm_list_recyclerView);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ReleaseAlarmSettingsAdapter();
        presenter.setAdapterView(adapter);
        presenter.setAdapterModel(adapter);
        presenter.setOnItemClickListener();
        presenter.setOnEmptyListener();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        setAlarmSwitch();
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (clickedByUser) {
                ArrayList<Alarm> alarms = presenter.getAlarms();
                for (Alarm alarm : alarms)
                    alarm.setActive(isChecked);
                presenter.setAlarms(alarms);
                Toast.makeText(getContext(), "알림이 " + (isChecked ? "전부 켜졌습니다" : "전부 꺼졌습니다"), Toast.LENGTH_SHORT).show();
                reload();
            }
        });

        presenter.loadItems();
        Util.runLayoutAnimation(recyclerView);
    }

    private void setAlarmSwitch() {
        clickedByUser = false;
        boolean checked = false;
        for (Alarm alarm : presenter.getAlarms()) {
            if (alarm.isActive()) {
                checked = true;
                break;
            }
        }
        aSwitch.setChecked(checked);
        clickedByUser = true;
    }

    private void reload() {
        presenter.loadItems();
        Util.runLayoutAnimation(recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
        setAlarmSwitch();
    }

    @Override
    public void setRecyclerEmpty(boolean isEmpty) {
        if (isEmpty) {
            recyclerView.setVisibility(View.GONE);
            emptyViewGroup.setVisibility(View.VISIBLE);
            if (!emptyAnim.isAnimating()) emptyAnim.playAnimation();
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyViewGroup.setVisibility(View.GONE);
            if (emptyAnim.isAnimating()) emptyAnim.pauseAnimation();
        }
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
