package com.lusle.soon.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lusle.soon.Adapter.AlarmSettingsAdapter;
import com.lusle.soon.Adapter.BaseRecyclerAdapter;
import com.lusle.soon.AlarmSettingActivity;
import com.lusle.soon.Model.Alarm;
import com.lusle.soon.R;
import com.lusle.soon.Utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SettingFragment extends Fragment {

    private Switch aSwitch;
    private RecyclerView recyclerView;
    private AlarmSettingsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private FrameLayout emptyViewGroup;
    private LottieAnimationView emptyAnim;

    private SharedPreferences mPrefs;
    private boolean clickedByUser = true;

    public static SettingFragment newInstance() {

        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);


        aSwitch = view.findViewById(R.id.alarm_switch);
        emptyViewGroup = view.findViewById(R.id.list_empty_view);
        emptyAnim = view.findViewById(R.id.list_empty_anim);
        recyclerView = view.findViewById(R.id.alarm_list_recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AlarmSettingsAdapter();
        adapter.setOnItemClickListener((v, pos, alarm) -> {
            Intent intent = new Intent(getContext(), AlarmSettingActivity.class);
            intent.putExtra("alarm_info", alarm);
            startActivity(intent);
        });
        adapter.setOnEmptyListener(new BaseRecyclerAdapter.OnEmptyListener() {
            @Override
            public void onEmpty() {
                recyclerView.setVisibility(View.GONE);
                emptyViewGroup.setVisibility(View.VISIBLE);
                if (!emptyAnim.isAnimating()) emptyAnim.playAnimation();
            }

            @Override
            public void onNotEmpty() {
                recyclerView.setVisibility(View.VISIBLE);
                emptyViewGroup.setVisibility(View.GONE);
                if (emptyAnim.isAnimating()) emptyAnim.pauseAnimation();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        ArrayList<Alarm> alarms = getAlarms();

        setAlarmSwitch();
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (clickedByUser) {
                ArrayList<Alarm> _alarms = getAlarms();
                for (Alarm alarm : _alarms)
                    alarm.setActive(isChecked);
                setAlarms(_alarms);
                Toast.makeText(getContext(), "알림이 " + (isChecked ? "전부 켜졌습니다" : "전부 꺼졌습니다"), Toast.LENGTH_SHORT).show();

                _alarms = getAlarms();
                adapter.setList(_alarms);
                Utils.runLayoutAnimation(recyclerView);
            }
        });

        adapter.setList(alarms);
        Utils.runLayoutAnimation(recyclerView);

        return view;
    }

    private void setAlarmSwitch() {
        clickedByUser = false;
        boolean checked = false;
        for (Alarm alarm : getAlarms()) {
            if (alarm.isActive()) {
                checked = true;
                break;
            }
        }
        aSwitch.setChecked(checked);
        clickedByUser = true;
        ArrayList<Alarm> alarms = getAlarms();
        adapter.setList(alarms);
        Utils.runLayoutAnimation(recyclerView);
    }

    private void setAlarms(ArrayList<Alarm> alarms) {
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Type type = new TypeToken<ArrayList<Alarm>>() {
        }.getType();
        String _json = new Gson().toJson(alarms, type);
        prefsEditor.putString("alarms", _json);
        prefsEditor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<Alarm> alarms = getAlarms();

        adapter.setList(alarms);
        Utils.runLayoutAnimation(recyclerView);
        setAlarmSwitch();
    }

    private ArrayList<Alarm> getAlarms() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Alarm>>() {
        }.getType();
        String json = mPrefs.getString("alarms", "");
        ArrayList<Alarm> alarms = gson.fromJson(json, type);
        if (alarms == null) return new ArrayList<>();
        return alarms;
    }


}
